package com.diamondq.reactions.common.process;

public interface ProcessWhereIsCommand {

	/**
	 * Returns a launcher for determining where a given command is. NOTE: The launcher has a variable called COMMAND for
	 * the command to search
	 * 
	 * @return the ProcessLauncher
	 */
	public ProcessLauncher getLauncher();

}
