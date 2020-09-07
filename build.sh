#!/bin/bash
PROJECT_HOME=$(cd `dirname $0`; pwd)
cd $PROJECT_HOME

JAVACBIN=javac
LIBPATH="${PROJECT_HOME}/lib/jsp-api-2.1.jar:${PROJECT_HOME}/lib/servlet-api.jar"

rm -rf dist/*
mkdir -p dist/

cp -a template/* dist/



### build
rm -f $(find src/ -name "*.class")

for i in $(find src -type f -name "*.java");
do
    ${JAVACBIN} -g:none -nowarn -cp "${LIBPATH}" ${i}
done;

# ${JAVACBIN} -g:none -nowarn -cp "${LIBPATH}" $(find src/ -name "*.java")

cd src/

for i in $(find . -name "*.class");
do
classdir=`dirname $i`;
classname=`basename $i`;
dispath="${PROJECT_HOME}/dist/${classdir#./}.js"
code=`base64 $i`
sed -i "" "s~###${classname%%.*}###~${code}~g" ${dispath}
done
