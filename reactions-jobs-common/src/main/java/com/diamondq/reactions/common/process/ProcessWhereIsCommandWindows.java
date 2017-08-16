package com.diamondq.reactions.common.process;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessWhereIsCommandWindows implements ProcessWhereIsCommand {

	/**
	 * @see com.diamondq.reactions.common.process.ProcessWhereIsCommand#getLauncher()
	 */
	@Override
	public ProcessLauncher getLauncher() {
		return new ProcessLauncher(ProcessLauncher.constant("CMD"), ProcessLauncher.constant("/C"),
			ProcessLauncher.constant("where"), ProcessLauncher.variable("COMMAND"));
	}

}
