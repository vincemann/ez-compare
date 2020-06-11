package com.github.vincemann.ezcompare;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.C;

@AllArgsConstructor
public abstract class AbstractConfigModifier<M extends AbstractConfigModifier, C extends RapidEqualsBuilder.CompareConfig> {

    private C config;

    public M reflectUpToClass(Class<?> value) {
        config.reflectUpToClass = value;
        return (M) this;
    }

    public M fullDiff(boolean value) {
        config.minimalDiff = value;
        return (M) this;
    }

    protected C getConfig() {
        return config;
    }
}
