package com.diamondq.reactions.network.arp;

import com.google.common.collect.ImmutableSet;

import java.util.Date;
import java.util.Set;

public class NMapResult {

	public final Date				scanDate;

	public final Set<NMapHostInfo>	hosts;

	public NMapResult(Date pScanDate, Set<NMapHostInfo> pHosts) {
		super();
		scanDate = pScanDate;
		hosts = ImmutableSet.copyOf(pHosts);
	}

	public String toSerializedString() {
		StringBuilder sb = new StringBuilder();
		sb.append("NMapResult(\n");
		sb.append("   scanDate=");
		sb.append(scanDate.toString());
		sb.append("\n   hosts=[\n");
		for (NMapHostInfo h : hosts)
			sb.append(h.toSerializedString());
		sb.append("   ]\n");
		sb.append(")\n");
		return sb.toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toSerializedString();
	}
}
