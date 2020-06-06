package io.github.vincemann.ezcompare.configurer.properties;

//select properties to include for compare process
public interface SelectivePropertiesConfigurer extends
        //menu options
        PartialPropertyConfigurer{
        FullComparePropertyConfigurer all();

}
