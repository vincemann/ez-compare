package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.config.FullCompareConfig;

public interface FullCompareOptionsConfigurer
        extends CompareOptionsConfigurer{

    interface FullCompareConfigConfigurer{
        public void configure(FullCompareConfig.Modifier<?> config);
    }

    public FullCompareOptionsConfigurer ignoreNull(boolean value);
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value);
    public FullCompareOptionsConfigurer configureFullCompare(FullCompareConfigConfigurer configurer);

}
