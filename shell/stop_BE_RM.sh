/usr/bin/ps -ef |grep PaaS-BE-RM|grep -v "grep"|awk '{print $2}'|xargs kill -9
