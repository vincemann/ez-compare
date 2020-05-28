package io.github.vincemann.ezcompare.template;

public interface OptionsConfigurer<T extends CompareTemplate.AbstractCompareConfig> {
    public void configure(T config);
}
