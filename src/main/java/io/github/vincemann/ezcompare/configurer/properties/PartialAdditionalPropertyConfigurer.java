package io.github.vincemann.ezcompare.configurer.properties;

import io.github.vincemann.ezcompare.configurer.operation.OperationConfigurer;
import io.github.vincemann.ezcompare.menu.OperationBridge;

public interface PartialAdditionalPropertyConfigurer extends
        //menu options
        PartialPropertyConfigurer,
        OperationBridge<OperationConfigurer>
{

}
