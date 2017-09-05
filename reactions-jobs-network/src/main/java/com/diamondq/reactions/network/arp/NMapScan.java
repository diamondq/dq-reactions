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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

@ApplicationScoped
public class NMapScan extends AbstractNoParamsJobInfo {
	private static final Logger sLogger = LoggerFactory.getLogger(NMapScan.class);

	@Inject
	public NMapScan(Config pConfig) {
	}

	@ConfigureReaction
	public void setup(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder()
			.method(this::execute).info(this)
			.result(NMapResult.class).build()
			.variable(String.class, "nmap-targets").valueByResultStateValue("nmap-targets").build()
			.variable(String.class, "nmap-discovery").valueByResultStateValue("nmap-discovery").build()
			.param(ProcessLauncher.class).isStored().name("nmap").state("base-launcher").build()
			.param(String.class).valueByVariable("nmap-discovery").build()
			.param(String.class).valueByVariable("nmap-targets").build()
		.build());
		// @formatter:on
	}

	public NMapResult execute(ProcessLauncher pNMapLauncher, String pDiscovery, String pTargets) {
		sLogger.debug("Execute");

		/* If there are no targets, then there's no result */

		if (pTargets.trim().isEmpty() == true)
			return new NMapResult(new Date(), Collections.emptySet());

		/* Run the command */

		ImmutableList.Builder<String> argBuilder = ImmutableList.builder();
		argBuilder.add(pDiscovery.split(" "));
		argBuilder.add(pTargets.split(" "));
		List<String> args = argBuilder.build();
		Result processResult = pNMapLauncher.launch(null, args);
		if (processResult.code != 0) {
			sLogger.warn("Unable to get the nmap data");
			throw new IllegalArgumentException("Unable to get the nmap data");
		}

		String msg = new String(processResult.data).trim();

		sLogger.trace("Result of NMap: {}", msg);

		Document nmapDoc =
			Parser.parse(new InputSource(new StringReader(new String(processResult.data, Charsets.UTF_8))));
		Attr nmapElem = DOMTraversal.traverseRequiredSingle(nmapDoc, Attr.class, null, "nmaprun", null, "start");
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

		/* Now find all the host objects */

		List<Element> hosts = DOMTraversal.traverse(nmapDoc, Element.class, null, "nmaprun", null, "host");
		ImmutableSet.Builder<NMapHostInfo> hostBuilder = ImmutableSet.builder();
		for (Element host : hosts) {

			/* Make sure the host is up */

			Attr upAttr = DOMTraversal.traverseSingle(host, Attr.class, null, "status", null, "state");
			if (upAttr == null)
				continue;
			String upStr = DOMTraversal.getValue(upAttr);
			if ("up".equals(upStr) == false)
				continue;

			/* Get the addresses */

			ImmutableSet.Builder<NMapAddress> addressBuilder = ImmutableSet.builder();
			for (Element addressElem : DOMTraversal.traverse(host, Element.class, null, "address")) {
				String addressType = DOMTraversal.getAttributeValue(addressElem, null, "addrtype");
				String address = DOMTraversal.getAttributeValue(addressElem, null, "addr");
				if ((addressType != null) && (address != null))
					addressBuilder.add(new NMapAddress(address, addressType));
			}

			/* We have a record. */

			hostBuilder.add(new NMapHostInfo(addressBuilder.build()));
		}

		NMapResult result = new NMapResult(date, hostBuilder.build());
		sLogger.debug("Result of NMap Parsing: {}", result);

		return result;
	}

}
