package io.github.vincemann.ezcompare.template;

public interface ConfigConfigurer<T extends CompareTemplate.CompareTemplateConfig.CompareTemplateConfigBuilder> {
    public void configure(T config);
}
