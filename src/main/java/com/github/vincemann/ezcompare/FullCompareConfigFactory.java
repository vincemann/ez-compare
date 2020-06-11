package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.util.BeanUtils;

import static com.github.vincemann.ezcompare.Comparator.getFullCompareGlobalConfig;

public class FullCompareConfigFactory {

    /**
     * Creates Config based on default Config.
     *
     * @see this#buildBasedOnGlobal()
     */
    public static FullCompareConfig.Builder buildDefault() {
        return new FullCompareConfig.Builder();
    }

    public static FullCompareConfig createDefault() {
        return buildDefault().build();
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static FullCompareConfig.Builder buildBasedOnGlobal() {
        return new FullCompareConfig.Builder(BeanUtils.clone(getFullCompareGlobalConfig()));
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static FullCompareConfig createBasedOnGlobal() {
        return buildBasedOnGlobal().build();
    }
}
