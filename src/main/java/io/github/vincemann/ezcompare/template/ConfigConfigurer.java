package io.github.vincemann.ezcompare.template;

public interface ConfigConfigurer<T extends Comparison.CompareTemplateConfig.CompareTemplateConfigBuilder> {
    public void configure(T config);
}
