package com.monitor

import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.text.DecimalFormat
import java.util.Date

class LatencyBenchmark {
	
	private String filename;
	CloudWatchMetricPublisher publisher;
	
	public LatencyBenchmark(String filename, String dimension, String namespace) {
		this.filename = filename;
		this.publisher = new CloudWatchMetricPublisher("us-west-2", "resources/awsCredentials", dimension, namespace);
	}
	
	public long getWriteDurationAverage(long testTotalDurationInSeconds, boolean runContinously, int writesPerSecond, long writeSize, int calculationPeriod) throws InterruptedException {
	  long writeDuration = 0;
	  long writeDurationSum = 0;
	  long writeDurationSumInLastMinute = 0;
	  long totalWriteDurationAverage = 0;
	  long writeDurationAverageInLastMinute = 0;
	  long n = 0;
	  double LESS_THAN_50_MILLISECONDS = 0;
	  double BETWEEN_50_AND_100_MILLISECONDS = 0;
	  double BETWEEN_100_AND_500_MILLISECONDS = 0;
	  double BETWEEN_500_AND_1000_MILLISECONDS = 0;
	  double BETWEEN_1000_AND_5000_MILLISECONDS = 0;
	  double BETWEEN_5000_AND_10000_MILLISECONDS = 0;
	  double MORE_THAN_10000_MILLISECONDS = 0;
	  double LESS_THAN_50_MILLISECONDS_PERCENTAGE = 0;
	  double BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE = 0;
	  double BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE = 0;
	  double BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE = 0;
	  double BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE = 0;
	  double BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE = 0;
	  double MORE_THAN_10000_MILLISECONDS_PERCENTAGE = 0;
	  DecimalFormat df = new DecimalFormat();
	  df.setMaximumFractionDigits(2);
	  System.out.println("type,DATE,n,WRITE_DURATION,LESS_THAN_50_MILLISECONDS,BETWEEN_50_AND_100_MILLISECONDS,BETWEEN_100_AND_500_MILLISECONDS,BETWEEN_500_AND_1000_MILLISECONDS,BETWEEN_1000_AND_5000_MILLISECONDS,BETWEEN_5000_AND_10000_MILLISECONDS,MORE_THAN_10000_MILLISECONDS,WRITE_DURATION_SUM,TOTAL_WRITE_DURATION_AVERAGE");
	  while (n < testTotalDurationInSeconds || runContinously == true) {
		  n++;
		  writeDuration = getWriteDuration(filename, writeSize);
		  writeDurationSum += writeDuration;
		  writeDurationSumInLastMinute += writeDuration;
		  if(writeDuration <= 50) {
			  LESS_THAN_50_MILLISECONDS++;
		  } else if (50 < writeDuration && writeDuration <= 100) {
			  BETWEEN_50_AND_100_MILLISECONDS++;
		  } else if (100 < writeDuration && writeDuration <= 500) {
			  BETWEEN_100_AND_500_MILLISECONDS++;
		  } else if (500 < writeDuration && writeDuration <= 1000) {
			  BETWEEN_500_AND_1000_MILLISECONDS++;
		  } else if (1000 < writeDuration && writeDuration <= 5000) {
			  BETWEEN_1000_AND_5000_MILLISECONDS++;
		  } else if (5000 < writeDuration && writeDuration <= 10000) {
			  BETWEEN_5000_AND_10000_MILLISECONDS++;
		  } else if (writeDuration > 10000) {
			  MORE_THAN_10000_MILLISECONDS++;
		  }
		  System.out.println("I," + new Date().toString() + "," + n + "," + writeDuration + "," + df.format(LESS_THAN_50_MILLISECONDS) + "," + df.format(BETWEEN_50_AND_100_MILLISECONDS) + "," + df.format(BETWEEN_100_AND_500_MILLISECONDS) + "," + df.format(BETWEEN_500_AND_1000_MILLISECONDS) + "," + df.format(BETWEEN_1000_AND_5000_MILLISECONDS) + "," + df.format(BETWEEN_5000_AND_10000_MILLISECONDS) + "," + df.format(MORE_THAN_10000_MILLISECONDS));
		  this.publisher.publishMetric(writeDuration);
		  if(n % calculationPeriod == 0) {
			  
			  //Calculate metrics inside the period
			  writeDurationAverageInLastMinute = writeDurationSumInLastMinute / calculationPeriod;
			  LESS_THAN_50_MILLISECONDS_PERCENTAGE = (LESS_THAN_50_MILLISECONDS / calculationPeriod)*100;
			  BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE = (BETWEEN_50_AND_100_MILLISECONDS / calculationPeriod)*100;
			  BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE = (BETWEEN_100_AND_500_MILLISECONDS / calculationPeriod)*100;
			  BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE = (BETWEEN_500_AND_1000_MILLISECONDS / calculationPeriod)*100;
			  BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE = (BETWEEN_1000_AND_5000_MILLISECONDS / calculationPeriod)*100;
			  BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE = (BETWEEN_5000_AND_10000_MILLISECONDS / calculationPeriod)*100;
			  MORE_THAN_10000_MILLISECONDS_PERCENTAGE = (MORE_THAN_10000_MILLISECONDS / calculationPeriod)*100;
			  totalWriteDurationAverage = writeDurationSum / n;
			  
			  //Publish metrics to cloudwatch
			  List metricsList = [LESS_THAN_50_MILLISECONDS_PERCENTAGE,BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE,BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE,BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE,BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE,BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE,MORE_THAN_10000_MILLISECONDS_PERCENTAGE];
			  this.publisher.publishMetrics(metricsList);
			  
			  //Print metrics to CSV format
			  System.out.println("S," + new Date().toString() + "," + n + "," + writeDurationAverageInLastMinute + "," + df.format(LESS_THAN_50_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE) + "," + df.format(MORE_THAN_10000_MILLISECONDS_PERCENTAGE) + "," + writeDurationSum + "," + totalWriteDurationAverage);
			  
			  // Reseting variables so we get just the last minute statistics for them.
			  writeDurationSumInLastMinute = writeDurationAverageInLastMinute = 0;
			  LESS_THAN_50_MILLISECONDS = BETWEEN_50_AND_100_MILLISECONDS = BETWEEN_100_AND_500_MILLISECONDS = BETWEEN_500_AND_1000_MILLISECONDS = BETWEEN_1000_AND_5000_MILLISECONDS = BETWEEN_5000_AND_10000_MILLISECONDS = MORE_THAN_10000_MILLISECONDS = 0.0;
			  LESS_THAN_50_MILLISECONDS_PERCENTAGE = BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE = BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE = BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE = BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE = BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE = MORE_THAN_10000_MILLISECONDS_PERCENTAGE = 0.0;
		  }
		  Thread.sleep((1000/writesPerSecond).toLong());
	  }
	  return totalWriteDurationAverage;
	}
	
	private long getWriteDuration(String filename, long length) {
		long initialTime = new Date().getTime();
		String string = randomAlphaNumeric(length);
		try {
		  Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"))
		  writer.write(string);
		  writer.flush();
		  writer.close();
		} catch (IOException e) {
		  e.printStackTrace();
		}
		long finalTime = new Date().getTime();
		long duration = (finalTime - initialTime);
		return duration;
	}
  
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	  
	public static String randomAlphaNumeric(long count) {
	  StringBuilder builder = new StringBuilder();
	  while (count-- != 0) {
		int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
		builder.append(ALPHA_NUMERIC_STRING.charAt(character));
	  }
	  return builder.toString();
	}
	
	public static void main(String[] args) {
	  if(args.length < 7) {
		  System.out.println("Usage: java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar <file and path to write data> <Test Duration in seconds> <run continously? [true||false]> <writes per second> <writes size> <statistics calculation period> <Cloudwatch Dimension> <Cloudwatch Namespace>");
		  System.exit(1);
	  }
	  long initialTrialsTime = new Date().getTime();
	  LatencyBenchmark ds = new LatencyBenchmark(args[0], args[6], args[7]);
	  long testTotalDurationInSeconds = Integer.parseInt(args[1]);
	  boolean runContinously = Boolean.parseBoolean(args[2]);
	  int writesPerSecond = Integer.parseInt(args[3]);
	  long writeSize = Long.parseLong(args[4]);
	  int calculationPeriod = Integer.parseInt(args[5]);
	  System.out.println("Writting " + writesPerSecond + " per second...");
	  
	  try {
		  ds.getWriteDurationAverage(testTotalDurationInSeconds, runContinously, writesPerSecond, writeSize, calculationPeriod);
	  } catch (InterruptedException e) {
		  e.printStackTrace();
	  }
	  
	  long finalTrialsTime = new Date().getTime();
	  long testsDuration = finalTrialsTime - initialTrialsTime;
	  System.out.println("Tests Duration: " + testsDuration + " ms.");
	}
  
}
