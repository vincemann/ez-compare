package com.github.vincemann.ezcompare.config;

import com.github.vincemann.ezcompare.util.BeanUtils;
import com.google.common.collect.Sets;

import static com.github.vincemann.ezcompare.Comparator.getPartialCompareGlobalConfig;

public class PartialCompareConfigFactory {

    /**
     * Creates Config based on default Config.
     *
     * @see this#buildBasedOnGlobal()
     */
    public static PartialCompareConfig.Builder buildDefault() {
        return new PartialCompareConfig.Builder();
    }

    public static PartialCompareConfig createDefault() {
        return buildDefault().build();
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static PartialCompareConfig.Builder buildBasedOnGlobal() {
        PartialCompareConfig global = getPartialCompareGlobalConfig();
        PartialCompareConfig cloned = BeanUtils.clone(global);
        cloned.setIncludedProperties(Sets.newHashSet(global.getIncludedProperties()));
        return new PartialCompareConfig.Builder(cloned);
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static PartialCompareConfig createBasedOnGlobal() {
        return buildBasedOnGlobal().build();
    }
}
