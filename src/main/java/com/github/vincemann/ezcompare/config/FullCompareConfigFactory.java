package com.github.vincemann.ezcompare.config;

import com.github.vincemann.ezcompare.util.BeanUtils;
import com.google.common.collect.Sets;

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
        FullCompareConfig global = getFullCompareGlobalConfig();
        FullCompareConfig cloned = BeanUtils.clone(global);
        cloned.setIgnoredProperties(Sets.newHashSet(global.getIgnoredProperties()));
        return new FullCompareConfig.Builder(cloned);
    }

    /**
     * Creates Config based on GlobalConfig. (clone)
     */
    public static FullCompareConfig createBasedOnGlobal() {
        return buildBasedOnGlobal().build();
    }
}
