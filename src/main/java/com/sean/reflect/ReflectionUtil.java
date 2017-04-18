package com.sean.reflect;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;

/**
 * Created by guozhenbin on 2017/3/3.
 */
public class ReflectionUtil {

    private static String TYPE_BOOLEAN = "boolean";
    private static String PREFIX_IS = "is";
    private static String PREFIX_GET = "get";

    public static Object invokeGetterMethod(Object object, String fieldName) {
        if (null == object || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Object value = null;
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            String methodPrefix = isBooleanType(field) ? PREFIX_IS : PREFIX_GET;
            String methodName = String.format("%s%s", methodPrefix, StringUtils.capitalize(fieldName));
            value = MethodUtils.invokeMethod(object, methodName, null);
        } catch (Exception e) {
        }

        return value;
    }

    public static void invokeSetterMethod(Object object, String field, Object value) {
        if (null == object || StringUtils.isBlank(field)) {
            return;
        }

        String methodName = String.format("set%s", StringUtils.capitalize(field));
        try {
            MethodUtils.invokeMethod(object, methodName, value);
        } catch (Exception e) {
        }
    }

    private static boolean isBooleanType(Field field){
        if(null == field){
            return false;
        }
        String typeName = field.getName();
        //考虑基础类型boolean 和 Boolean 两种不同的type
        return StringUtils.equals(Boolean.class.getName(),typeName) || StringUtils.equals(TYPE_BOOLEAN,typeName);
    }

}

