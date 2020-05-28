package io.github.vincemann.ezcompare.template;

import io.github.vincemann.ezcompare.template.CompareTemplate;

public interface OptionsConfigurer<T extends CompareTemplate.AbstractCompareConfig> {
    public void configure(T config);
}
