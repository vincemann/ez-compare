package io.github.vincemann.ezcompare.configurer.properties;

import com.github.hervian.reflection.Types;

public interface PartialComparePropertyConfigurer
        //menu options
{
        SelectedPartialComparePropertyConfigurer include(Types.Supplier<?>... getters);
        SelectedPartialComparePropertyConfigurer include(String... propertyNames);

}
