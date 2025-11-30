#!/bin/bash
# first build for frontend generation because it happens after building the native image :/
mvn clean install -DskipTests=true
# second build with frontend previously built this time - no mather if frontend is build again at the end
mvn clean install -Dquarkus.container-image.build=true -Dnative
