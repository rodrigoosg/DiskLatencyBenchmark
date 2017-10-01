#!/bin/sh

crontab_entry_exists() {
    crontab -l 2>/dev/null | grep -x "/opt/metrics-collector/collectMetrics.sh" >/dev/null 2>/dev/null
}

if [ $# -gt 0 ]; then
	
	echo "Downloading metrics collector script from S3"
	java -jar /opt/s3-cmd/s3cmd.jar download s3://cmaas.infrastructure/metricsCollector/collectMetrics.sh /opt/metrics-collector/collectMetrics.sh
	java -jar /opt/s3-cmd/s3cmd.jar download s3://cmaas.infrastructure/metricsCollector/cmsMemoryMetricsCollector.groovy /opt/metrics-collector/cmsMemoryMetricsCollector.groovy
	chmod +x /opt/metrics-collector/collectMetrics.sh

    if ! crontab_entry_exists; then
		echo "Configure crontab"
		crontab -l > tempcron
		echo  "* * * * * /opt/metrics-collector/collectMetrics.sh $1" >> tempcron
		crontab tempcron
		rm tempcron
	else
		echo "Crontab entry was already installed. Skipping."
	fi
else
    echo "Usage: updateMetricsCollector.sh <owner>"
fi