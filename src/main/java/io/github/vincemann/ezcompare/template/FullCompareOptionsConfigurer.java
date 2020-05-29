package io.github.vincemann.ezcompare.template;

public interface FullCompareOptionsConfigurer
        extends PropertyBridge{

    public FullCompareOptionsConfigurer ignoreNull(boolean value);
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value);

    //use full name and dont detect arg type for selection of method with same name, bc it wont work for lambdas
    public FullCompareOptionsConfigurer configureFullCompare(OptionsConfigurer<CompareTemplate.FullCompareConfig> configurer);

}
