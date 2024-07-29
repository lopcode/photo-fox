#!/bin/bash
set -eou pipefail

CONTAINER_TAG="${CONTAINER_TAG:-test}"

./gradlew clean check shadowJar
./compile-assets.main.kts

JAR=$(find app/build/libs/*-all.jar | head -n1)

docker build \
  --build-arg jar="$JAR" \
  --build-arg static_assets="build/static" \
  --build-arg template_assets="build/templates" \
  --build-arg version="$CONTAINER_TAG" \
  -t "ghcr.io/lopcode/photo-fox:$CONTAINER_TAG" \
  .