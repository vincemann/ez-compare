package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.converters.PropertyConverter;

public interface ConverterConfigurer {
    public <S,T> SelectedActorConfigurer useConverter(PropertyConverter<S,T> propertyConverter);
}
