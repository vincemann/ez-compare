package io.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;

public interface PartialPropertyConfigurer {
    PartialAdditionalPropertyConfigurer include(Types.Supplier<?>... getters);
    PartialAdditionalPropertyConfigurer include(String... propertyNames);

}
