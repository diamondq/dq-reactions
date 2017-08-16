package com.diamondq.reactions.common.process;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessWhereIsCommandLinux implements ProcessWhereIsCommand {

	/**
	 * @see com.diamondq.reactions.common.process.ProcessWhereIsCommand#getLauncher()
	 */
	@Override
	public ProcessLauncher getLauncher() {
		return new ProcessLauncher(ProcessLauncher.constant("which"), ProcessLauncher.variable("COMMAND"));
	}

}
