package com.mu.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Peng Mu
 */
public class ReflectionUtil
{
    public static boolean isGetter(Method method)
    {
        if (!method.getName().startsWith("get"))
        {
            return false;
        }
        if (method.getParameterTypes().length != 0)
        {
            return false;
        }
        if (void.class.equals(method.getReturnType()))
        {
            return false;
        }
        if(!Modifier.isPublic(method.getModifiers()))
            return false;
        return true;
    }

    public static boolean isSetter(Method method)
    {
        if (!method.getName().startsWith("set"))
        {
            return false;
        }
        if (method.getParameterTypes().length != 1)
        {
            return false;
        }
        if(!Modifier.isPublic(method.getModifiers()))
            return false;
        return true;
    }

    public static boolean isImplemented(Object o, Class implemented)
    {
        Class[] arr = o.getClass().getInterfaces();
        if(arr == null)
            return false;
        for(Class c : arr)
            if(c.equals(implemented))
                return true;
        return false;
    }

}
