#!/bin/bash

NAMESPACE=$1

EFSNUM=$2

EFS_ID=$3

./buildMonitoringCommand.sh /mnt/mfvp/efs$EFSNUM az3-efs$EFSNUM-fs-$EFS_ID $NAMESPACE

exit
