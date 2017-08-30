package com.diamondq.reactions.network.arp;

import com.diamondq.common.config.Config;
import com.diamondq.common.utils.parsing.xml.DOMTraversal;
import com.diamondq.common.utils.parsing.xml.Parser;
import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.info.AbstractNoParamsJobInfo;
import com.diamondq.reactions.common.process.ProcessLauncher;
import com.diamondq.reactions.common.process.ProcessLauncher.Result;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.io.StringReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

@ApplicationScoped
public class NMapPingArpScan extends AbstractNoParamsJobInfo {
	private static final Logger	sLogger	= LoggerFactory.getLogger(NMapPingArpScan.class);

	private final Set<String>	mExcludeNetworks;

	@Inject
	public NMapPingArpScan(Config pConfig) {
		@SuppressWarnings("unchecked")
		Collection<String> networks =
			pConfig.bind("reactions.network.nmap-ping-arp-scan.exclude-networks-with-ip", Collection.class);
		if (networks == null)
			networks = Collections.emptyList();
		mExcludeNetworks = ImmutableSet.copyOf(networks);
	}

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::execute).info(this)
			.result(ArpScanResult.class).state("viaNMap").build()
			.param(ProcessLauncher.class).isTransient().name("nmap").state("pingArpScan").build()
		.build());
		// @formatter:on	
	}

	public ArpScanResult execute(ProcessLauncher pNMapLauncher) {
		sLogger.debug("Execute");
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException ex) {
			throw new RuntimeException(ex);
		}

		Date startScanDate = null;

		Map<InetAddress, Collection<ArpRecord>> scanResult = new HashMap<>();

		for (; networkInterfaces.hasMoreElements();) {
			NetworkInterface ni = networkInterfaces.nextElement();
			for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
				InetAddress networkAddress = ia.getAddress();
				if (networkAddress == null)
					continue;

				/* Skip ipv6 networks (not currently supported) */

				if (networkAddress instanceof Inet6Address)
					continue;
				StringBuilder sb = new StringBuilder();
				String naStr = networkAddress.toString();
				while (naStr.startsWith("/"))
					naStr = naStr.substring(1);

				/* Skip any network that includes an IP that we've been asked to skip */

				if (mExcludeNetworks.contains(naStr) == true)
					continue;

				sb.append(naStr);
				sb.append('/');
				if (ia.getNetworkPrefixLength() < 24)
					continue;
				sb.append(ia.getNetworkPrefixLength());
				String target = sb.toString();

				Collection<ArpRecord> networkScan = scanResult.get(networkAddress);
				if (networkScan == null) {
					networkScan = Lists.newArrayList();
					scanResult.put(networkAddress, networkScan);
				}

				/* Run the command */

				Result result = pNMapLauncher.launch(Collections.singletonMap("TARGET", target), null);
				if (result.code != 0) {
					sLogger.warn("Unable to get the arp data");
					throw new IllegalArgumentException("Unable to get the arp data");
				}

				String msg = new String(result.data).trim();

				sLogger.trace("Result of ARP: {}", msg);

				Document nmapDoc =
					Parser.parse(new InputSource(new StringReader(new String(result.data, Charsets.UTF_8))));
				Attr nmapElem =
					DOMTraversal.traverseRequiredSingle(nmapDoc, Attr.class, null, "nmaprun", null, "start");
				String startStr = DOMTraversal.getValue(nmapElem);
				Date date;
				try {
					long start = Long.parseLong(startStr) * 1000L;
					date = new Date(start);
				}
				catch (NumberFormatException ex) {
					sLogger.warn("Unable to parse the nmaprun/@start to a long: {}. Continuing with the current date",
						startStr);
					date = new Date();
				}

				if (startScanDate == null)
					startScanDate = date;

				/* Now find all the host objects */

				List<Element> hosts = DOMTraversal.traverse(nmapDoc, Element.class, null, "nmaprun", null, "host");
				for (Element host : hosts) {

					/* Make sure the host is up */

					Attr upAttr = DOMTraversal.traverseSingle(host, Attr.class, null, "status", null, "state");
					if (upAttr == null)
						continue;
					String upStr = DOMTraversal.getValue(upAttr);
					if ("up".equals(upStr) == false)
						continue;

					/* Get the ip address */

					InetAddress hostAddress = null;
					String macAddress = null;
					for (Element address : DOMTraversal.traverse(host, Element.class, null, "address")) {
						String addrType = DOMTraversal.getAttributeValue(address, null, "addrtype");
						if (("ipv4".equals(addrType) == true) || ("ipv6".equals(addrType) == true)) {
							String addr = DOMTraversal.getAttributeValue(address, null, "addr");
							try {
								hostAddress = InetAddress.getByName(addr);
							}
							catch (UnknownHostException ex) {
								continue;
							}
						}
						else if ("mac".equals(addrType) == true) {
							macAddress = DOMTraversal.getAttributeValue(address, null, "addr");
						}
					}

					/* If there wasn't both a host address and a MAC, then it's not valid */

					if ((hostAddress == null) || (macAddress == null))
						continue;

					/* We have a record. */

					ArpRecord r = new ArpRecord(networkAddress, date, macAddress, hostAddress);
					networkScan.add(r);
				}
			}
		}

		sLogger.debug("Result of NMap Parsing: {}", scanResult);

		if (startScanDate == null)
			startScanDate = new Date();
		return new ArpScanResult(startScanDate, scanResult);
	}

}
