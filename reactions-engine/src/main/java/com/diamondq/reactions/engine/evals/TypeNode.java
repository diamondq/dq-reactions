package com.diamondq.reactions.engine.evals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TypeNode {

	private static final String						sWITH_VARIABLES	= "__VARIABLES__";

	private static final String						sUNDEFINED		= "__UNDEFINED__";

	public final String								type;

	private final ConcurrentMap<String, NameNode>	mNames;

	public TypeNode(String pType) {
		type = pType;
		mNames = new ConcurrentHashMap<>();
	}

	private NameNode internalGetOrAddName(String pName) {
		NameNode nameNode = mNames.get(pName);
		if (nameNode != null)
			return nameNode;
		NameNode newNameNode = new NameNode(pName);
		if ((nameNode = mNames.putIfAbsent(pName, newNameNode)) == null)
			nameNode = newNameNode;
		return nameNode;
	}

	public NameNode getOrAddName(String pName) {
		if (pName.startsWith("__") == true)
			throw new IllegalArgumentException("Names cannot start with __ -> " + pName);
		return internalGetOrAddName(pName);
	}

	public NameNode getOrAddWithVariables() {
		return internalGetOrAddName(sWITH_VARIABLES);
	}

	public NameNode getOrAddUndefined() {
		return internalGetOrAddName(sUNDEFINED);
	}

	public @Nullable NameNode getName(String pName) {
		return mNames.get(pName);
	}

	public @Nullable NameNode getWithVariables() {
		return mNames.get(sWITH_VARIABLES);
	}

	public @Nullable NameNode getUndefined() {
		return mNames.get(sUNDEFINED);
	}

}
