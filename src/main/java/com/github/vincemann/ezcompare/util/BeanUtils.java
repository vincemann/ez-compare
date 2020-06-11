package com.github.vincemann.ezcompare.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class BeanUtils {

    private BeanUtils(){}

    /**
     * Shallow Copy!
     */
    public static <T> T clone(T o){
        try {
            return (T) BeanUtilsBean.getInstance().cloneBean(o);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
