package io.github.vincemann.ezcompare.template;

public interface PartialCompareOptionsConfigurer extends PropertyBridge{

    public PartialCompareOptionsConfigurer configurePartialCompare(OptionsConfigurer<CompareTemplate.PartialCompareConfig> configurer);
}
