package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.util.ReflectionUtils;
import com.google.common.collect.Sets;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Config for Partial Compare Mode.
 *
 * @see RapidEqualsBuilder.CompareConfig
 * @see Comparison for mode & config info
 */
@ToString(callSuper = true)
public class PartialCompareConfig extends RapidEqualsBuilder.CompareConfig{
    //default config
    /**
     * Properties/Fields that shall be compared.
     */
    private Set<String> includedProperties = new HashSet<>();


    public Modifier<?> modify() {
        return new Modifier<>(this);
    }

    public Set<String> getIncludedProperties() {
        return includedProperties;
    }

    public void setIncludedProperties(Set<String> includedProperties) {
        this.includedProperties = includedProperties;
    }

    @Override
    public void init(Object root, Object compare) {
        Set<String> ignored = ReflectionUtils.getProperties(root.getClass());
        ignored.removeAll(Sets.newHashSet(includedProperties));
        ignoredProperties = ignored;
    }

    public static class Modifier<M extends Modifier> extends AbstractConfigModifier<M, PartialCompareConfig> {

        public Modifier(PartialCompareConfig config) {
            super(config);
        }

        public M includeProperties(Set<String> properties) {
            getConfig().setIncludedProperties(properties);
            return (M) this;
        }
    }

    public static class Builder extends Modifier<Builder> {

        Builder() {
            super(new PartialCompareConfig());
        }

        Builder(PartialCompareConfig config) {
            super(config);
        }

        PartialCompareConfig build() {
            return getConfig();
        }

    }

}
