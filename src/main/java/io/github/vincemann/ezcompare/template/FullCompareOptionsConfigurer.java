package io.github.vincemann.ezcompare.template;

public interface FullCompareOptionsConfigurer
        extends PropertyBridge{

    public FullCompareOptionsConfigurer ignoreNull(boolean value);
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value);

    interface FullCompareConfigConfigurer extends OptionsConfigurer<CompareTemplate.FullCompareConfig> {

    }

    public FullCompareOptionsConfigurer configure(FullCompareConfigConfigurer configurer);

}
