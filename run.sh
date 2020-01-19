#!/bin/bash -e

# plantuml-graal
#
# Copyright (C) 2019 - 2020 vektory79.me
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.

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
