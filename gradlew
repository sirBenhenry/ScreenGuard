#!/bin/sh
# Gradle wrapper script for Unix

APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit

APP_NAME="Gradle"
APP_BASE_NAME="${0##*/}"

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

JAVA_EXE=java

if ! command -v "$JAVA_EXE" > /dev/null 2>&1; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

JAVA_HOME=$( readlink -f "$( command -v "$JAVA_EXE" )" | sed 's!/bin/java!!' )

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVA_EXE" \
    "-classpath" "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
