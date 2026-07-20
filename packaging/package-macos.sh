#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
cd "$project_dir"

./mvnw clean package

package_input="target/jpackage-input"
installer_dir="target/installer"
mkdir -p "$package_input" "$installer_dir"

cp target/libris.jar "$package_input/"
cp target/dependency/*.jar "$package_input/"

jpackage \
  --type dmg \
  --name Libris \
  --app-version 1.0.0 \
  --vendor "CSC 4350 Group 7" \
  --description "Library management system for librarians and members" \
  --input "$package_input" \
  --main-jar libris.jar \
  --main-class Launcher \
  --dest "$installer_dir" \
  --mac-package-identifier edu.gsu.csc4350.libris \
  --mac-package-name Libris \
  --java-options -Dfile.encoding=UTF-8

echo "Created $(find "$installer_dir" -maxdepth 1 -name '*.dmg' -print -quit)"
