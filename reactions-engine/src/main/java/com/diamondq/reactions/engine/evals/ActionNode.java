package com.diamondq.reactions.engine.evals;

import com.diamondq.reactions.api.Action;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ActionNode {

	private static final String						sUNDEFINED	= "__UNDEFINED__";

	public final Action								action;

	private final ConcurrentMap<String, TypeNode>	types;

	public ActionNode(Action pAction) {
		action = pAction;
		types = new ConcurrentHashMap<>();
	}

	public TypeNode getOrAddType(String pType) {
		if (pType.startsWith("__") == true)
			throw new IllegalArgumentException("Types cannot start with __ -> " + pType);
		return internalGetOrAddType(pType);
	}

	public TypeNode getOrAddUndefined() {
		return internalGetOrAddType(sUNDEFINED);
	}

	private TypeNode internalGetOrAddType(String pType) {
		TypeNode typeNode = types.get(pType);
		if (typeNode != null)
			return typeNode;
		TypeNode newTypeNode = new TypeNode(pType);
		if ((typeNode = types.putIfAbsent(pType, newTypeNode)) == null)
			typeNode = newTypeNode;
		return typeNode;
	}

	public @Nullable TypeNode getType(String pType) {
		return types.get(pType);
	}

	public @Nullable TypeNode getUndefined() {
		return types.get(sUNDEFINED);
	}
}
