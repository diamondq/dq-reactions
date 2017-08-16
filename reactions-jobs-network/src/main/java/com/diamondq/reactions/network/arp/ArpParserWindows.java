package com.diamondq.reactions.network.arp;

import com.diamondq.reactions.api.ConfigureReaction;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.network.arp.ArpParser.ArpParserParams;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ArpParserWindows {

	@Inject
	public ArpParserWindows() {

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

		ImmutableMap.Builder<InetAddress, Collection<ArpRecord>> interfaceBuilder = ImmutableMap.builder();

		try {
			try (StringReader sr = new StringReader(pMsg)) {
				try (BufferedReader br = new BufferedReader(sr)) {

					boolean inInterface = false;
					InetAddress interfaceAddress = null;
					ImmutableSet.Builder<ArpRecord> recordBuilder = ImmutableSet.builder();
					String line;
					while ((line = br.readLine()) != null) {
						if (inInterface == false) {
							if (line.startsWith("Interface:")) {
								line = line.substring("Interface:".length()).trim();
								int offset = line.indexOf(' ');
								if (offset != -1)
									line = line.substring(0, offset);
								interfaceAddress = InetAddress.getByName(line);
								inInterface = true;
							}
						}
						else {
							line = line.trim();
							if (line.startsWith("Internet"))
								continue;
							if (line.isEmpty()) {
								inInterface = false;
								interfaceAddress = null;
								continue;
							}
							int offset = line.indexOf(' ');
							if (offset == -1)
								continue;
							InetAddress address = InetAddress.getByName(line.substring(0, offset));
							line = line.substring(offset + 1).trim();
							offset = line.indexOf(' ');
							if (offset == -1)
								continue;
							String macAddress = line.substring(0, offset).toLowerCase();
							if (interfaceAddress == null)
								throw new IllegalStateException("The interfaceAddress shouldn't be null at this point");
							recordBuilder.add(new ArpRecord(interfaceAddress, pDate, macAddress, address));
						}
					}
					ImmutableSet<ArpRecord> recordSet = recordBuilder.build();
					if ((interfaceAddress != null) && (recordSet.isEmpty() == false))
						interfaceBuilder.put(interfaceAddress, recordSet);
				}
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return interfaceBuilder.build();

	}
}
