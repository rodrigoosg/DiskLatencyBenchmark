nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /mnt/share/$1 $2 $3 $4 $5 $6 > /efs-186/efs-5-21-stats-$1-143.csv 2>&1&
nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /mnt/mfvp/efs3/$1 $2 $3 $4 $5 $6 > /efs-186/efs-5-41-stats-$1-143.csv 2>&1&
nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /mnt/mfvp/efs4/$1 $2 $3 $4 $5 $6 > /efs-186/efs-5-224-stats-$1-143.csv 2>&1&
nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /mnt/mfvp/efs2/$1 $2 $3 $4 $5 $6 > /efs-186/efs-242-stats-$1-143.csv 2>&1&
nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /mnt/mfvp/efs1/$1 $2 $3 $4 $5 $6 > /efs-186/efs-27-stats-$1-143.csv 2>&1&
nohup java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar /ofs/latencyTests/$1 $2 $3 $4 $5 $6 > /efs-186/ofs-$1-143.csv 2>&1&
