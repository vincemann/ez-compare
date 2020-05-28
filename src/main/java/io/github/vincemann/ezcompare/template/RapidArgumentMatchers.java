package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;
import com.google.common.collect.Sets;
import io.github.vincemann.ezcompare.template.CompareTemplate;
import io.github.vincemann.ezcompare.template.RapidReflectionEquals;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

public class RapidArgumentMatchers {

    /**
     * Same as {@link org.mockito.ArgumentMatchers#refEq(Object, String...)}, but creates and logs minimal Diff, if objects differ.
     * Only takes fields from root (expected) object class into consideration.
     * Does not perform deep compare -> only scans fields from root and performs equal on all of those (depth = 1)
     * Can compare diff Types, if field names match.
     *
     * @param value
     * @param <T>
     * @return
     */
    public static <T> T fullRefEq(T value, String... ignoredProperties) {
        return fullRefEq(value,
                CompareTemplate.FullCompareConfig.builder()
                        .ignoredProperties(Sets.newHashSet(ignoredProperties))
                        .build()
        );
    }


    public static <T> T fullRefEq(T value, Types.Supplier<?>... ignoredProperties) {
        return fullRefEq(value,
                Arrays.stream(ignoredProperties)
                        .map(MethodNameUtil::propertyNameOf)
                        .distinct()
                        .toArray(String[]::new)
        );
    }

    public static <T> T fullRefEq(T value, CompareTemplate.FullCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(value, config.convert()));
        return null;
    }


    public static <T> T partialRefEq(T value,) {
        reportMatcher(new RapidReflectionEquals(value, ignoreNull, excludeFields));
        return null;
    }


    public static <T> T rapidRefEq(T value, CompareTemplate.FullCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(value, config));
        return null;
    }

    private static void reportMatcher(ArgumentMatcher<?> matcher) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
    }
}
