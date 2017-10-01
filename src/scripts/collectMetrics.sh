#!/bin/sh

if [ $# -gt 0 ]; then
    JAVA_HOME=/usr/java/jdk1.7.0

	GROOVY_HOME=/root/.gvm/groovy/2.4.3

	export JAVA_HOME

	export GROOVY_HOME

	PATH=$PATH:$JAVA_HOME/bin:$GROOVY_HOME/bin:

	export PATH

	groovy /opt/metrics-collector/cmsMemoryMetricsCollector.groovy $1
	sleep 10;
	groovy /opt/metrics-collector/cmsMemoryMetricsCollector.groovy $1
else
    echo "Usage: collectMetrics.sh <owner>"
fi

#clean up logs older than 2 weeks
find /opt/metrics-collector/logs -type f -mtime +14 -exec rm -f {} \;