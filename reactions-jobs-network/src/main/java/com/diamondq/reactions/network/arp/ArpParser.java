package com.diamondq.reactions.network.arp;

import com.diamondq.reactions.api.JobInfo;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.network.arp.ArpParser.ArpParserParams;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ArpParser implements JobInfo<Map<InetAddress, Collection<ArpRecord>>, ArpParserParams> {

	public static class ArpParserParams implements JobParamsBuilder {

		private @Nullable Date		mDate;

		private @Nullable String	mMessage;

		public ArpParserParams date(Date pDate) {
			mDate = pDate;
			return this;
		}

		public ArpParserParams message(String pMessage) {
			mMessage = pMessage;
			return this;
		}

		public @Nullable Date getDate() {
			return mDate;
		}

		public @Nullable String getMessage() {
			return mMessage;
		}

		/**
		 * @see com.diamondq.reactions.api.JobParamsBuilder#setParam(java.lang.String, java.lang.Object)
		 */
		@Override
		public void setParam(String pKey, Object pValue) {
			if (pKey.equals("date"))
				mDate = (Date) pValue;
			else if (pKey.equals("message"))
				mMessage = (String) pValue;
			else
				throw new IllegalArgumentException("Unrecognized param key: " + pKey);
		}
	}

	/**
	 * @see com.diamondq.reactions.api.JobInfo#newParamsBuilder()
	 */
	@Override
	public ArpParserParams newParamsBuilder() {
		return new ArpParserParams();
	}
}
