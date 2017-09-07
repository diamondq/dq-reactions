package com.diamondq.reactions.engine;

import com.diamondq.common.config.Config;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.reactions.api.Action;
import com.diamondq.reactions.api.JobContext;
import com.diamondq.reactions.api.JobDefinition;
import com.diamondq.reactions.api.JobInfo;
import com.diamondq.reactions.api.JobParamsBuilder;
import com.diamondq.reactions.api.ReactionsEngine;
import com.diamondq.reactions.api.ReactionsEngineInitializedEvent;
import com.diamondq.reactions.api.errors.AbstractReactionsException;
import com.diamondq.reactions.api.errors.AbstractReactionsNotErrorException;
import com.diamondq.reactions.api.errors.ReactionsMissingDependentException;
import com.diamondq.reactions.api.impl.StateCriteria;
import com.diamondq.reactions.api.impl.StateValueCriteria;
import com.diamondq.reactions.api.impl.StateValueVariableCriteria;
import com.diamondq.reactions.api.impl.StateVariableCriteria;
import com.diamondq.reactions.engine.definitions.DependentDefinition;
import com.diamondq.reactions.engine.definitions.JobDefinitionImpl;
import com.diamondq.reactions.engine.definitions.ParamDefinition;
import com.diamondq.reactions.engine.definitions.ResultDefinition;
import com.diamondq.reactions.engine.definitions.TriggerDefinition;
import com.diamondq.reactions.engine.definitions.VariableDefinition;
import com.diamondq.reactions.engine.evals.ActionNode;
import com.diamondq.reactions.engine.evals.NameNode;
import com.diamondq.reactions.engine.evals.TypeNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jodah.typetools.TypeResolver;

@ApplicationScoped
public class EngineImpl implements ReactionsEngine {

	private static final Logger									sLogger				=
		LoggerFactory.getLogger(EngineImpl.class);

	private static final String									sPERSISTENT_STATE	= "persistent";

	private final Store											mPersistentStore;

	private final Store											mTransientStore;

	private final CopyOnWriteArraySet<JobDefinitionImpl>		mJobs;

	private final ConcurrentMap<String, JobDefinitionImpl>		mJobByName;

	private final ConcurrentMap<Class<?>, JobDefinitionImpl>	mJobByInfoClass;

	/* Trigger tree */

	private final ConcurrentMap<String, ActionNode>				mTriggers;

	private final ConcurrentMap<String, TypeNode>				mResultTree;

	private final CDIObservingExtension							mObservations;

	private final ExecutorService								mExecutorService;

	private final Event<ReactionsEngineInitializedEvent>		mInitializedEvent;

	@Inject
	public EngineImpl(Toolkit pToolkit, Config pConfig, ExecutorService pExecutorService,
		CDIObservingExtension pExtension, Event<ReactionsEngineInitializedEvent> pInitializedEvent) {
		mTriggers = Maps.newConcurrentMap();
		mResultTree = Maps.newConcurrentMap();
		mExecutorService = pExecutorService;
		mObservations = pExtension;
		mJobByName = Maps.newConcurrentMap();
		mJobs = Sets.newCopyOnWriteArraySet();
		mJobByInfoClass = Maps.newConcurrentMap();
		mInitializedEvent = pInitializedEvent;

		/* Persistent */

		String persistentScopeName = pConfig.bind("reaction.engine.scopes.persistent", String.class);
		if (persistentScopeName == null)
			throw new IllegalArgumentException("The config key reaction.engine.scopes.persistent is mandatory");

		mPersistentStore = new Store(pToolkit, persistentScopeName);

		/* Transient */

		String transientScopeName = pConfig.bind("reaction.engine.scopes.transient", String.class);
		if (transientScopeName == null)
			throw new IllegalArgumentException("The config key reaction.engine.scopes.transient is mandatory");

		mTransientStore = new Store(pToolkit, transientScopeName);

	}

	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

		sLogger.debug("Initializing all Reaction jobs...");

		/* Process each possible job configuration */

		JobContext jc = new JobContextImpl(this);
		for (CDIObservingExtension.MethodSupplierPair<?> pair : mObservations.getJobs()) {

			setupClass(jc, pair);

		}

		sLogger.debug("Firing ReactionsEngineInitializedEvent...");

		mInitializedEvent.fire(new ReactionsEngineInitializedEvent());
	}

	/**
	 * @see com.diamondq.reactions.api.ReactionsEngine
	 */
	@Override
	public <T extends JobInfo<?, ? extends JobParamsBuilder>> T findMandatoryJob(Class<T> pJobInfoClass) {
		JobDefinitionImpl jobDef = mJobByInfoClass.get(pJobInfoClass);
		if (jobDef == null)
			throw new IllegalArgumentException("The provided job class " + pJobInfoClass.getName() + " can't be found");
		if (pJobInfoClass.isInstance(jobDef.jobInfo) == false)
			throw new IllegalArgumentException(
				"The provided job class " + pJobInfoClass.getName() + " isn't the same as the registered one");
		@SuppressWarnings("unchecked")
		T result = (T) jobDef.jobInfo;
		return result;
	}

	private <C> void setupClass(JobContext pContext, CDIObservingExtension.MethodSupplierPair<C> pair) {
		Supplier<C> supplier = pair.supplier.apply(pair.clazz);
		C obj = supplier.get();
		try {
			pair.method.invoke(obj, pContext);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void registerJob(JobDefinition pDefinition) {
		if (pDefinition instanceof JobDefinitionImpl == false)
			throw new IllegalArgumentException("Only JobDefinitionImpl is supported");

		JobDefinitionImpl definition = (JobDefinitionImpl) pDefinition;

		sLogger.debug("Registering job {}", definition.getShortName());

		mJobs.add(definition);

		if (definition.name != null)
			mJobByName.putIfAbsent(definition.name, definition);

		if (definition.jobInfo != null)
			mJobByInfoClass.putIfAbsent(definition.jobInfo.getClass(), definition);

		/* Calculate the required states to add to each branch */

		boolean valueByResultName = false;
		ImmutableSet.Builder<StateCriteria> builder = ImmutableSet.builder();
		for (VariableDefinition<?> variable : definition.variables) {
			if (variable.valueByResultName == true)
				valueByResultName = true;
			String valueByResultStateValue = variable.valueByResultStateValue;
			if ((valueByResultStateValue != null))
				builder.add(new StateValueVariableCriteria(valueByResultStateValue, variable.variableName, true));
		}
		Set<StateCriteria> variableRequiredStates = builder.build();

		/* Register against the trigger tree */

		for (TriggerDefinition<?> td : definition.triggers) {
			String actionName = td.action.name();
			ActionNode actionNode = mTriggers.get(actionName);
			if (actionNode == null) {
				ActionNode newActionNode = new ActionNode(td.action);
				if ((actionNode = mTriggers.putIfAbsent(actionName, newActionNode)) == null)
					actionNode = newActionNode;
			}
			String type = td.clazz.getName();
			TypeNode typeNode = actionNode.getOrAddType(type);
			String name = td.name;
			NameNode nameNode;
			if (name == null)
				nameNode = typeNode.getOrAddUndefined();
			else
				nameNode = typeNode.getOrAddName(name);
			Set<StateCriteria> requiredStates =
				ImmutableSet.<StateCriteria> builder().addAll(variableRequiredStates).addAll(td.requiredStates).build();
			nameNode.addCriteria(requiredStates, definition);
		}

		/* Register against the result tree */

		for (ResultDefinition<?> rd : definition.results) {
			String type = rd.clazz.getName();
			TypeNode typeNode = mResultTree.get(type);
			if (typeNode == null) {
				TypeNode newTypeNode = new TypeNode(type);
				if ((typeNode = mResultTree.putIfAbsent(type, newTypeNode)) == null)
					typeNode = newTypeNode;
			}
			Set<StateCriteria> requiredStates =
				ImmutableSet.<StateCriteria> builder().addAll(variableRequiredStates).addAll(rd.requiredStates).build();
			String name = rd.name;
			if (name != null) {
				NameNode nameNode = typeNode.getOrAddName(name);
				nameNode.addCriteria(requiredStates, definition);
			}
			if (valueByResultName == true) {
				NameNode nameNode = typeNode.getOrAddWithVariables();
				nameNode.addCriteria(requiredStates, definition);
			}
			NameNode nameNode = typeNode.getOrAddUndefined();
			nameNode.addCriteria(requiredStates, definition);
		}
	}

	/**
	 * @see com.diamondq.reactions.api.ReactionsEngine#submit(java.lang.Class)
	 */
	@Override
	public <T> @NonNull ExtendedCompletableFuture<T> submit(@NonNull Class<T> pResultClass) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.reactions.api.ReactionsEngine#submit(com.diamondq.reactions.api.JobInfo,
	 *      com.diamondq.reactions.api.JobParamsBuilder)
	 */
	@Override
	@SuppressWarnings("null")
	public <RESULT, JPB extends JobParamsBuilder, T extends JobInfo<RESULT, JPB>> ExtendedCompletableFuture<RESULT> submit(
		T pJob, JPB pBuilder) {
		JobDefinitionImpl definition = mJobByInfoClass.get(pJob.getClass());
		if (definition == null) {
			sLogger.debug("Couldn't find " + pJob.getClass().getName());
			throw new IllegalArgumentException("The job info " + pJob.getClass().getName() + " could not be found");
		}

		sLogger.debug("Receiving job submission for {}", definition.getShortName());

		JobRequest request = new JobRequest(definition, null, pBuilder, null, Collections.emptySet());
		return submit(request);
	}

	/**
	 * @see com.diamondq.reactions.api.ReactionsEngine#addToCollection(java.lang.Object,
	 *      com.diamondq.reactions.api.Action, java.lang.String, java.util.Map)
	 */
	@Override
	public <@NonNull T> ExtendedCompletableFuture<@Nullable Void> addToCollection(T pRecord, Action pAction,
		String pName, Map<String, String> pStates) {

		String type = pRecord.getClass().getName();

		/* Determine if the transient or persistent mScope is to be used */

		boolean isPersistent = false;
		String persistenceValue = pStates.get(sPERSISTENT_STATE);
		if ("true".equals(persistenceValue) == true)
			isPersistent = true;

		Store store = (isPersistent == true ? mPersistentStore : mTransientStore);

		return storeAndTrigger(store, pRecord, pAction, type, pName, pStates);
	}

	private <T> ExtendedCompletableFuture<@Nullable Void> storeAndTrigger(Store pStore, T pRecord, Action pAction,
		String pType, String pName, Map<String, String> pStates) {
		/* Persist the object */

		pStore.persist(pRecord, pAction, pType, pName, pStates);

		/* Next, we need to find the set of jobs that would be triggered by this change */

		Set<JobRequest> jobs = resolveTriggers(pRecord, pAction, pType, pName, pStates);

		/* Submit each job for execution */

		Set<ExtendedCompletableFuture<@Nullable Void>> futures = Sets.newHashSet();
		for (JobRequest job : jobs) {
			futures.add(submit(job));
		}
		return ExtendedCompletableFuture.allOf(futures);
	}

	/**
	 * Submit a given job definition for execution. This attempts to resolve all dependencies, and once they are
	 * resolved, executes them. Once the job is queued for execution, it returns and all the work runs on a separate
	 * thread.
	 *
	 * @param pJob the job to execute
	 * @return the future
	 */
	private <RESULT> ExtendedCompletableFuture<RESULT> submit(JobRequest pJob) {

		ExtendedCompletableFuture<RESULT> result = new ExtendedCompletableFuture<>();
		ExtendedCompletableFuture.runAsync(new Runnable() {

			@Override
			public void run() {
				sLogger.trace("Calling executeIfDepends from submit(JobRequest)");
				executeIfDepends(pJob, result);
			}
		}, mExecutorService).whenComplete((v, ex) -> {
			if (ex != null) {
				sLogger.trace("", ex);
				result.completeExceptionally(ex);
				return;
			}
			sLogger.trace("submit({}) completed", pJob.jobDefinition.getShortName());
		});
		return result;
	}

	/**
	 * Checks all the dependencies on this job, and if they're all available, executes the job. If not, it attempts to
	 * find jobs that can fulfill the dependences, and schedules those.
	 *
	 * @param pJob the job
	 * @param pResult the result
	 */
	private <RESULT> void executeIfDepends(JobRequest pJob, ExtendedCompletableFuture<RESULT> pResult) {

		if (sLogger.isDebugEnabled() == true)
			sLogger.debug("executeIfDepends: {}", pJob.getIdentifier());

		List<@Nullable Object> dependents = Lists.newArrayList();
		for (DependentDefinition<?> dependent : Iterables.concat(pJob.jobDefinition.variables,
			pJob.jobDefinition.params)) {

			DependentInfo queriedDependentInfo = pJob.executingByParam.get(dependent);
			if (queriedDependentInfo == null) {
				DependentInfo newDependentInfo = new DependentInfo();
				if ((queriedDependentInfo = pJob.executingByParam.putIfAbsent(dependent, newDependentInfo)) == null)
					queriedDependentInfo = newDependentInfo;
				if (dependent instanceof VariableDefinition<?>) {
					VariableDefinition<?> variable = (VariableDefinition<?>) dependent;
					String name = variable.variableName;
					pJob.variableMap.put(name, variable);
				}
			}

			final DependentInfo dependentInfo = queriedDependentInfo;

			/* If it's a VariableDefinition, see if we can resolve it directly */

			if (dependent instanceof VariableDefinition) {
				VariableDefinition<?> variable = (VariableDefinition<?>) dependent;

				if ((dependentInfo.isResolved == false) && (variable.valueByResultName == true)) {
					String resultName = pJob.resultName;
					if (resultName != null) {
						dependentInfo.resolvedValue = resultName;
						dependentInfo.isResolved = true;
					}
				}

				if ((dependentInfo.isResolved == false) && (variable.valueByResultStateValue != null)) {
					for (StateCriteria sc : pJob.resultStates) {
						if ((sc.state.equals(variable.valueByResultStateValue)) && (sc.isEqual == true)) {
							if (sc instanceof StateValueCriteria) {
								dependentInfo.resolvedValue = ((StateValueCriteria) sc).value;
								dependentInfo.isResolved = true;
							}
						}
					}
				}

				/* If there is a valueByInput, then use that */

				Function<JobParamsBuilder, ?> valueByInput = variable.valueByInput;
				JobParamsBuilder paramsBuilder = pJob.paramsBuilder;
				if ((dependentInfo.isResolved == false) && (valueByInput != null) && (paramsBuilder != null)) {
					dependentInfo.resolvedValue = convert(valueByInput.apply(paramsBuilder), variable.clazz);
					dependentInfo.isResolved = true;
				}

			}

			/* Use a job variableName if present */

			if (dependent instanceof ParamDefinition) {
				ParamDefinition<?> param = (ParamDefinition<?>) dependent;

				if ((dependentInfo.isResolved == false) && (param.valueByVariable != null)) {
					VariableDefinition<?> variableDefinition = pJob.variableMap.get(param.valueByVariable);
					if (variableDefinition != null) {
						DependentInfo variableDependentInfo = pJob.executingByParam.get(variableDefinition);
						if (variableDependentInfo != null) {
							if (variableDependentInfo.isResolved == false)
								throw new IllegalStateException("The variable " + variableDefinition.getIdentifier()
									+ " is not resolved by the time it was used in parameter " + param.getIdentifier());

							/* Coerce the variable resolved value into the type needed by the dependent */

							Object paramValue = convert(variableDependentInfo.resolvedValue, param.clazz);
							dependentInfo.resolvedValue = paramValue;
							dependentInfo.isResolved = true;
						}
					}
				}

				/* If there is a valueByInput, then use that */

				Function<JobParamsBuilder, ?> valueByInput = param.valueByInput;
				JobParamsBuilder paramsBuilder = pJob.paramsBuilder;
				if ((dependentInfo.isResolved == false) && (valueByInput != null) && (paramsBuilder != null)) {
					dependentInfo.resolvedValue = valueByInput.apply(paramsBuilder);
					dependentInfo.isResolved = true;
				}

				/* If it's a value by trigger */

				if ((dependentInfo.isResolved == false) && (param.valueByTrigger == true)) {
					dependentInfo.resolvedValue = pJob.triggerObject;
					dependentInfo.isResolved = true;
				}
			}

			/* Try looking for a transient or persistent storage for this dependent */

			if ((dependentInfo.isResolved == false) && (dependent.isStored == true)) {
				if (dependent.isPersistent != null) {
					Store store = (dependent.isPersistent == true ? mPersistentStore : mTransientStore);
					Set<Object> storeDependents = store.resolve(dependent);
					// TODO: For now, just take the first
					if (storeDependents.isEmpty() == false) {
						dependentInfo.resolvedValue = storeDependents.iterator().next();
						dependentInfo.isResolved = true;
					}
				}
				else {
					Set<Object> storeDependents = mTransientStore.resolve(dependent);
					// TODO: For now, just take the first
					if (storeDependents.isEmpty() == false) {
						dependentInfo.resolvedValue = storeDependents.iterator().next();
						dependentInfo.isResolved = true;
					}
					if (dependentInfo.isResolved == false) {
						storeDependents = mPersistentStore.resolve(dependent);
						// TODO: For now, just take the first
						if (storeDependents.isEmpty() == false) {
							dependentInfo.resolvedValue = storeDependents.iterator().next();
							dependentInfo.isResolved = true;
						}
					}
				}
			}

			/* Finally, try to see if there is a job */

			if (dependentInfo.isResolved == false) {

				/* If we haven't already searched the jobs, then do so now */

				if (dependentInfo.currentJob == -1) {

					/* Find the list of jobs that can produce it */

					Set<JobRequest> possibleJobs = resolveParams(pJob, dependent);
					List<@NonNull JobRequest> bestJobs = sortJobs(possibleJobs);
					dependentInfo.jobs = ImmutableList.copyOf(bestJobs);
				}

				/* See if there is an additional job to try */

				if (dependentInfo.jobs.size() > (dependentInfo.currentJob + 1)) {

					dependentInfo.currentJob++;

					/* Execute the job */

					JobRequest bestJob = dependentInfo.jobs.get(dependentInfo.currentJob);

					sLogger.debug("queuing param job {}", bestJob.jobDefinition.getShortName());
					submit(bestJob).whenComplete((v, ex) -> {
						sLogger.trace("Here");
						if (ex != null) {

							if (ex instanceof CompletionException)
								ex = ((CompletionException) ex).getCause();

							if (ex == null)
								ex = new RuntimeException(
									"CompletionException returned a null cause. That should never happen. Unknown how to resolve.");

							/*
							 * If there was an exception, but it wasn't an error, then just log it as a debug message,
							 * and continue to the next one. NOTE: If it was the 'last' job, then the re-queue of the
							 * parent job will use the result as it's output
							 */
							if (ex instanceof AbstractReactionsNotErrorException) {

								sLogger.debug("param job({}) failed, but continuing to the next choice: {}",
									bestJob.jobDefinition.getShortName(), ex.getMessage());

								dependentInfo.jobError = ex;
							}
							else {
								sLogger.debug("param job(" + bestJob.jobDefinition.getShortName() + ") failed", ex);
								pResult.completeExceptionally(ex);
								return;
							}
						}

						if (dependent.isStored == false) {
							dependentInfo.resolvedValue = v;
							dependentInfo.isResolved = true;
						}

						sLogger.debug("re-queuing job {}", pJob.jobDefinition.getShortName());

						ExtendedCompletableFuture.runAsync(() -> executeIfDepends(pJob, pResult), mExecutorService)
							.whenComplete((v2, ex2) -> {
								if (ex2 != null) {
									sLogger.debug("re-running job " + pJob.jobDefinition.getShortName() + " failed");
									sLogger.trace("", ex2);
									pResult.completeExceptionally(ex2);
									return;
								}
								sLogger.debug("executeIfDepends({}) completed - 3", pJob.jobDefinition.getShortName());
							});

					});

					/* Exit now, so that the dependent can be built */

					return;
				}
			}

			if (dependentInfo.isResolved == false) {

				StringBuilder sb = new StringBuilder();
				sb.append("The job cannot be executed because there are no solution on how to construct the ");
				if (dependent instanceof VariableDefinition)
					sb.append("variable ");
				else if (dependent instanceof ParamDefinition)
					sb.append("param ");
				else
					sb.append("UNKNOWN ");
				sb.append(dependent.getShortName());
				sb.append('.');
				if (dependentInfo.jobError != null)
					sb.append(" This could be because of an error when executing the dependent jobs.");
				sLogger.warn(sb.toString());
				AbstractReactionsException are =
					new ReactionsMissingDependentException(sb.toString(), dependentInfo.jobError);
				are.setMessagePrefix(pJob.getIdentifier());
				throw are;
			}

			if (dependent instanceof ParamDefinition)
				dependents.add(dependentInfo.resolvedValue);
		}

		/* Now execute the job */

		sLogger.debug("Received all parameters. Executing...");

		Object result = execute(pJob, pJob.jobDefinition.params.toArray(new ParamDefinition<?>[0]),
			dependents.toArray(new Object[0]));

		/* If the method returned a future, then set up a callback to continue processing when it actually finishes */

		if (result instanceof ExtendedCompletableFuture) {
			@SuppressWarnings("unchecked")
			ExtendedCompletableFuture<@Nullable Object> resultFuture =
				(ExtendedCompletableFuture<@Nullable Object>) result;
			resultFuture.whenComplete((resolvedResult, ex) -> {
				if (ex != null) {
					sLogger.debug("execution of job(" + pJob.jobDefinition.getShortName() + ") failed", ex);
					pResult.completeExceptionally(ex);
					return;
				}
				sLogger.debug("execution of job(" + pJob.jobDefinition.getShortName() + ") succeeded", ex);
				processResult(pJob, pResult, resolvedResult);
			});
		}
		else {
			processResult(pJob, pResult, result);
		}
	}

	/**
	 * Attempts to convert an object from one type to another
	 *
	 * @param pValue the object
	 * @param pClazz the expected class
	 * @return the value or throws an exception if it can't be converted
	 */
	private <C> @Nullable C convert(@Nullable Object pValue, Class<C> pClazz) {
		if (pValue == null) {
			if (pClazz.isPrimitive() == true)
				throw new IllegalArgumentException("Unable to convert a null value to a " + pClazz.getName());
			return null;
		}

		/* If it already matches, then just return it */

		Object r;
		if (pClazz.isInstance(pValue))
			r = pValue;
		else {

			/* Handle some basic conversions */

			if (pValue instanceof String) {
				if (pClazz.equals(Integer.class)) {
					r = Integer.parseInt((String) pValue);
				}
				else
					throw new IllegalArgumentException(
						"Unable to convert " + pValue.getClass().getName() + " to " + pClazz.getName());
			}
			else if (pValue instanceof Integer) {
				if (pClazz.equals(String.class))
					r = pValue.toString();
				else
					throw new IllegalArgumentException(
						"Unable to convert " + pValue.getClass().getName() + " to " + pClazz.getName());
			}
			else
				throw new IllegalArgumentException(
					"Unable to convert " + pValue.getClass().getName() + " to " + pClazz.getName());
		}

		@SuppressWarnings("unchecked")
		C result = (C) r;
		return result;

	}

	@SuppressWarnings("null")
	private <RESULT> void processResult(JobRequest pJob, ExtendedCompletableFuture<RESULT> pResult,
		@Nullable Object result) {
		/* Store the results and trigger anything that needs it */

		@Nullable
		RESULT resultObj = null;

		for (ResultDefinition<?> rd : pJob.jobDefinition.results) {

			/* Determine the actual object */

			Object rdObject;
			if (rd.resultIsParam == false) {
				if (result == null)
					throw new IllegalArgumentException("The result must not be null");
				rdObject = result;
				@SuppressWarnings("unchecked")
				RESULT r = (RESULT) result;
				resultObj = r;
			}
			else {
				throw new UnsupportedOperationException();
			}

			if (rd.isStored == true) {
				boolean persist = (rd.isPersistent != null ? rd.isPersistent : false);
				Map<String, String> states = Maps.newHashMap();
				for (StateCriteria sc : rd.requiredStates) {
					if (sc instanceof StateValueCriteria) {
						StateValueCriteria svc = (StateValueCriteria) sc;
						if (svc.isEqual == false)
							throw new IllegalArgumentException("A Result state criteria cannot be a not equal");
						states.put(svc.state, svc.value);
					}
					else if (sc instanceof StateVariableCriteria) {
						throw new UnsupportedOperationException();
					}
					else if (sc instanceof StateValueVariableCriteria) {
						throw new UnsupportedOperationException();
					}
					else {
						if (sc.isEqual == false)
							throw new IllegalArgumentException("A Result state criteria cannot be a not equal");
						states.put(sc.state, "true");
					}
				}

				String name = pJob.resultName;
				if (name == null)
					name = rd.name;
				if (name == null)
					throw new IllegalArgumentException("Unable to find the name");

				Store store = (persist == true ? mPersistentStore : mTransientStore);
				storeAndTrigger(store, rdObject, Action.CHANGE, rd.clazz.getName(), name, states);
			}
		}

		/* We're done */

		sLogger.trace("Job is complete");

		pResult.complete(resultObj);
	}

	public static class VirtualNameNode {
		public final NameNode			nameNode;

		public final @Nullable String	resultName;

		public VirtualNameNode(NameNode pNameNode, @Nullable String pResultName) {
			super();
			nameNode = pNameNode;
			resultName = pResultName;
		}
	}

	/**
	 * Attempt to resolve the param definition to find a job that has it as a result
	 *
	 * @param pJob the job
	 * @param pDependent the param
	 * @return the set of jobs that produce that result
	 */
	private Set<JobRequest> resolveParams(JobRequest pJob, DependentDefinition<?> pDependent) {
		Set<TypeNode> typeNodes = Sets.newIdentityHashSet();

		/* Types */

		TypeNode possibleTypeNode;
		possibleTypeNode = mResultTree.get(pDependent.clazz.getName());
		if (possibleTypeNode != null)
			typeNodes.add(possibleTypeNode);

		/* Names */

		Set<VirtualNameNode> nameNodes = Sets.newIdentityHashSet();
		for (TypeNode typeNode : typeNodes) {
			NameNode possibleNameNode;
			String name = pDependent.name;
			if (name != null) {

				/* Check to see if there is one matching the name */

				possibleNameNode = typeNode.getName(name);
				if (possibleNameNode != null)
					nameNodes.add(new VirtualNameNode(possibleNameNode, name));

				/* Also, check to see if there is one that wildcards the name into a variable */

				possibleNameNode = typeNode.getWithVariables();
				if (possibleNameNode != null)
					nameNodes.add(new VirtualNameNode(possibleNameNode, name));
			}
			else {
				possibleNameNode = typeNode.getUndefined();
				if (possibleNameNode != null)
					nameNodes.add(new VirtualNameNode(possibleNameNode, null));

				/* Since we didn't provide a name, we shouldn't find those that require a name bound to a variable */

			}
		}

		/* States */

		Set<JobRequest> jobs = Sets.newIdentityHashSet();
		for (VirtualNameNode nameNode : nameNodes) {
			Set<StateCriteria[]> criteriasSet = nameNode.nameNode.getCriterias();
			for (StateCriteria[] criterias : criteriasSet) {

				/* Handle the required states */

				boolean match = true;
				ImmutableSet.Builder<StateCriteria> matchCriteriaBuilder = ImmutableSet.builder();
				for (StateCriteria reqSC : pDependent.requiredStates) {
					if (reqSC instanceof StateVariableCriteria) {
						throw new UnsupportedOperationException(
							reqSC.getIdentifier() + " not supported for " + pDependent.getIdentifier());
					}
					else if (reqSC instanceof StateValueVariableCriteria) {
						StateValueVariableCriteria reqSVVC = (StateValueVariableCriteria) reqSC;

						/* First resolve to a StateValueCriteria */

						VariableDefinition<?> variableDefinition = pJob.variableMap.get(reqSVVC.variableName);
						if (variableDefinition == null) {
							match = false;
							break;
						}
						DependentInfo variableDependentInfo = pJob.executingByParam.get(variableDefinition);
						if (variableDependentInfo == null) {
							match = false;
							break;
						}
						if (variableDependentInfo.isResolved == false) {
							match = false;
							break;
						}
						Object resolvedValue = variableDependentInfo.resolvedValue;
						if (resolvedValue == null) {
							match = false;
							break;
						}
						resolvedValue = convert(resolvedValue, String.class);
						if (resolvedValue == null) {
							match = false;
							break;
						}
						StateValueCriteria svc =
							new StateValueCriteria(reqSVVC.state, reqSVVC.isEqual, (String) resolvedValue);

						/* Now evaluate against that */

						StateCriteria matchCriteria = evaluateCriterias(svc, criterias);
						if (matchCriteria == null) {
							match = false;
							break;
						}
						matchCriteriaBuilder.add(matchCriteria);

					}
					else {
						StateCriteria matchCriteria = evaluateCriterias(reqSC, criterias);
						if (matchCriteria == null) {
							match = false;
							break;
						}
						matchCriteriaBuilder.add(matchCriteria);
					}
				}

				if (match == true) {
					Set<JobDefinitionImpl> jobsByCriteria = nameNode.nameNode.getJobsByCriteria(criterias);
					ImmutableSet<StateCriteria> matchCriterias = matchCriteriaBuilder.build();
					if (jobsByCriteria != null)
						for (JobDefinitionImpl job : jobsByCriteria) {
							jobs.add(new JobRequest(job, null, null, nameNode.resultName, matchCriterias));
						}
				}
			}

			/* If the parameter has any required states, then the no criteria jobs don't comply */

			if (pDependent.requiredStates.isEmpty() == true) {
				for (JobDefinitionImpl job : nameNode.nameNode.getNoCriteriaJobs()) {
					jobs.add(new JobRequest(job, null, null, nameNode.resultName, Collections.emptySet()));
				}
			}
		}

		return jobs;

	}

	/**
	 * Evaluate the test criteria against the possible matches. If it matches, then it returns a resolved possibleMatch
	 * or null if it doesn't match
	 *
	 * @param pTestCritera the test criteria (Must be either StateCriteria or StateValueCriteria
	 * @param pPossibleMatches the array of possible matches. Can be any of the test of criterias
	 * @return the 'resolved' possible match (meaning that StateValueVariableCriterias are resolved down to
	 *         StateValueCriteria's)
	 */
	private @Nullable StateCriteria evaluateCriterias(StateCriteria pTestCritera, StateCriteria[] pPossibleMatches) {
		StateCriteria match = null;
		if (pTestCritera instanceof StateValueCriteria) {
			StateValueCriteria reqSVC = (StateValueCriteria) pTestCritera;
			for (StateCriteria sc : pPossibleMatches) {
				if (sc instanceof StateValueCriteria) {
					StateValueCriteria svc = (StateValueCriteria) sc;

					/* Check to see if this criteria matches the state */

					if (svc.state.equals(reqSVC.state)) {

						if (svc.value.equals(reqSVC.value)) {

							/*
							 * If it does, but we're not supposed to find it, then we're done. This one won't match
							 */

							if (pTestCritera.isEqual == false) {
								break;
							}

							match = svc;
							break;
						}
					}
				}
				else if (sc instanceof StateValueVariableCriteria) {
					StateValueVariableCriteria svvc = (StateValueVariableCriteria) sc;

					if (svvc.state.equals(reqSVC.state)) {

						/* It's a wildcard on the value so that matches */

						match = new StateValueCriteria(svvc.state, svvc.isEqual, reqSVC.value);
						break;
					}
				}
			}
		}
		else if (pTestCritera.getClass().equals(StateCriteria.class)) {
			/* The parameter criteria is just if a State exists (or not) */

			for (StateCriteria sc : pPossibleMatches) {

				/* Check to see if this criteria matches the state */

				if (sc.state.equals(pTestCritera.state)) {

					/*
					 * If it does, but we're not supposed to find it, then we're done. This one won't match
					 */

					if (pTestCritera.isEqual == false) {
						break;
					}

					match = sc;
					break;
				}
			}
		}
		else
			throw new UnsupportedOperationException(pTestCritera.getIdentifier() + " not supported for evaluation");

		return match;
	}

	/**
	 * Sorts the set of possible jobs to the best one to try.
	 *
	 * @param pPossibleJobs
	 * @return
	 */
	private List<@NonNull JobRequest> sortJobs(Set<@NonNull JobRequest> pPossibleJobs) {

		/*
		 * TODO: This hack just returns the first job. A better implementation would look at the ones that have the
		 * fewest dependencies
		 */

		List<@NonNull JobRequest> jobList = new ArrayList<JobRequest>(pPossibleJobs);
		jobList.sort(null);
		return jobList;
	}

	private @Nullable Object execute(JobRequest pJob, ParamDefinition<?>[] pArray, Object[] pDependents) {
		Object callback = pJob.jobDefinition.method.getCallback();
		Method method = pJob.jobDefinition.method.getMethod();
		Class<?> functionClass = pJob.jobDefinition.method.getCallbackClass();
		Class<?>[] parameterTypes = TypeResolver.resolveRawArguments(functionClass, callback.getClass());
		int paramLen = parameterTypes.length - (pJob.jobDefinition.method.getHasReturn() == true ? 1 : 0);
		Object[] invokeParams = new Object[paramLen];

		/* Now, for each parameter, find the matching dependent */

		for (int i = 0; i < paramLen; i++) {
			invokeParams[i] = pDependents[i];
			// if ((match == false) && (pJob.triggerObject != null)) {
			// if (parameterTypes[i].isAssignableFrom(pJob.triggerObject.getClass())) {
			// invokeParams[i] = pJob.triggerObject;
			// match = true;
			// }
			// }
		}

		Object result;
		try {
			method.setAccessible(true);
			result = method.invoke(callback, invokeParams);
		}
		catch (IllegalAccessException | IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		}
		catch (InvocationTargetException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof AbstractReactionsException) {
				AbstractReactionsException are = (AbstractReactionsException) cause;
				are.setMessagePrefix(pJob.getIdentifier());
				throw are;
			}
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			throw new RuntimeException(cause);
		}
		return result;
	}

	private <T> Set<JobRequest> resolveTriggers(T pTriggerObject, @Nullable Action pAction, @Nullable String pType,
		@Nullable String pName, @Nullable Map<String, String> pStates) {
		Set<ActionNode> actionNodes = Sets.newIdentityHashSet();

		/* ActionNodes */

		ActionNode possibleActionNode;
		if (pAction != null) {
			possibleActionNode = mTriggers.get(pAction.name());
			if (possibleActionNode != null)
				actionNodes.add(possibleActionNode);
		}
		possibleActionNode = mTriggers.get("__UNDEFINED__");
		if (possibleActionNode != null)
			actionNodes.add(possibleActionNode);

		/* Types */

		Set<TypeNode> typeNodes = Sets.newIdentityHashSet();
		for (ActionNode actionNode : actionNodes) {
			TypeNode possibleTypeNode;
			if (pType != null) {
				possibleTypeNode = actionNode.getType(pType);
				if (possibleTypeNode != null)
					typeNodes.add(possibleTypeNode);
			}
			possibleTypeNode = actionNode.getUndefined();
			if (possibleTypeNode != null)
				typeNodes.add(possibleTypeNode);
		}

		/* Names */

		Set<NameNode> nameNodes = Sets.newIdentityHashSet();
		for (TypeNode typeNode : typeNodes) {
			NameNode possibleNameNode;
			if (pName != null) {
				possibleNameNode = typeNode.getName(pName);
				if (possibleNameNode != null)
					nameNodes.add(possibleNameNode);
			}
			possibleNameNode = typeNode.getUndefined();
			if (possibleNameNode != null)
				nameNodes.add(possibleNameNode);
		}

		/* States */

		Set<JobRequest> jobs = Sets.newIdentityHashSet();
		for (NameNode nameNode : nameNodes) {
			Set<StateCriteria[]> criteriasSet = nameNode.getCriterias();
			for (StateCriteria[] criterias : criteriasSet) {

				/* Check the criterias */

				boolean match = true;
				for (StateCriteria criteria : criterias) {
					if (criteria instanceof StateValueCriteria) {
						StateValueCriteria svc = (StateValueCriteria) criteria;
						if ((pStates == null) || (pStates.containsKey(svc.state) == false)) {
							if (svc.isEqual == false)
								continue;
							match = false;
							break;
						}
						if (svc.value.equals(pStates.get(svc.state)) == false) {
							if (svc.isEqual == false)
								continue;
							match = false;
							break;
						}
					}
					else if (criteria instanceof StateVariableCriteria) {
						throw new UnsupportedOperationException();
					}
					else if (criteria instanceof StateValueVariableCriteria) {
						throw new UnsupportedOperationException();
					}
					else {
						if ((pStates == null) || (pStates.containsKey(criteria.state) == false)) {
							if (criteria.isEqual == false)
								continue;
							match = false;
							break;
						}
					}
				}
				if (match == true) {
					@Nullable
					Set<JobDefinitionImpl> possibleJobs = nameNode.getJobsByCriteria(criterias);
					if (possibleJobs != null) {
						for (JobDefinitionImpl job : possibleJobs) {
							jobs.add(new JobRequest(job, pTriggerObject, null, Collections.emptySet()));
						}
					}
				}
			}
			for (JobDefinitionImpl job : nameNode.getNoCriteriaJobs()) {
				jobs.add(new JobRequest(job, pTriggerObject, null, Collections.emptySet()));
			}
		}

		return jobs;
	}

}
