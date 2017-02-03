#!/bin/bash
ORGANIZATION="play-secure"
MODULE="secure"
VERSION=`grep self conf/dependencies.yml | sed "s/.*$MODULE //"`

REPO=/var/www/repo/$ORGANIZATION
TARGET=$REPO/$MODULE-$VERSION.jar

rm -fr dist
rm -fr lib

play dependencies --sync || exit $?
play build-module || exit $?

if [[ "$VERSION" == *SNAPSHOT ]]
then
  echo "Skip publishing $TARGET (nobody needs snapshot)"
elif [ -e $TARGET ]; then
  echo "Not publishing ($MODULE-$VERSION already exists)"
elif [ -e $REPO ]; then

  echo ""
  echo ""
  echo ""
  echo " ********************************************************* "
  cp lib/$$MODULE.jar $TARGET || exit $?
  echo "Published $TARGET"
  echo " ********************************************************* "
  echo ""
  echo ""
  echo ""

else
  echo "Not publishing ($REPO does not exists)"
fi
