package io.github.vincemann.ezcompare.template;

public interface PartialCompareOptionsConfigurer extends PropertyBridge{

    interface PartialCompareConfigConfigurer extends OptionsConfigurer<CompareTemplate.PartialCompareConfig> {

    }
    public PartialCompareOptionsConfigurer configure(PartialCompareConfigConfigurer configurer);
}
