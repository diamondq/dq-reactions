package com.diamondq.common.reaction.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation is applied to a method on a class. This indicates that this method can return the result of the method
 * assuming that the inputs are provided.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConfigureReaction {

}
