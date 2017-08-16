package com.diamondq.reactions.network.arp;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.network.arp.ArpParser.ArpParserParams;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.weld.util.collections.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ArpParserUnix {
	private static final Logger sLogger = LoggerFactory.getLogger(ArpParserUnix.class);

	@Inject
	public ArpParserUnix() {

	}

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::parse).info(new ArpParser())
			.result(Map.class).build()
			.param(Date.class).valueByInput(ArpParserParams::getDate).build()
			.param(String.class).valueByInput(ArpParserParams::getMessage).build()
		.build());
		// @formatter:on
	}

	public Map<InetAddress, Collection<ArpRecord>> parse(Date pDate, String pMsg) {

		Pattern pattern = Pattern.compile("^.*\\(([^\\)]+)\\)\\s+at\\s+(\\S+)\\s+\\[[^\\]]+\\]\\s+on\\s+(\\S+).*$");

		Multimap<InetAddress, ArpRecord> arpMap = LinkedListMultimap.create();
		try {
			try (StringReader sr = new StringReader(pMsg)) {
				try (BufferedReader br = new BufferedReader(sr)) {
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						Matcher m = pattern.matcher(line);
						if (m.matches() == false) {
							sLogger.warn("ARP Line doesn't match: {}", line);
							continue;
						}
						InetAddress address = InetAddress.getByName(m.group(1));
						String macAddress = m.group(2);
						if (macAddress == null) {
							sLogger.warn("ARP Line no MAC Address: {}", line);
							continue;
						}
						String networkName = m.group(3);
						if (networkName == null) {
							sLogger.warn("ARP Line no Network Name: {}", line);
							continue;
						}
						NetworkInterface ni = NetworkInterface.getByName(networkName);
						if (ni == null) {
							sLogger.warn("ARP Line Unable to find Network Interface {} : {}", networkName, line);
							continue;
						}
						@Nullable
						InetAddress interfaceAddress = null;
						for (Enumeration<InetAddress> e = ni.getInetAddresses(); e.hasMoreElements();) {
							InetAddress ia = e.nextElement();
							if (ia instanceof Inet4Address == false)
								continue;
							interfaceAddress = ia;
							break;
						}
						if (interfaceAddress == null) {
							sLogger.warn("ARP Line Unable to find InetAddress of Network Interface {} : {}",
								networkName, line);
							continue;
						}
						arpMap.put(interfaceAddress, new ArpRecord(interfaceAddress, pDate, macAddress, address));
					}
				}
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		ImmutableMap.Builder<InetAddress, Collection<ArpRecord>> interfaceBuilder = ImmutableMap.builder();
		for (InetAddress key : arpMap.keySet()) {
			Collection<@NonNull ArpRecord> collection = arpMap.get(key);
			interfaceBuilder.put(key, ImmutableList.<@NonNull ArpRecord> copyOf(collection));
		}
		return interfaceBuilder.build();

	}
}
