#!/usr/bin/env bash

COMPRESSED_FILENAME=latency-data-$(date +%Y-%m).zip

zip /home/centos/$COMPRESSED_FILENAME /efs-186/*upload.csv
aws s3 cp /home/centos/$COMPRESSED_FILENAME s3://ericsson-poc-bucket/Latency_Data/$COMPRESSED_FILENAME
