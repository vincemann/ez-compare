package com.github.vincemann.ezcompare;


public class PropertyMatchers {

    /**
     * @param compareRoot supply own Entity
     * @return
     */
    public static PropertyMatcher propertyAssert(Object compareRoot) {
        return new PropertyMatcher(compareRoot);
    }


}
