package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

import static com.github.vincemann.ezcompare.util.MethodNameUtil.propertyNameOf;

/**
 * Config for fullCompare Mode.
 *
 * @see RapidEqualsBuilder.CompareConfig
 * @see Comparison for mode & config info
 */
@Getter
@Setter
@ToString(callSuper = true)
public class FullCompareConfig  extends RapidEqualsBuilder.CompareConfig{


    public FullCompareConfig.Modifier<?> modify() {
        return new FullCompareConfig.Modifier<>(this);
    }


    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public boolean isIgnoreNotFound() {
        return ignoreNotFound;
    }

    public void setIgnoreNotFound(boolean ignoreNotFound) {
        this.ignoreNotFound = ignoreNotFound;
    }

    public boolean isUseNullForNotFound() {
        return useNullForNotFound;
    }

    public void setUseNullForNotFound(boolean useNullForNotFound) {
        this.useNullForNotFound = useNullForNotFound;
    }

    public Set<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    public void setIgnoredProperties(Set<String> ignoredProperties) {
        this.ignoredProperties = ignoredProperties;
    }

    public void ignoreProperty(String ignored) {
        getIgnoredProperties().add(ignored);
    }

    public void ignoreProperty(Types.Supplier<?> ignored) {
        getIgnoredProperties().add(propertyNameOf(ignored));
    }

    public static class Modifier<M extends Modifier> extends AbstractConfigModifier<M, FullCompareConfig> {

        Modifier(FullCompareConfig config) {
            super(config);
        }

        public M ignoreNull(boolean v) {
            getConfig().setIgnoreNull(v);
            return (M) this;
        }

        public M ignoreNotFound(boolean v) {
            getConfig().setIgnoreNotFound(v);
            return (M) this;
        }

        public M useNullForNotFound(boolean v) {
            getConfig().setUseNullForNotFound(v);
            return (M) this;
        }

        public M ignoreProperty(String ignored) {
            getConfig().ignoreProperty(ignored);
            return (M) this;
        }

        public M ignoreProperty(Types.Supplier<?> ignored) {
            getConfig().ignoreProperty(ignored);
            return (M) this;
        }

        public M ignoreProperties(Set<String> ignored) {
            getConfig().setIgnoredProperties(ignored);
            return (M) this;
        }
    }

    public static class Builder extends Modifier<Builder> {

        Builder() {
            super(new FullCompareConfig());
        }

        Builder(FullCompareConfig config) {
            super(config);
        }

        FullCompareConfig build() {
            return getConfig();
        }
    }
}
