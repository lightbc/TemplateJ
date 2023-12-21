package com.lightbc.templatej.utils;

import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射调用工具类
 */
class ReflectUtil {
    private Method method;

    /**
     * 获取指定类的指定方法
     *
     * @param aClass     指定类
     * @param name       方法名
     * @param parameters 方法传参
     * @return this
     */
    ReflectUtil getMethod(@NotNull Class<?> aClass, @NonNls @NotNull String name, @NotNull Class... parameters) {
        this.method = ReflectionUtil.getMethod(aClass, name, parameters);
        return this;
    }

    /**
     * 调用
     *
     * @param obj  调用对象
     * @param args 传参
     * @return object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    Object invoke(Object obj, Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, args);
    }


}
