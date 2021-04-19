package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;

public interface FullComparePropertyConfigurer extends OperationConfigurer {

//    @FunctionalInterface
//    public interface Matcher{
//        public boolean matches(String s);
//    }

    FullComparePropertyConfigurer ignore(Types.Supplier<?>... getter);
    FullComparePropertyConfigurer ignore(String... propertyName);

}
