package com.diamondq.common.reaction.engine;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DataObject<T> {

	private @Nullable T			mObject;

	private @Nullable String	mName;

	private Map<String, String>	mStateMap;

	public DataObject() {
		mStateMap = new HashMap<>();
	}

	public @Nullable T getObject() {
		return mObject;
	}

	public void setObject(T pObject) {
		mObject = pObject;
	}

	public @Nullable String getName() {
		return mName;
	}

	public void setName(String pName) {
		mName = pName;
	}

	public Map<String, String> getStateMap() {
		return mStateMap;
	}

}
