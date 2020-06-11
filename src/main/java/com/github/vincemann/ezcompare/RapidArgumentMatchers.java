package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;
import com.google.common.collect.Sets;
import org.mockito.ArgumentMatcher;

import static com.github.vincemann.ezcompare.util.MethodNameUtil.propertyNamesOf;
import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

/**
 * Mockito Argument Matchers using {@link RapidReflectionEquals} for matching.
 *
 * @see Comparison, {@link RapidEqualsBuilder.CompareConfig}
 */
public class RapidArgumentMatchers {


    public static <T> T fullRefEq(T root, String... ignoredProperties) {
        return fullRefEq(
                root,
                FullCompareConfigFactory.buildBasedOnGlobal()
                        .ignoreProperties(Sets.newHashSet(ignoredProperties))
                        .build()
        );
    }

    public static <T> T fullRefEq(T root, Types.Supplier<?>... ignoredGetter) {
        return fullRefEq(root, propertyNamesOf(ignoredGetter));
    }

    public static <T> T fullRefEq(T root, FullCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(root, config));
        return null;
    }




    public static <T> T partialRefEq(T root, Types.Supplier<?>... includedGetter) {
        partialRefEq(root, propertyNamesOf(includedGetter));
        return null;
    }

    public static <T> T partialRefEq(T root, String... includedProperties) {
        return partialRefEq(
                root,
                PartialCompareConfigFactory.buildBasedOnGlobal()
                        .includeProperties(Sets.newHashSet(includedProperties))
                        .build()
        );
    }

    public static <T> T partialRefEq(T root, PartialCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(root, config));
        return null;
    }




    private static void reportMatcher(ArgumentMatcher<?> matcher) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
    }


}
