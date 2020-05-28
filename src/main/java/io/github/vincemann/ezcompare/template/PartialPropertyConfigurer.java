package io.github.vincemann.ezcompare.template;

import com.github.hervian.reflection.Types;

public interface PartialPropertyConfigurer {
    PartialAdditionalPropertyConfigurer include(Types.Supplier<?> getter);
    PartialAdditionalPropertyConfigurer include(String propertyName);

}
