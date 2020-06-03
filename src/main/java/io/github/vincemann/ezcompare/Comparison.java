package io.github.vincemann.ezcompare;

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


/**
 * Fluent-API-like Wrapper for {@link RapidReflectionEquals}.
 * Represents ONE Comparison of two objects. Dont reuse!
 *
 * Supports two Comparison Modes:
 *
 * FullCompare (applied by using {@link SelectivePropertiesConfigurer#all()})
 * Uses all fields from {@link this#root} for comparison.
 * Fields may be ignored with {@link FullComparePropertyConfigurer#ignore(Types.Supplier)}.
 *
 * PartialCompare (applied by using {@link SelectivePropertiesConfigurer#include(Types.Supplier)})
 * Uses only explicitly included fields from root.
 *
 *
 * Supports local and global Configuration for each mode:
 * FullCompare:     local:  {@link FullCompareOptionsConfigurer#configureFullCompare(FullCompareConfigConfigurer)}
 *                  global: {@link this#GLOBAL_FULL_COMPARE_CONFIG}
 *                  @see FullCompareConfig
 *
 * PartialCompare:  local:  {@link PartialCompareOptionsConfigurer#configurePartial(PartialCompareConfigConfigurer)}
 *                  global: {@link this#GLOBAL_PARTIAL_COMPARE_CONFIG}
 *                  @see PartialCompareConfig
 *
 *
 * Supports two Result Modes:
 * minimalDiff: stops searching for differences when first is found. (can be useful for performence reasons)
 * fullDiff:    always finds all differences.
 * @see RapidEqualsBuilder.Diff
 */
@Getter
@Setter
public class Comparison implements
        ActorConfigurer, ActorBridge,
        SelectiveOptionsConfigurer, FullCompareOptionsConfigurer, PartialCompareOptionsConfigurer,CompareOptionsConfigurer,
        PropertyBridge,
        SelectivePropertiesConfigurer, FullComparePropertyConfigurer, PartialPropertyConfigurer, PartialAdditionalPropertyConfigurer,
        OperationConfigurer,
        ResultProvider
{
    private final static Logger log = Logger.getLogger(Comparison.class.getName());

    /**
     * Comparison is initialized with Global Config, if present.
     * Otherwise default Configs get created.
     */

    public static PartialCompareConfig  GLOBAL_PARTIAL_COMPARE_CONFIG;
    public static FullCompareConfig     GLOBAL_FULL_COMPARE_CONFIG;


    /**
     * Local Config within scope of one Compare Process aka one {@link Comparison}.
     */
    private FullCompareConfig           fullCompareConfig;
    private PartialCompareConfig        partialCompareConfig;

    private Object root;
    private Object compare = new ArrayList<>();

    private RapidEqualsBuilder.Diff diff;
    private Boolean fullCompare = null;

    protected Comparison(Object root) {
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

    /**
     * Create with this method.
     */
    public static ActorConfigurer compare(Object root) {
        return new Comparison(root);
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
     * If you want to add some of them anyways for this specific comparison, use {@link this#configureFullCompare(FullCompareConfigConfigurer)}
     * and remove them from {@link FullCompareConfig#ignoredProperties} for this specific CompareTemplate.
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
    public CompareOptionsConfigurer fullDiff(boolean v) {
        fullCompareConfig.setMinimalDiff(v);
        partialCompareConfig.setMinimalDiff(v);
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


    /**
     * Config for fullCompare Mode.
     * @see RapidEqualsBuilder.CompareConfig
     * @see Comparison for mode & config info
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FullCompareConfig extends RapidEqualsBuilder.CompareConfig implements CompareTemplateConfig {


        /**
         * Create Default Config wrapped in Builder, ready for modification.
         * Global Config is not included.
         *
         * @see this#modGlobal()
         */
        public static Builder modDefault(){
            return new Builder();
        }

        /**
         * Get Global Config wrapped in Builder, ready for modification.
         *
         */
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

    /**
     * Config for Partial Compare Mode.
     *
     *
     * @see RapidEqualsBuilder.CompareConfig
     * @see Comparison for mode & config info
     */
    @ToString(callSuper = true)
    public static class PartialCompareConfig extends RapidEqualsBuilder.CompareConfig implements CompareTemplateConfig{
        //default config
        /**
         * Properties/Fields that shall be compared.
         */
        private Set<String> includedProperties = new HashSet<>();


        /**
         * Create Default Config wrapped in Builder, ready for modification.
         * Global Config is not included.
         *
         * @see this#modGlobal()
         */
        public static Builder modDefault(){
            return new Builder();
        }

        /**
         * Get Global Config wrapped in Builder, ready for modification.
         */
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
        public void init(Object root,Object compare) {
            Set<String> ignored = ReflectionUtils.getProperties(root.getClass());
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

    public interface CompareTemplateConfig{

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
