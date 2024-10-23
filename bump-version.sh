# /bin/sh

version=$1

echo "Updating project version to $version"

mvn versions:set -DnewVersion="$version" -DprocessAllModules -DgenerateBackupPoms=false
