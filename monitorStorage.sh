#Usage: java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar <type> <file and path to write data> <Test Duration in seconds> <run continously? [true||false]> <writes per second> <number of IO threads> <writes size> <statistics calculation period> <Cloudwatch Dimension> <Cloudwatch Namespace>
nohup java -jar build/libs/diskLatencyMonitor-Rodrigo-1.0-all.jar Monitoring $1 $2 $3 $4 $5 $6 $7 $8 $9 >> /mnt/monitoring/$8.csv 2>&1&
