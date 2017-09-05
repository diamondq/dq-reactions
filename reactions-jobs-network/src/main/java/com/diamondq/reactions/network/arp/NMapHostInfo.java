package com.diamondq.reactions.network.arp;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class NMapHostInfo {

	public final Set<NMapAddress> addresses;

	public NMapHostInfo(Set<NMapAddress> pAddresses) {
		super();
		addresses = ImmutableSet.copyOf(pAddresses);
	}

	public Set<NMapAddress> getAddresses() {
		return addresses;
	}

	public String toSerializedString() {
		StringBuilder sb = new StringBuilder();
		sb.append("      addresses=[\n");
		for(NMapAddress a : addresses)
			sb.append(a.toSerializedString());
		sb.append("      ]\n");
		return sb.toString();
	}
}
