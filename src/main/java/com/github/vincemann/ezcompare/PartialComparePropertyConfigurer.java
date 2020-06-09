package com.github.vincemann.ezcompare;

import com.github.hervian.reflection.Types;

public interface PartialComparePropertyConfigurer {
    SelectedPartialComparePropertyConfigurer include(Types.Supplier<?>... getters);
    SelectedPartialComparePropertyConfigurer include(String... propertyNames);

}
