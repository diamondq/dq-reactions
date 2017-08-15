package com.diamondq.common.reaction.api.info;

import com.diamondq.common.reaction.api.JobInfo;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractNoParamsJobInfo implements JobInfo<@Nullable Void, NoParamsBuilder> {

	/**
	 * @see com.diamondq.common.reaction.api.JobInfo#newParamsBuilder()
	 */
	@Override
	public NoParamsBuilder newParamsBuilder() {
		return new NoParamsBuilder();
	}

}
