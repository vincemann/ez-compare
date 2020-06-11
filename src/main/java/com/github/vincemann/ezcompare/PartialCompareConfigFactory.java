package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.util.BeanUtils;

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
        return new PartialCompareConfig.Builder(BeanUtils.clone(getPartialCompareGlobalConfig()));
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static PartialCompareConfig createBasedOnGlobal() {
        return buildBasedOnGlobal().build();
    }
}
