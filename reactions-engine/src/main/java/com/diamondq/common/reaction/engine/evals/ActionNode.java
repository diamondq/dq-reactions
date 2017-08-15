package com.diamondq.common.reaction.engine.evals;

import com.diamondq.common.reaction.api.Action;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ActionNode {

	public final Action								action;

	private final ConcurrentMap<String, TypeNode>	types;

	public ActionNode(Action pAction) {
		action = pAction;
		types = new ConcurrentHashMap<>();
	}

	public TypeNode getOrAddType(String pType) {
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
}
