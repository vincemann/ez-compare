package com.github.vincemann.ezcompare;

public interface PartialCompareOptionsConfigurer extends CompareOptionsConfigurer{

    interface PartialCompareConfigConfigurer{
        public void configure(PartialCompareConfig config);
    }
    public PartialCompareOptionsConfigurer configurePartialCompare(PartialCompareConfigConfigurer configurer);
}
