package io.github.vincemann.ezcompare.configurer.properties;

import com.github.hervian.reflection.Types;
import io.github.vincemann.ezcompare.configurer.operation.OperationConfigurer;
import io.github.vincemann.ezcompare.bridges.OperationBridge;

public interface FullComparePropertyConfigurer extends
        //menu options
        OperationBridge<OperationConfigurer> {
        FullComparePropertyConfigurer ignore(Types.Supplier<?>... getter);
        FullComparePropertyConfigurer ignore(String... propertyName);

}
