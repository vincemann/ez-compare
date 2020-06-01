package io.github.vincemann.ezcompare.template;

public interface PartialCompareOptionsConfigurer extends CompareOptionsConfigurer{

    public PartialCompareOptionsConfigurer configurePartialCompare(ConfigConfigurer<CompareTemplate.PartialCompareConfig> configurer);
}
