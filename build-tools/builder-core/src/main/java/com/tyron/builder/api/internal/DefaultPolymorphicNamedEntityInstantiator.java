package com.tyron.builder.api.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tyron.builder.api.BuildException;
import com.tyron.builder.api.InvalidUserDataException;
import com.tyron.builder.api.NamedDomainObjectFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultPolymorphicNamedEntityInstantiator<T> implements PolymorphicNamedEntityInstantiator<T> {
    private final Map<Class<? extends T>, NamedDomainObjectFactory<? extends T>> factories = Maps.newHashMap();
    private final Class<? extends T> baseType;
    private final String displayName;

    public DefaultPolymorphicNamedEntityInstantiator(Class<? extends T> type, String displayName) {
        this.displayName = displayName;
        this.baseType = type;
    }

    @Override
    public <S extends T> S create(String name, Class<S> type) {
        @SuppressWarnings("unchecked")
        NamedDomainObjectFactory<S> factory = (NamedDomainObjectFactory<S>) factories.get(type);
        if (factory == null) {
            throw new InvalidUserDataException(
                    String.format("Cannot create a %s because this type is not known to %s. Known types are: %s", type.getSimpleName(), displayName, getSupportedTypeNames()),
                    new NoFactoryRegisteredForTypeException());
        }
        return factory.create(name);
    }

    public String getSupportedTypeNames() {
        List<String> names = Lists.newArrayList();
        for (Class<?> clazz : factories.keySet()) {
            names.add(clazz.getSimpleName());
        }
        Collections.sort(names);
        return names.isEmpty() ? "(None)" : Joiner.on(", ").join(names);
    }

    @Override
    public <U extends T> void registerFactory(Class<U> type, NamedDomainObjectFactory<? extends U> factory) {
        if (!baseType.isAssignableFrom(type)) {
            String message = String.format("Cannot register a factory for type %s because it is not a subtype of container element type %s.", type.getSimpleName(), baseType.getSimpleName());
            throw new IllegalArgumentException(message);
        }
        if (factories.containsKey(type)) {
            throw new BuildException(String.format("Cannot register a factory for type %s because a factory for this type is already registered.", type.getSimpleName()));
        }
        factories.put(type, factory);
    }

    @Override
    public Set<? extends Class<? extends T>> getCreatableTypes() {
        return ImmutableSet.copyOf(factories.keySet());
    }
}
