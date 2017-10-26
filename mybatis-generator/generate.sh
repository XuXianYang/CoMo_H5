#!/bin/bash
SELF_PATH=`cd $(dirname $0);pwd`;
echo $SELF_PATH;
java -jar mybatis-generator-core-1.3.2.jar -configfile generator.xml -overwrite
