package me.vektory79.plantuml.stubs;

/*-
 * #%L
 * plantuml-graal
 * %%
 * Copyright (C) 2019 - 2020 vektory79.me
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

public final class XsltStub {
}

@TargetClass(className = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl$TransletClassLoader")
final class TransletClassLoader {
    @Substitute
    Class<?> defineClass(final byte[] b) {
        throw new UnsupportedOperationException();
    }
}
