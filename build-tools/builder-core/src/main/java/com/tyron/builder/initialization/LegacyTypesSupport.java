package com.tyron.builder.initialization;

import java.util.Set;

/**
 * Enriches class loading with empty interfaces for certain types that have been removed,
 * but which are baked into the bytecode generated by the Groovy compiler.
 */
public interface LegacyTypesSupport {

    /**
     * Returns a set of classes that require {@link groovy.lang.GroovyObject} to be mixed in.
     */
    Set<String> getClassesToMixInGroovyObject();

    /**
     * Returns a set of types that have been removed, but which are baked into the bytecode
     * generated by the Groovy compiler
     */
    Set<String> getSyntheticClasses();

    /**
     * Generates an empty interface for the given class name.
     */
    byte[] generateSyntheticClass(String name);

    /**
     * Injects all the interfaces identified by {@link LegacyTypesSupport#getSyntheticClasses()}
     * into the given classloader.
     */
    void injectEmptyInterfacesIntoClassLoader(ClassLoader classLoader);
}
