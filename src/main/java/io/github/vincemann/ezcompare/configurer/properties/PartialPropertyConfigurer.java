package io.github.vincemann.ezcompare.configurer.properties;

import com.github.hervian.reflection.Types;

public interface PartialPropertyConfigurer<M> {
    //menu options
    PartialAdditionalPropertyConfigurer<M> include(Types.Supplier<?>... getters);
    PartialAdditionalPropertyConfigurer<M> include(String... propertyNames);

}
