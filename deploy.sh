#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
  mvn clean deploy --settings scripts/settings.xml -DskipTests
fi