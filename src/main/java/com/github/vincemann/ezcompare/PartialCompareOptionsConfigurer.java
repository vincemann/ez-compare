package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.config.PartialCompareConfig;

public interface PartialCompareOptionsConfigurer extends CompareOptionsConfigurer{

    interface PartialCompareConfigConfigurer{
        public void configure(PartialCompareConfig.Modifier<?> config);
    }
    public PartialCompareOptionsConfigurer configurePartialCompare(PartialCompareConfigConfigurer configurer);
}
