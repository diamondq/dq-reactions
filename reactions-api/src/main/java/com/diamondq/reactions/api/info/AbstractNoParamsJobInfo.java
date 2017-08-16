package com.diamondq.reactions.api.info;

import com.diamondq.reactions.api.JobInfo;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractNoParamsJobInfo implements JobInfo<@Nullable Void, NoParamsBuilder> {

	/**
	 * @see com.diamondq.reactions.api.JobInfo#newParamsBuilder()
	 */
	@Override
	public NoParamsBuilder newParamsBuilder() {
		return new NoParamsBuilder();
	}

}
