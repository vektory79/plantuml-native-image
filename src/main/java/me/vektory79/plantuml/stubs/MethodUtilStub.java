package me.vektory79.plantuml.stubs;

/*-
 * #%L
 * plantuml-graal
 * %%
 * Copyright (C) 2019 - 2020 vektory79.me
 * %%
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
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
