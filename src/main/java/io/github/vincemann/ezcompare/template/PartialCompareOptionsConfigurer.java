package io.github.vincemann.ezcompare.template;

public interface PartialCompareOptionsConfigurer extends CompareOptionsConfigurer{

    interface PartialCompareConfigConfigurer{
        public void configure(CompareTemplate.PartialCompareConfig config);
    }
    public PartialCompareOptionsConfigurer configurePartial(PartialCompareConfigConfigurer configurer);
}
