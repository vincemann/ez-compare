package io.github.vincemann.ezcompare.template;

public interface FullCompareOptionsConfigurer
        extends CompareOptionsConfigurer{

    interface FullCompareConfigConfigurer{
        public void configure(Comparison.FullCompareConfig config);
    }

    public FullCompareOptionsConfigurer ignoreNull(boolean value);
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value);
    public FullCompareOptionsConfigurer configureFullCompare(FullCompareConfigConfigurer configurer);

}
