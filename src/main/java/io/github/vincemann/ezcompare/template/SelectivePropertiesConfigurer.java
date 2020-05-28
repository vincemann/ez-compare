package io.github.vincemann.ezcompare.template;

public interface SelectivePropertiesConfigurer extends PartialPropertyConfigurer {

    FullComparePropertyConfigurer allOf(Object o);
    FullComparePropertyConfigurer all();

}
