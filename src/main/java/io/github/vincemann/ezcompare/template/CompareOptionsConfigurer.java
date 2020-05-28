package io.github.vincemann.ezcompare.template;

public interface CompareOptionsConfigurer<C extends CompareTemplate.AbstractCompareConfig> {

    public FullCompareOptionsConfigurer configure(OptionsConfigurer<C> configurer);
}
