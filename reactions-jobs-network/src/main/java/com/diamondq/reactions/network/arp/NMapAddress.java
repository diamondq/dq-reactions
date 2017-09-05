package com.diamondq.reactions.network.arp;

public class NMapAddress {

	public final String	address;

	public final String	type;

	public NMapAddress(String pAddress, String pType) {
		address = pAddress;
		type = pType;
	}

	public String getType() {
		return type;
	}

	public String getAddress() {
		return address;
	}

	public String toSerializedString() {
		StringBuilder sb = new StringBuilder();
		sb.append("         ");
		sb.append(type);
		sb.append(" = ");
		sb.append(address);
		sb.append("\n");
		return sb.toString();
	}

}
