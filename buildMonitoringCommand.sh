# Disk Latency Monitor Execution Script
#

MOUNT_PATH_TO_MONITOR=$1
MOUNT_ID_DIMENSION=$2
CW_NAMESPACE=$3

echo "nohup java -jar build/libs/diskLatencyMonitor-Rodrigo-1.0-all.jar Monitoring $MOUNT_PATH_TO_MONITOR/$MOUNT_ID_DIMENSION-16kb 0 true 1 1 16384 240 $2 DFW/$CW_NAMESPACE >> /mnt/monitoring/$MOUNT_ID_DIMENSION.csv 2>&1&"
