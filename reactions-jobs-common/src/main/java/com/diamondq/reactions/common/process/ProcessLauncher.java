package com.diamondq.reactions.common.process;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ProcessLauncher represents the commands necessary to execute a process
 */
public class ProcessLauncher {

	private static final Logger sLogger = LoggerFactory.getLogger(ProcessLauncher.class);

	/**
	 * Each argument of the command is a subclass of ProcessArgument
	 */
	public abstract static class ProcessArgument {

	}

	/**
	 * Represents the Result of running the ProcessLauncher
	 */
	public static class Result {
		/**
		 * The exit code of the process. 0 is traditionally a success
		 */
		public final int	code;

		/**
		 * The data from the Standard Out of the process
		 */
		public final byte[]	data;

		public Result(int pCode, byte[] pData) {
			code = pCode;
			data = pData;
		}

	}

	/**
	 * Represents a Constant Argument
	 */
	private static class ConstantArgument extends ProcessArgument {
		public final String constant;

		public ConstantArgument(String pConstant) {
			constant = pConstant;
		}
	}

	/**
	 * Represents a Variable Argument
	 */
	private static class VariableArgument extends ProcessArgument {
		public final String variable;

		public VariableArgument(String pVariable) {
			variable = pVariable;
		}
	}

	/**
	 * The list of arguments for this launcher
	 */
	private final List<@NonNull ProcessArgument> mArguments;

	/**
	 * Simple constructor that just takes a single constant argument
	 * 
	 * @param pSingleCommand the constant argument (NOTE: Must be a single argument, not a white separated set of
	 *            arguments)
	 */
	public ProcessLauncher(String pSingleCommand) {
		mArguments = ImmutableList.<@NonNull ProcessArgument> builder().add(constant(pSingleCommand)).build();
	}

	/**
	 * Constructor that takes an array of ProcessArgument's
	 * 
	 * @param pArguments the arguments
	 */
	public ProcessLauncher(@NonNull ProcessArgument... pArguments) {
		mArguments = ImmutableList.<@NonNull ProcessArgument> builder().addAll(Arrays.asList(pArguments)).build();
	}

	/**
	 * Helper that returns a ProcessArgument for a constant
	 * 
	 * @param pConstant the constant
	 * @return the ProcessArgument
	 */
	public static ProcessArgument constant(String pConstant) {
		return new ConstantArgument(pConstant);
	}

	/**
	 * Helper that returns a ProcessArgument for a variable
	 * 
	 * @param pVariable the variable name
	 * @return the ProcessArgument
	 */
	public static ProcessArgument variable(String pVariable) {
		return new VariableArgument(pVariable);
	}

	/**
	 * Gets the list of arguments
	 * 
	 * @return the arguments
	 */
	public List<@NonNull ProcessArgument> getArguments() {
		return mArguments;
	}

	/**
	 * Resolves the arguments into a String list.
	 * 
	 * @param pVariableMap the map of data to be used for variable substitution. If there are any variables in the
	 *            arguments that are not resolved by this Map, then an exception will be thrown.
	 * @param pExtraArguments an extra arguments to add to the end
	 * @return the String list
	 */
	public List<String> resolve(@Nullable Map<String, String> pVariableMap, @Nullable List<String> pExtraArguments) {
		List<String> arguments = Lists.newArrayList();
		for (ProcessArgument arg : mArguments) {
			if (arg instanceof ConstantArgument)
				arguments.add(((ConstantArgument) arg).constant);
			else if (arg instanceof VariableArgument) {
				String variable = pVariableMap == null ? null : pVariableMap.get(((VariableArgument) arg).variable);
				if (variable == null)
					throw new IllegalStateException(
						"The process " + getShortName() + " can't execute because the variable "
							+ ((VariableArgument) arg).variable + " can't be found");
				arguments.add(variable);
			}
		}
		if (pExtraArguments != null)
			arguments.addAll(pExtraArguments);
		return arguments;
	}

	/**
	 * Helper function that flattens the list to a single string.
	 * 
	 * @param pArguments the arguments
	 * @return the string
	 */
	public static String flatten(List<String> pArguments) {
		return String.join(" ", pArguments);
	}

	/**
	 * Creates a new ProcessLauncher that is based on this launcher.
	 * 
	 * @param pVariableMap if provided, any variables in this map will replace variables in the launcher. However, it is
	 *            ok to not provide all the necessary variables. In that case, the variables are carried over to the new
	 *            launcher.
	 * @param pExtraArguments extra arguments to be added to the end
	 * @return the new ProcessLauncher
	 */
	public ProcessLauncher extend(@Nullable Map<String, String> pVariableMap,
		@NonNull ProcessArgument @Nullable... pExtraArguments) {
		List<ProcessArgument> newArguments = Lists.newArrayList();
		for (ProcessArgument arg : mArguments) {
			if (arg instanceof ConstantArgument)
				newArguments.add(arg);
			else if (arg instanceof VariableArgument) {
				if (pVariableMap == null)
					newArguments.add(arg);
				else {
					String variableValue = pVariableMap.get(((VariableArgument) arg).variable);
					if (variableValue == null)
						newArguments.add(arg);
					else
						newArguments.add(constant(variableValue));
				}
			}
		}
		if (pExtraArguments != null)
			for (ProcessArgument arg : pExtraArguments)
				newArguments.add(arg);
		return new ProcessLauncher(newArguments.toArray(new @NonNull ProcessArgument[0]));
	}

	/**
	 * Launches the ProcessLauncher and gets a Result
	 * 
	 * @param pVariableMap the map of data to be used for variable substitution. If there are any variables in the
	 *            arguments that are not resolved by this Map, then an exception will be thrown.
	 * @param pExtraArguments an extra arguments to add to the end
	 * @return the Result
	 */
	public Result launch(@Nullable Map<String, String> pVariableMap, @Nullable List<String> pExtraArguments) {

		List<String> arguments = resolve(pVariableMap, pExtraArguments);
		sLogger.debug("Launching {}", arguments);
		ProcessBuilder pb = new ProcessBuilder(arguments);
		pb = pb.redirectErrorStream(true);
		Process process;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[32000];
		try {
			process = pb.start();
			InputStream stream = process.getInputStream();
			int bytesRead;
			while ((bytesRead = stream.read(buffer)) != -1)
				baos.write(buffer, 0, bytesRead);
			boolean waiting = true;
			Integer result = 0;
			while (waiting == true) {
				try {
					result = process.waitFor();
					waiting = false;
				}
				catch (InterruptedException ex) {
				}
			}
			baos.flush();
			baos.close();
			byte[] bytes = baos.toByteArray();
			if (bytes == null)
				throw new IllegalStateException();
			return new Result(result, bytes);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	/**
	 * Returns a short name that represents this launcher. Usually used for debugging purposes
	 * 
	 * @return the short name
	 */
	public String getShortName() {
		StringBuilder sb = new StringBuilder();
		sb.append("ProcessLauncher(");
		for (ProcessArgument arg : mArguments) {
			if (arg instanceof ConstantArgument)
				sb.append(((ConstantArgument) arg).constant);
			else if (arg instanceof VariableArgument)
				sb.append(((VariableArgument) arg).variable);
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getShortName();
	}
}
