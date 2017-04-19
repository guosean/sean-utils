package com.sean.reflect;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.List;

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
        Class<?> targetClzz = object.getClass();
        do{
            try {
                Field field = targetClzz.getDeclaredField(fieldName);
                String methodPrefix = isBooleanType(field) ? PREFIX_IS : PREFIX_GET;
                String methodName = String.format("%s%s", methodPrefix, StringUtils.capitalize(fieldName));
                value = MethodUtils.invokeMethod(object, methodName, null);
            } catch (Exception e) {
                continue;
            } finally {
                targetClzz = targetClzz.getSuperclass();
            }
        }while(null!=targetClzz && targetClzz!=Object.class);

        return value;
    }

    public static void invokeSetterMethod(Object object, String field, Object value) {
        if (null == object || StringUtils.isBlank(field) || null == value) {
            return;
        }

        String methodName = String.format("set%s", StringUtils.capitalize(field));
        try {
            MethodUtils.invokeMethod(object, methodName, value);
        } catch (Exception e) {}
    }

    public static List<String> getFieldNames(Class clzz){
        Preconditions.checkNotNull(clzz,"clzz cannot be null");
        List<String> fieldNames = Lists.newArrayList();

        Class<?> targetClass = clzz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                fieldNames.add(field.getName());
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return fieldNames;
    }

    /**
     * 将srcObject中的filedValue set到targetObject对应字段
     * @param srcObject
     * @param targetObject
     */
    public static void cloneObject(Object srcObject,Object targetObject){
        Preconditions.checkNotNull(srcObject,"srcObject cannot not be null");
        Preconditions.checkNotNull(targetObject,"targetObject cannot be null");
        Class srcClzz = srcObject.getClass();
        List<String> srcFileds = getFieldNames(srcClzz);
        if(CollectionUtils.isNotEmpty(srcFileds)){
            for(String field:srcFileds){
                try {
                    Object fieldValue = invokeGetterMethod(srcObject,field);
                    invokeSetterMethod(targetObject,field,fieldValue);
                } catch (Exception e) {}
            }
        }

    }

    private static boolean isBooleanType(Field field){
        if(null == field){
            return false;
        }
        String typeName = field.getName();
        return StringUtils.equals(Boolean.class.getName(),typeName) || StringUtils.equals(TYPE_BOOLEAN,typeName);
    }

}

