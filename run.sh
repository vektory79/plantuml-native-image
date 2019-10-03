#!/bin/bash -e

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
