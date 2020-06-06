package io.github.vincemann.ezcompare.configurer.actor;

import io.github.vincemann.ezcompare.configurer.options.SelectiveOptionsConfigurer;
import io.github.vincemann.ezcompare.configurer.properties.SelectivePropertiesConfigurer;
import io.github.vincemann.ezcompare.menu.OptionsBridge;
import io.github.vincemann.ezcompare.menu.PropertyBridge;

//actor has been selected
public interface SelectedActorConfigurer extends
        //menu options
        OptionsBridge<SelectiveOptionsConfigurer>,
        PropertyBridge<SelectivePropertiesConfigurer>
{
}
