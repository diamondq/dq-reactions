package com.diamondq.reactions.network.arp;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

public class ArpRecord implements Serializable {

	private static final long	serialVersionUID	= -4016157726921880678L;

	public final InetAddress	networkAddress;

	public final Date			recordTime;

	public final String			macAddress;

	public final InetAddress	inetAddress;

	public ArpRecord(InetAddress pNetworkAddress, Date pRecordTime, String pMacAddress, InetAddress pInetAddress) {
		super();
		networkAddress = pNetworkAddress;
		recordTime = pRecordTime;
		macAddress = pMacAddress;
		inetAddress = pInetAddress;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("networkAddress", networkAddress).add("recordTime", recordTime)
			.add("macAddress", macAddress).add("inetAddress", inetAddress).toString() + "\n";
	}
}
