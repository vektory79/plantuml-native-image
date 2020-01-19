#!/bin/bash -e

###
# #%L
# plantuml-graal
# %%
# Copyright (C) 2019 - 2020 vektory79.me
# %%
# This code is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 only, as
# published by the Free Software Foundation.  Oracle designates this
# particular file as subject to the "Classpath" exception as provided
# by Oracle in the LICENSE file that accompanied this code.
#
# This code is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# version 2 for more details (a copy is included in the LICENSE file that
# accompanied this code).
#
# You should have received a copy of the GNU General Public License version
# 2 along with this work; if not, write to the Free Software Foundation,
# Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
# #L%

test_x() {
    FORMAT=${1}
    SCHEMA=${2}
    echo "Build ${SCHEMA} to ${FORMAT}"
    target/plantuml -Djava.awt.headless=true -t${FORMAT} ${SCHEMA}
}

test_all_formats() {
    SCHEMA=${1}
    test_x png ${SCHEMA}
    test_x svg ${SCHEMA}
    test_x eps ${SCHEMA}
#    test_x pdf ${SCHEMA}
    test_x vdx ${SCHEMA}
#    test_x xmi ${SCHEMA}
#    test_x scxml ${SCHEMA}
#    test_x html ${SCHEMA}
    test_x txt ${SCHEMA}
    test_x utxt ${SCHEMA}
    test_x latex ${SCHEMA}
    test_x latex:nopreamble ${SCHEMA}
}

test_all_formats ./examples/example1.txt
test_all_formats ./examples/example2.txt
test_all_formats ./examples/example3.txt
