package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.config.FullCompareConfig;
import com.github.vincemann.ezcompare.config.FullCompareConfigFactory;
import com.github.vincemann.ezcompare.config.PartialCompareConfig;
import com.github.vincemann.ezcompare.config.PartialCompareConfigFactory;

/**
 * Factory for {@link Comparison}.
 * Holds Global Compare Configuration.
 * Supports local and global Configuration for each mode:
 * <p>
 * FullCompare:
 * local:  {@link FullCompareOptionsConfigurer#configureFullCompare(FullCompareOptionsConfigurer.FullCompareConfigConfigurer)}
 * global: {@link this#FULL_COMPARE_GLOBAL_CONFIG}
 *
 * @see FullCompareConfig
 *
 * <p>
 * PartialCompare:
 * local:  {@link PartialCompareOptionsConfigurer#configurePartialCompare(PartialCompareOptionsConfigurer.PartialCompareConfigConfigurer)}
 * global: {@link this#PARTIAL_COMPARE_GLOBAL_CONFIG}
 * @see PartialCompareConfig
 * <p>
 * <p>
 */
public class Comparator {

    /**
     * Comparison is initialized with Global Config, if present.
     * Otherwise default Configs get created.
     */
    private static PartialCompareConfig PARTIAL_COMPARE_GLOBAL_CONFIG;
    private static FullCompareConfig FULL_COMPARE_GLOBAL_CONFIG;

    private Comparator() {
    }

    /**
     * Create with this method.
     */
    public static ActorConfigurer compare(Object root) {
        return Comparison.builder()
                .root(root)
                .fullCompareConfig(FullCompareConfigFactory.buildBasedOnGlobal().build())
                .partialCompareConfig(PartialCompareConfigFactory.buildBasedOnGlobal().build())
                .build();
    }

    /**
     * Use {@link FullCompareConfig#modify()} for modification.
     */
    public static FullCompareConfig getFullCompareGlobalConfig() {
        if (FULL_COMPARE_GLOBAL_CONFIG == null) {
            FULL_COMPARE_GLOBAL_CONFIG = new FullCompareConfig();
        }
        return FULL_COMPARE_GLOBAL_CONFIG;
    }

    public static void setFullCompareGlobalConfig(FullCompareConfig fullCompareGlobalConfig) {
        FULL_COMPARE_GLOBAL_CONFIG = fullCompareGlobalConfig;
    }

    /**
     * Use {@link PartialCompareConfig#modify()}for modification.
     */
    public static PartialCompareConfig getPartialCompareGlobalConfig() {
        if (PARTIAL_COMPARE_GLOBAL_CONFIG == null) {
            PARTIAL_COMPARE_GLOBAL_CONFIG = new PartialCompareConfig();
        }
        return PARTIAL_COMPARE_GLOBAL_CONFIG;
    }

    public static void setPartialCompareGlobalConfig(PartialCompareConfig partialCompareGlobalConfig) {
        PARTIAL_COMPARE_GLOBAL_CONFIG = partialCompareGlobalConfig;
    }

    /**
     * Resets global configuration to default config.
     */
    public static void resetGlobalConfig() {
        setFullCompareGlobalConfig(FullCompareConfigFactory.buildDefault().build());
        setPartialCompareGlobalConfig(PartialCompareConfigFactory.buildDefault().build());
    }



}
