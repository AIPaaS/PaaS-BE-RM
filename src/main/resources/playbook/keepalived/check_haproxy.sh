#!/bin/bash
A=`ps -C haproxy --no-header |wc -l`
if [ $A -eq 0 ];then
/usr/sbin/haproxy -f /etc/haproxy/haproxy.cfg
sleep 3
if [ `ps -C haproxy --no-header |wc -l` -eq 0 ];then
systemctl stop keepalived
fi
fi