#!/bin/bash

set -e

APP_NAME="framework-url-handler"
SRC_FILE="src/main/java/URLHandlerLogic.java"
BUILD_DIR="build-jar"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

javac -d "$BUILD_DIR/classes" "$SRC_FILE"

jar -cvf "$BUILD_DIR/$APP_NAME.jar" -C "$BUILD_DIR/classes" .

echo ""
echo "JAR generated: $BUILD_DIR/$APP_NAME.jar"
echo ""