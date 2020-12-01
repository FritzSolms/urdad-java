package org.urdad.jaxrs.adapter.rpcrest;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** The qualifier used by the Jaxb Context Resolver to resolve the ClassesToBeBound if there are multiple instances. */
@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD, PARAMETER })
public @interface ConfiguredClassesToBeBound
{}