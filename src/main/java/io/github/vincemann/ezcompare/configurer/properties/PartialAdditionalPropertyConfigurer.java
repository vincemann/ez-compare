package io.github.vincemann.ezcompare.configurer.properties;

import io.github.vincemann.ezcompare.configurer.operation.OperationConfigurer;
import io.github.vincemann.ezcompare.menu.BackToMenuBridge;
import io.github.vincemann.ezcompare.menu.OperationBridge;

public interface PartialAdditionalPropertyConfigurer<M> extends
        //menu options
        PartialPropertyConfigurer<M>,
        BackToMenuBridge<M>
{

}
