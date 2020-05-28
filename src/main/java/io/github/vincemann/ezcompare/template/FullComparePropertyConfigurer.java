package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;

public interface FullComparePropertyConfigurer extends OperationConfigurer{

    FullComparePropertyConfigurer ignore(Types.Supplier<?> getter);
    FullComparePropertyConfigurer ignore(String propertyName);

}
