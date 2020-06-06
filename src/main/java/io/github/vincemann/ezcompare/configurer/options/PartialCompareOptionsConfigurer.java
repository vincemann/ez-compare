package io.github.vincemann.ezcompare.configurer.options;

import io.github.vincemann.ezcompare.Comparison;

public interface PartialCompareOptionsConfigurer extends CompareOptionsConfigurer {

    interface PartialCompareConfigConfigurer{
        public void configure(Comparison.PartialCompareConfig config);
    }
    public PartialCompareOptionsConfigurer configurePartial(PartialCompareConfigConfigurer configurer);
}
