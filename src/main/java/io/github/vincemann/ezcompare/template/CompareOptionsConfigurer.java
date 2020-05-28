package io.github.vincemann.ezcompare.template;

import org.checkerframework.checker.units.qual.C;

public interface CompareOptionsConfigurer<C extends CompareTemplate.AbstractCompareConfig> {

    public FullCompareOptionsConfigurer configure(OptionsConfigurer<C> configurer);
}
