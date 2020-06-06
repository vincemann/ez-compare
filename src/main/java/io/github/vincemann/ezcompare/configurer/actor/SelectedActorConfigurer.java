package io.github.vincemann.ezcompare.configurer.actor;

import io.github.vincemann.ezcompare.configurer.options.SelectiveOptionsConfigurer;
import io.github.vincemann.ezcompare.configurer.properties.SelectivePropertiesConfigurer;
import io.github.vincemann.ezcompare.bridges.OptionsBridge;
import io.github.vincemann.ezcompare.bridges.PropertyBridge;

//actor has been selected
public interface SelectedActorConfigurer extends
        //menu options
        OptionsBridge<SelectiveOptionsConfigurer>,
        PropertyBridge<SelectivePropertiesConfigurer>
{
}
