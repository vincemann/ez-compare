package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;
import com.google.common.collect.Sets;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import io.github.vincemann.ezcompare.util.ReflectionUtils;
import lombok.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Getter
@Setter
public class CompareTemplate implements
        ActorConfigurer, ActorBridge,
        SelectiveOptionsConfigurer, FullCompareOptionsConfigurer, PartialCompareOptionsConfigurer,CompareOptionsConfigurer,
        PropertyBridge,
        SelectivePropertiesConfigurer, FullComparePropertyConfigurer, PartialPropertyConfigurer, PartialAdditionalPropertyConfigurer,
        OperationConfigurer,
        ResultProvider
{
    private final static Logger log = Logger.getLogger(CompareTemplate.class.getName());

    public static PartialCompareConfig  GLOBAL_PARTIAL_COMPARE_CONFIG;
    public static FullCompareConfig     GLOBAL_FULL_COMPARE_CONFIG;


    private FullCompareConfig           fullCompareConfig;
    private PartialCompareConfig        partialCompareConfig;

    private Object root;
    private Object compare = new ArrayList<>();

    private RapidEqualsBuilder.Diff diff;
    private Boolean fullCompare = null;

    protected CompareTemplate(Object root) {
        this.root = root;
        this.fullCompareConfig  = getGlobalFullCompareConfig();
        this.partialCompareConfig = getGlobalPartialCompareConfig();
    }

    static FullCompareConfig getGlobalFullCompareConfig(){
        return  GLOBAL_FULL_COMPARE_CONFIG == null
                ? new FullCompareConfig()
                : GLOBAL_FULL_COMPARE_CONFIG;
    }
    static PartialCompareConfig getGlobalPartialCompareConfig(){
        return GLOBAL_PARTIAL_COMPARE_CONFIG == null
                ? new PartialCompareConfig()
                : GLOBAL_PARTIAL_COMPARE_CONFIG;
    }

    public static ActorConfigurer compare(Object rootActor) {
        return new CompareTemplate(rootActor);
    }

    @Override
    public ActorBridge with(Object actor) {
        this.compare = actor;
        return this;
    }

    @Override
    public SelectivePropertiesConfigurer properties() {
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(Types.Supplier<?> getter) {
        return ignore(MethodNameUtil.propertyNameOf(getter));
    }

    @Override
    public FullComparePropertyConfigurer ignore(String propertyName) {
        fullCompare=true;
        fullCompareConfig.getIgnoredProperties().add(propertyName);
        return this;
    }


    /**
     * Globally ignored Properties are not included.
     * If you want to add some of them anyways, use {@link this#configureFullCompare(FullCompareConfigConfigurer)}
     * and remove them from {@link FullCompareConfig#ignoredProperties} for this specific CompareTemplate.
     * @return
     */
    @Override
    public FullComparePropertyConfigurer all() {
        fullCompare = true;
        return this;
    }

    @Override
    public PartialAdditionalPropertyConfigurer include(Types.Supplier<?> getter) {
        return include(MethodNameUtil.propertyNameOf(getter));
    }

    @Override
    public PartialAdditionalPropertyConfigurer include(String propertyName) {
        fullCompare = false;
        partialCompareConfig.getIncludedProperties().add(propertyName);
        return this;
    }

    @Override
    public CompareOptionsConfigurer fullDiff(boolean value) {
        fullCompareConfig.setMinimalDiff(value);
        partialCompareConfig.setMinimalDiff(value);
        return this;
    }

    @Override
    public FullCompareOptionsConfigurer configureFullCompare(FullCompareConfigConfigurer configurer) {
        configurer.configure(fullCompareConfig);
        return this;
    }

    @Override
    public PartialCompareOptionsConfigurer configurePartial(PartialCompareConfigConfigurer configurer) {
        configurer.configure(partialCompareConfig);
        return this;
    }

    private boolean performEqualCheck() {
        RapidReflectionEquals equalMatcher = new RapidReflectionEquals(root, selectConfig());
        equalMatcher.matches(compare);
        this.diff=equalMatcher.getDiff();
        return diff.isEmpty();
    }

    private RapidEqualsBuilder.CompareConfig selectConfig() {
        if (fullCompare == null) {
            throw new IllegalArgumentException("No CompareMode selected");
        }
        if (fullCompare) {
            return fullCompareConfig;
        } else {
            return partialCompareConfig;
        }
    }

    @Override
    public ResultProvider assertEqual() {
        Assertions.assertTrue(performEqualCheck());
        return this;
    }

    @Override
    public ResultProvider assertNotEqual() {
        Assertions.assertFalse(performEqualCheck());
        return this;
    }

    @Override
    public ResultProvider go() {
        performEqualCheck();
        return this;
    }

    @Override
    public boolean isEqual() {
        return performEqualCheck();
    }

    @Override
    public RapidEqualsBuilder.Diff getDiff() {
        return diff;
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNull(boolean v) {
        this.fullCompareConfig.ignoreNull = v;
        return this;
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNotFound(boolean v) {
        this.fullCompareConfig.ignoreNotFound = v;
        return this;
    }


    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FullCompareConfig extends RapidEqualsBuilder.CompareConfig implements CompareTemplateConfig {


        public static Builder modDefault(){
            return new Builder();
        }

        public static Builder modGlobal(){
            return new Builder(getGlobalFullCompareConfig());
        }


        public boolean isIgnoreNull() {
            return ignoreNull;
        }

        public boolean isIgnoreNotFound() {
            return ignoreNotFound;
        }

        public boolean isUseNullForNotFound() {
            return useNullForNotFound;
        }

        public Set<String> getIgnoredProperties() {
            return ignoredProperties;
        }

        public void setIgnoreNull(boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
        }

        public void setIgnoreNotFound(boolean ignoreNotFound) {
            this.ignoreNotFound = ignoreNotFound;
        }

        public void setUseNullForNotFound(boolean useNullForNotFound) {
            this.useNullForNotFound = useNullForNotFound;
        }

        public void setIgnoredProperties(Set<String> ignoredProperties) {
            this.ignoredProperties = ignoredProperties;
        }


        public static class Builder extends CompareTemplateConfigBuilder<Builder,FullCompareConfig>{

            public Builder() {
                super(new FullCompareConfig());
            }

            public Builder(FullCompareConfig config) {
                super(config);
            }

            public Builder ignoreNull(boolean v){
                getConfig().setIgnoreNull(v);
                return this;
            }

            public Builder ignoreNotFound(boolean v){
                getConfig().setIgnoreNotFound(v);
                return this;
            }

            public Builder useNullForNotFound(boolean v){
                getConfig().setUseNullForNotFound(v);
                return this;
            }

            public Builder ignoredProperties(Set<String> ignored) {
                getConfig().setIgnoredProperties(ignored);
                return this;
            }

        }


    }

    @ToString(callSuper = true)
    public static class PartialCompareConfig extends RapidEqualsBuilder.CompareConfig implements CompareTemplateConfig{
        //default config
        private Set<String> includedProperties = new HashSet<>();

        public static Builder modDefault(){
            return new Builder();
        }

        public static Builder modGlobal(){
            return new Builder(getGlobalPartialCompareConfig());
        }

        public void setIncludedProperties(Set<String> includedProperties) {
            this.includedProperties = includedProperties;
        }

        public Set<String> getIncludedProperties() {
            return includedProperties;
        }

        @Override
        public void init(Object rootActor,Object compareActor) {
            Set<String> ignored = ReflectionUtils.getProperties(rootActor.getClass());
            ignored.removeAll(Sets.newHashSet(includedProperties));
            ignoredProperties = ignored;
        }

        public static class Builder extends CompareTemplateConfigBuilder<Builder,PartialCompareConfig>{

            public Builder() {
                super(new PartialCompareConfig());
            }

            public Builder(PartialCompareConfig config) {
                super(config);
            }

            public Builder includedProperties(Set<String> properties){
                getConfig().setIncludedProperties(properties);
                return this;
            }

        }

    }

    public static interface CompareTemplateConfig{

        @Getter
        public static abstract class CompareTemplateConfigBuilder<T extends CompareTemplateConfigBuilder, C extends RapidEqualsBuilder.CompareConfig> {
            private C config;

            public CompareTemplateConfigBuilder(C config) {
                this.config = config;
            }

            public T reflectUpToClass(Class<?> value){
                config.reflectUpToClass= value;
                return (T) this;
            }

            public T fullDiff(boolean value){
                config.minimalDiff = value;
                return (T) this;
            }

            public C build(){
                return config;
            }
        }


    }



}
