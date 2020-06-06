package io.github.vincemann.ezcompare.configurer.properties;

import com.github.hervian.reflection.Types;

public interface PartialPropertyConfigurer {
    //menu options
    PartialAdditionalPropertyConfigurer include(Types.Supplier<?>... getters);
    PartialAdditionalPropertyConfigurer include(String... propertyNames);

}
