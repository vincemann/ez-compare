package io.github.vincemann.ezcompare.configurer.options;


import io.github.vincemann.ezcompare.configurer.properties.SelectivePropertiesConfigurer;
import io.github.vincemann.ezcompare.menu.PropertyBridge;

public interface CompareOptionsConfigurer extends PropertyBridge<SelectivePropertiesConfigurer> {
    public CompareOptionsConfigurer fullDiff(boolean value);
}
