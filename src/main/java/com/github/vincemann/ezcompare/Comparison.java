package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;
import com.github.vincemann.ezcompare.config.FullCompareConfig;
import com.github.vincemann.ezcompare.config.PartialCompareConfig;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.github.vincemann.ezcompare.util.MethodNameUtil.propertyNamesOf;


/**
 * Fluent-API-style Wrapper for {@link RapidReflectionEquals}.
 * One instance represents ONE Comparison of two objects. Dont reuse!
 * <p>
 * Supports two Comparison Modes:
 * <p>
 * FullCompare (applied by using {@link SelectivePropertiesConfigurer#all()})
 * Uses all fields from {@link this#root} for comparison.
 * Fields may be ignored with {@link FullComparePropertyConfigurer#ignore(Types.Supplier[])}.
 * <p>
 * PartialCompare (applied by using {@link SelectivePropertiesConfigurer#include(Types.Supplier[])} )
 * Uses only explicitly included fields from root.
 * <p>
 * <p>
 *
 * Supports two Result Modes:
 * minimalDiff:          stops searching for differences when first is found. (can be useful for performence reasons)
 * fullDiff(default):    always finds all differences.
 * @see RapidEqualsBuilder.Diff
 *
 *
 * Create with {@link Comparator#compare(Object)}
 */
@Getter
@Setter
public class Comparison implements
        ActorConfigurer, SelectedActorConfigurer,
        SelectiveOptionsConfigurer, FullCompareOptionsConfigurer, PartialCompareOptionsConfigurer, CompareOptionsConfigurer,
        PropertyBridge,
        SelectivePropertiesConfigurer, FullComparePropertyConfigurer, PartialComparePropertyConfigurer, SelectedPartialComparePropertyConfigurer,
        OperationConfigurer,
        ResultProvider,
        ContinueBridge
{
    private final static Logger log = Logger.getLogger(Comparison.class.getName());

    /**
     * Local Config within scope of one Compare Process aka one {@link Comparison} instance.
     */
    private FullCompareConfig fullCompareConfig;
    private PartialCompareConfig partialCompareConfig;

    private Object root;
    private Object compare = new ArrayList<>();

    private RapidEqualsBuilder.Diff diff;
    private Boolean fullCompare = null;

    @Builder(access = AccessLevel.PROTECTED)
    protected Comparison(Object root,FullCompareConfig fullCompareConfig,PartialCompareConfig partialCompareConfig) {
        this.root = root;
        this.fullCompareConfig = fullCompareConfig;
        this.partialCompareConfig = partialCompareConfig;
    }


    @Override
    public SelectedActorConfigurer with(Object actor) {
        this.compare = actor;
        return this;
    }

    @Override
    public SelectivePropertiesConfigurer properties() {
        return this;
    }

    @Override
    public FullComparePropertyConfigurer ignore(Types.Supplier<?>... getters) {
        return ignore(propertyNamesOf(getters));
    }

    @Override
    public FullComparePropertyConfigurer ignore(String... propertyNames) {
        fullCompare = true;
        fullCompareConfig.getIgnoredProperties().addAll(Sets.newHashSet(propertyNames));
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
    public SelectedPartialComparePropertyConfigurer include(Types.Supplier<?>... getters) {
        return include(propertyNamesOf(getters));
    }

    @Override
    public SelectedPartialComparePropertyConfigurer include(String... propertyName) {
        fullCompare = false;
        partialCompareConfig.getIncludedProperties().addAll(Sets.newHashSet(propertyName));
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
        configurer.configure(fullCompareConfig.modify());
        return this;
    }

    @Override
    public PartialCompareOptionsConfigurer configurePartialCompare(PartialCompareConfigConfigurer configurer) {
        configurer.configure(partialCompareConfig.modify());
        return this;
    }

    protected boolean performEqualCheck() {
        RapidReflectionEquals equalMatcher = new RapidReflectionEquals(root, selectConfig());
        equalMatcher.matches(compare);
        this.diff = equalMatcher.getDiff();
        return diff.isEmpty();
    }

    protected RapidEqualsBuilder.CompareConfig selectConfig() {
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
        boolean equal = performEqualCheck();
        //get better assert msg in test
        getDiff().getDiffNodes().forEach(diffNode -> {
            Assertions.assertEquals(diffNode.getRootValue(),diffNode.getCompareValue());
        });
        Assertions.assertTrue(equal);
        return this;
    }

    @Override
    public ResultProvider assertNotEqual() {
        boolean equal = performEqualCheck();
        //get better assert msg in test
        getDiff().getDiffNodes().forEach(diffNode -> {
            Assertions.assertNotEquals(diffNode.getRootValue(),diffNode.getCompareValue());
        });
        Assertions.assertFalse(equal);
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

    @Override
    public SelectedActorConfigurer and() {
        return Comparator.compare(root).with(compare);
    }



}
