#!/bin/bash

set -e

APP_NAME="framework-url-handler"
SRC_DIR="src/main/java"
BUILD_DIR="build-jar"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes"

find "$SRC_DIR" -name "*.java" > sources.txt
javac -cp "$SERVLET_API_JAR" -d "$BUILD_DIR/classes" @sources.txt
rm sources.txt

jar -cvf "$BUILD_DIR/$APP_NAME.jar" -C "$BUILD_DIR/classes" .

echo ""
echo "JAR generated: $BUILD_DIR/$APP_NAME.jar"
echo ""