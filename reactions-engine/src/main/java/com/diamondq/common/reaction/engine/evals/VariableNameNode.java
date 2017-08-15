package com.diamondq.common.reaction.engine.evals;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class VariableNameNode extends NameNode {

	private final ConcurrentMap<String, NameNode> mByVariableName;

	public VariableNameNode() {
		super("__UNKNOWN__");
		mByVariableName = Maps.newConcurrentMap();
	}

	public Set<Map.Entry<String, NameNode>> getByVariableNames() {
		return mByVariableName.entrySet();
	}

	public NameNode getOrAddVariable(String pNameByVariable) {
		NameNode nameNode = mByVariableName.get(pNameByVariable);
		if (nameNode == null) {
			NameNode newNameNode = new NameNode(pNameByVariable);
			if ((nameNode = mByVariableName.putIfAbsent(pNameByVariable, newNameNode)) == null)
				nameNode = newNameNode;
		}
		return nameNode;
	}
}
