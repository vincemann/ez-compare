package io.github.vincemann.ezcompare.template;

public interface ConfigConfigurer<T extends CompareTemplate.AbstractCompareConfig> {
    public void configure(T config);
}
