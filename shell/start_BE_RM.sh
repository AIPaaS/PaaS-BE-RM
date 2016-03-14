#!/bin/sh
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/config

LIB_DIR=$DEPLOY_DIR/libs
LIB_JARS=$DEPLOY_DIR/libs/*

nohup ${JAVA_HOME}/bin/java -classpath $CONF_DIR:$LIB_JARS:$DEPLOY_DIR/classes/main com.ai.paas.ipaas.DubboServiceS
tart $1 &