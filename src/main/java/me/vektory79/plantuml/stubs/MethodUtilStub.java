package me.vektory79.plantuml.stubs;

/*-
 * #%L
 * plantuml-graal
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2019 - 2020 vektory79.me
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
