package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;

public interface FullComparePropertyConfigurer extends OperationConfigurer {

//    @FunctionalInterface
//    public interface Matcher{
//        public boolean matches(String s);
//    }


    FullComparePropertyConfigurer ignore(Types.Supplier<?>... getter);

    /**
     * Ignore all properties in root AND compare obj whichs fieldname matches
     * {@link FieldNameMatcher#matches(String)}.
     * @param fieldNameMatcher
     * @return
     */
    FullComparePropertyConfigurer ignore(FieldNameMatcher fieldNameMatcher);
    FullComparePropertyConfigurer ignore(String... propertyName);

}
