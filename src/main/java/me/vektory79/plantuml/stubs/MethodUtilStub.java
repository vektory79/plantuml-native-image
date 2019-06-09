package me.vektory79.plantuml.stubs;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TargetClass(className = "sun.reflect.misc.MethodUtil")
public final class MethodUtilStub {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias)
    private static Method bounce;

    static {
        try {
            bounce = MethodUtilStubSupport.class.getDeclaredMethod("invoke", Method.class, Object.class, Object[].class);
        } catch (NoSuchMethodException e) {
            throw new InternalError("bouncer cannot be found", e);
        }
    }
}

final class MethodUtilStubSupport {
    private static Object invoke(Method m, Object obj, Object[] params)
            throws InvocationTargetException, IllegalAccessException {
        return m.invoke(obj, params);
    }
}
