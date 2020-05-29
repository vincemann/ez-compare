package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;
import com.google.common.collect.Sets;
import io.github.vincemann.ezcompare.util.MethodNameUtil;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

public class RapidArgumentMatchers {

    /**
     * Same as {@link org.mockito.ArgumentMatchers#refEq(Object, String...)}, but creates and logs minimal Diff, if objects differ.
     * Only takes fields from root (expected) object class into consideration.
     * Does not perform deep compare -> only scans fields from root and performs equal on all of those (depth = 1)
     * Can compare diff Types, if field names match.
     *
     * @param root  root Actor of comparison, see {@link CompareTemplate#getRootActor()}
     * @param <T>
     * @return
     */
    public static <T> T fullRefEq(T root, String... ignoredProperties) {
        return fullRefEq(
                root,
                CompareTemplate.FullCompareConfig.createDefault()
                        .ignoredProperties(Sets.newHashSet(ignoredProperties))
                        .build()
        );
    }

    public static <T> T fullRefEq(T root, Types.Supplier<?>... ignoredGetter) {
        return fullRefEq(root, getPropertyNamesOf(ignoredGetter));
    }

    public static <T> T fullRefEq(T root, CompareTemplate.FullCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(root, config.convert(root)));
        return null;
    }




    public static <T> T partialRefEq(T root, Types.Supplier<?>... includedGetter) {
        partialRefEq(root, getPropertyNamesOf(includedGetter));
        return null;
    }

    public static <T> T partialRefEq(T root, String... includedProperties) {
        return partialRefEq(
                root,
                CompareTemplate.PartialCompareConfig.createDefault()
                        .includedProperties(Sets.newHashSet(includedProperties))
                        .build()
        );
    }

    public static <T> T partialRefEq(T root, CompareTemplate.PartialCompareConfig config) {
        reportMatcher(new RapidReflectionEquals(root, config.convert(root)));
        return null;
    }




    private static void reportMatcher(ArgumentMatcher<?> matcher) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
    }

    private static String[] getPropertyNamesOf(Types.Supplier<?>... getter) {
        return Arrays.stream(getter)
                .map(MethodNameUtil::propertyNameOf)
                .distinct()
                .toArray(String[]::new);
    }
}
