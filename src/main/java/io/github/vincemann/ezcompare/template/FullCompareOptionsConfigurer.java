package io.github.vincemann.ezcompare.template;

public interface FullCompareOptionsConfigurer
        extends PropertyBridge,
        CompareOptionsConfigurer<CompareTemplate.FullCompareConfig> {

    public FullCompareOptionsConfigurer ignoreNull(boolean value);
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value);

}
