package io.github.vincemann.ezcompare.configurer.properties;

import io.github.vincemann.ezcompare.configurer.operation.OperationConfigurer;
import io.github.vincemann.ezcompare.bridges.OperationBridge;

public interface SelectedPartialComparePropertyConfigurer extends
        //menu options
        PartialComparePropertyConfigurer,
        OperationBridge<OperationConfigurer>
{

}
