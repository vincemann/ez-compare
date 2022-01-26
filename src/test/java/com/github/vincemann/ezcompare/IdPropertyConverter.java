package com.github.vincemann.ezcompare;

import com.github.vincemann.ezcompare.converters.PropertyConverter;
import com.github.vincemann.ezcompare.domain.IdentifiableEntity;
import com.github.vincemann.ezcompare.util.IdPropertyNameUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;

public class IdPropertyConverter<S, T> extends PropertyConverter<Serializable, IdentifiableEntity> {

    @Override
    public boolean propertiesMatch(Object src, String srcPropertyName, Object dst, String dstPropertyName) {
        boolean srcSoloIdProperty = IdPropertyNameUtils.isIdField(srcPropertyName);
        boolean srcCollectionIdProperty = IdPropertyNameUtils.isCollectionIdField(srcPropertyName);

        boolean dstSoloIdProperty = IdPropertyNameUtils.isIdField(dstPropertyName);
        boolean dstCollectionIdProperty = IdPropertyNameUtils.isCollectionIdField(dstPropertyName);

        if (srcSoloIdProperty){
            if (getSrcType().isAssignableFrom(src.getClass()) && getDstType().isAssignableFrom(dst.getClass())){
                return true;
            }
        }
        if (srcCollectionIdProperty){
            if (getSrcType().isAssignableFrom(src.getClass()) && Collection.class.isAssignableFrom(dst.getClass())){
                return collectionMatchesDstType(dst);
            }
        }


        if (dstSoloIdProperty){
            if (getDstType().isAssignableFrom(dst.getClass()) && getSrcType().isAssignableFrom(src.getClass())){
                return true;
            }
        }
        if (dstCollectionIdProperty){
            if (getDstType().isAssignableFrom(dst.getClass()) && Collection.class.isAssignableFrom(src.getClass())){
                return collectionMatchesSrcType(src);
            }
        }

        return false;
    }


    protected boolean collectionMatchesDstType(Object o){
        boolean empty = ((Collection<?>) o).isEmpty();
        if (empty){
            System.err.println("src collection was emtpy, cant match with dst collection");
            return false;
        }
        Object element = ((Collection<?>) o).stream().findFirst().get();
        return getDstType().isAssignableFrom(element.getClass());
    }


    protected boolean collectionMatchesSrcType(Object o){
        boolean empty = ((Collection<?>) o).isEmpty();
        if (empty){
            System.err.println("dst collection was emtpy, cant match with src collection");
            return false;
        }
        Object element = ((Collection<?>) o).stream().findFirst().get();
        return getSrcType().isAssignableFrom(element.getClass());
    }

    @Override
    public boolean isEqual(Serializable src, IdentifiableEntity target) {
        return false;
    }
}
