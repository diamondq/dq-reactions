package com.diamondq.reactions.network.arp;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class ArpScanResult {

	public final Date										scanDate;

	public final Map<InetAddress, Collection<ArpRecord>>	results;

	public ArpScanResult(Date pScanDate, Map<InetAddress, Collection<ArpRecord>> pResults) {
		super();
		scanDate = pScanDate;
		results = pResults;
	}

}
