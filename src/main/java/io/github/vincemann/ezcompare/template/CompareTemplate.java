package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;
import com.google.common.collect.Sets;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import io.github.vincemann.ezcompare.util.ReflectionUtils;
import lombok.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Getter
@Setter
public class CompareTemplate implements
        ActorConfigurer, AdditionalActorConfigurer,
        SelectiveOptionsConfigurer, FullCompareOptionsConfigurer, PartialCompareOptionsConfigurer,
        PropertyBridge,
        SelectivePropertiesConfigurer, FullComparePropertyConfigurer, PartialPropertyConfigurer, PartialAdditionalPropertyConfigurer,
        OperationConfigurer,
        ResultProvider
{
    private final static Logger log = Logger.getLogger(CompareTemplate.class.getName());

    public static PartialCompareConfig GLOBAL_PARTIAL_COMPARE_CONFIG;
    public static FullCompareConfig GLOBAL_FULL_COMPARE_CONFIG;


    private FullCompareConfig fullCompareConfig;
    private PartialCompareConfig partialCompareConfig;

    private Object rootActor;
    private List<Object> actors = new ArrayList<>();
    private Set<String> properties = new HashSet<>();

    private RapidEqualsBuilder.MinimalDiff minimalDiff;
    private Boolean fullCompare = null;

    protected CompareTemplate(Object rootActor) {
        this.rootActor = rootActor;
        initConfig();
    }

    private void initConfig(){
        this.fullCompareConfig =
                GLOBAL_FULL_COMPARE_CONFIG == null
                        ? FullCompareConfig.createDefault().build()
                        : GLOBAL_FULL_COMPARE_CONFIG;
        this.partialCompareConfig =
                GLOBAL_PARTIAL_COMPARE_CONFIG == null
                        ? PartialCompareConfig.createDefault().build()
                        : GLOBAL_PARTIAL_COMPARE_CONFIG;
    }

    public static ActorConfigurer compare(Object rootActor) {
        return new CompareTemplate(rootActor);
    }

    @Override
    public AdditionalActorConfigurer with(Object actor) {
        actors.add(actor);
        return this;
    }

    @Override
    public SelectivePropertiesConfigurer properties() {
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(Types.Supplier<?> getter) {
        String propertyName = MethodNameUtil.propertyNameOf(getter);
        Assertions.assertTrue(properties.contains(propertyName), "No Property known named: " + propertyName + " that could be ignored.");
        properties.remove(propertyName);
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(String propertyName) {
        Assertions.assertTrue(properties.contains(propertyName), "No Property known named: " + propertyName + " that could be ignored.");
        properties.remove(propertyName);
        return this;
    }

    @Override
    public FullComparePropertyConfigurer allOf(Object o) {
        fullCompare = true;
        Assertions.assertNotNull(o);
        Assertions.assertTrue(actors.contains(o) || rootActor.equals(o));
        properties.addAll(ReflectionUtils.getProperties(o.getClass()));
        return this;
    }

    @Override
    public FullComparePropertyConfigurer all() {
        fullCompare = true;
        properties.addAll(ReflectionUtils.getProperties(rootActor.getClass()));
        return this;
    }

    @Override
    public PartialAdditionalPropertyConfigurer include(Types.Supplier<?> getter) {
        fullCompare = false;
        include(MethodNameUtil.propertyNameOf(getter));
        return this;
    }

    @Override
    public PartialAdditionalPropertyConfigurer include(String propertyName) {
        fullCompare = false;
        properties.add(propertyName);
        return this;
    }


    @Override
    public FullCompareOptionsConfigurer configureFullCompare(OptionsConfigurer<FullCompareConfig> configurer) {
        configurer.configure(fullCompareConfig);
        return this;
    }

    @Override
    public PartialCompareOptionsConfigurer configurePartialCompare(OptionsConfigurer<PartialCompareConfig> configurer) {
        configurer.configure(partialCompareConfig);
        return this;
    }


    private boolean performEqualCheck() {
        RapidReflectionEquals equalMatcher = new RapidReflectionEquals(rootActor,
                selectConfig().convert(rootActor));
        boolean finalEqual = true;
        for (Object actor : actors) {
            boolean equal = equalMatcher.matches(actor);
            if (!equal) {
                finalEqual = false;
            }
//            Assertions.assertTrue(equal,"Objects differ. See last log above for diff or enable logging for: io.github.vincemann.springrapid.coretest");
        }
        this.minimalDiff = equalMatcher.getMinimalDiff();
        return finalEqual;
    }

    private AbstractCompareConfig selectConfig() {
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
        return minimalDiff.isEmpty();
    }

    @Override
    public RapidEqualsBuilder.MinimalDiff getDiff() {
        return minimalDiff;
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNull(boolean value) {
        this.fullCompareConfig.setIgnoreNull(value);
        return this;
    }

    @Override
    public FullCompareOptionsConfigurer ignoreNotFound(boolean value) {
        this.fullCompareConfig.setIgnoreNotFound(value);
        return this;
    }


    @Getter
    @Setter
    public static class FullCompareConfig extends AbstractCompareConfig {
        //default config
        private Boolean ignoreNull = Boolean.FALSE;
        private Boolean ignoreNotFound = Boolean.FALSE;
        private Boolean useNullForNotFound = Boolean.FALSE;
        private Set<String> ignoredProperties = new HashSet<>();


        @Builder(builderMethodName = "createDefault")
        public FullCompareConfig(Class<?> reflectUpToClass, Boolean ignoreNull, Boolean ignoreNotFound, Boolean useNullForNotFound, Set<String> ignoredProperties) {
            super(reflectUpToClass);
            if (ignoreNull!=null)
                this.ignoreNull = ignoreNull;
            if (ignoreNotFound!=null)
                this.ignoreNotFound = ignoreNotFound;
            if (useNullForNotFound!=null)
                this.useNullForNotFound = useNullForNotFound;
            if (ignoredProperties!=null)
                this.ignoredProperties = ignoredProperties;
        }



        @Override
        protected RapidEqualsBuilder.CompareConfig convert(Object rootActor) {
            RapidEqualsBuilder.CompareConfig result = super.convert(rootActor);
            result.setIgnoredProperties(getIgnoredProperties());
            result.setIgnoreNotFound(ignoreNotFound);
            result.setUseNullForNotFound(useNullForNotFound);
            result.setIgnoreNull(ignoreNull);
            return result;
        }

    }

    @Getter
    @Setter
    public static class PartialCompareConfig extends AbstractCompareConfig {
        //default
        private Set<String> includedProperties = new HashSet<>();


        @Builder(builderMethodName = "createDefault")
        public PartialCompareConfig(Class<?> reflectUpToClass,Set<String> includedProperties) {
            super(reflectUpToClass);
            if (includedProperties!=null)
                this.includedProperties = includedProperties;
        }

        @Override
        protected RapidEqualsBuilder.CompareConfig convert(Object rootActor) {
            RapidEqualsBuilder.CompareConfig result = super.convert(rootActor);
            Set<String> ignored = ReflectionUtils.getProperties(rootActor.getClass());
            ignored.removeAll(Sets.newHashSet(includedProperties));
            result.setIgnoredProperties(ignored);
            return result;
        }


    }

    @Getter
    @Setter
    static abstract class AbstractCompareConfig {
        //default
        private Class<?> reflectUpToClass = null;

        public AbstractCompareConfig(Class<?> reflectUpToClass) {
            this.reflectUpToClass = reflectUpToClass;
        }


        protected RapidEqualsBuilder.CompareConfig convert(Object rootActor) {
            RapidEqualsBuilder.CompareConfig result = RapidEqualsBuilder.CompareConfig.createDefault();
            result.setReflectUpToClass(getReflectUpToClass());
            return result;
        }

    }


}
