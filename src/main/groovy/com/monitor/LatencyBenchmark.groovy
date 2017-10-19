package com.monitor

import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.text.DecimalFormat
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future

class LatencyBenchmark {
	
	private String filename;
	CloudWatchMetricPublisher publisher;
	
	public LatencyBenchmark(String filename) {
		this.filename = filename;
	}
	
	public LatencyBenchmark(String filename, String dimension, String namespace) {
		this.filename = filename;
		this.publisher = new CloudWatchMetricPublisher("us-west-2", "resources/awsCredentials", dimension, namespace);
	}
	
	public long getWriteDurationAverage(long testTotalDurationInSeconds, boolean runContinously, int writesPerSecond, int numberOfIOThreads, long writeSize, int calculationPeriod, String mode) throws InterruptedException {
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
	  String filenameTimestamp = "-" + new Date().time.toString();
	  DecimalFormat df = new DecimalFormat();
	  df.setMaximumFractionDigits(2);
	  def appendDurationClosure = {num ->
		  writeDuration = getAppendDuration(filename + "-thread-" + num + filenameTimestamp, writeSize);
	  }
	  def threadPool = Executors.newFixedThreadPool(7);
	  System.out.println("type,DATE,n,WRITE_DURATION,LESS_THAN_50_MILLISECONDS,BETWEEN_50_AND_100_MILLISECONDS,BETWEEN_100_AND_500_MILLISECONDS,BETWEEN_500_AND_1000_MILLISECONDS,BETWEEN_1000_AND_5000_MILLISECONDS,BETWEEN_5000_AND_10000_MILLISECONDS,MORE_THAN_10000_MILLISECONDS,WRITE_DURATION_SUM,TOTAL_WRITE_DURATION_AVERAGE");
	  try {
		  while (n < testTotalDurationInSeconds || runContinously == true) {
			  n++;
			  if(mode == "LoadGen") {
					  List<Future> futures = (1..numberOfIOThreads).collect{num->
						threadPool.submit({->
						appendDurationClosure num } as Callable);
					  }
					  // recommended to use following statement to ensure the execution of all tasks.
					  List results = futures.collect{it.get()} as List;
				  	  writeDuration = results.sum() / numberOfIOThreads;
			  } else {
				  writeDuration = getWriteDuration(filename, writeSize);
			  }
			  
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
			  if (mode == "Monitoring") this.publisher.publishMetric(writeDuration);
			  if(n % calculationPeriod == 0) {
				  //Change filename timestamp
				  filenameTimestamp = "-" + new Date().time.toString();
				  
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
				  if (mode == "Monitoring") this.publisher.publishMetrics(metricsList);
				  
				  //Print metrics to CSV format
				  System.out.println("S," + new Date().toString() + "," + n + "," + writeDurationAverageInLastMinute + "," + df.format(LESS_THAN_50_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE) + "," + df.format(BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE) + "," + df.format(MORE_THAN_10000_MILLISECONDS_PERCENTAGE) + "," + writeDurationSum + "," + totalWriteDurationAverage);
				  
				  // Reseting variables so we get just the last minute statistics for them.
				  writeDurationSumInLastMinute = writeDurationAverageInLastMinute = 0;
				  LESS_THAN_50_MILLISECONDS = BETWEEN_50_AND_100_MILLISECONDS = BETWEEN_100_AND_500_MILLISECONDS = BETWEEN_500_AND_1000_MILLISECONDS = BETWEEN_1000_AND_5000_MILLISECONDS = BETWEEN_5000_AND_10000_MILLISECONDS = MORE_THAN_10000_MILLISECONDS = 0.0;
				  LESS_THAN_50_MILLISECONDS_PERCENTAGE = BETWEEN_50_AND_100_MILLISECONDS_PERCENTAGE = BETWEEN_100_AND_500_MILLISECONDS_PERCENTAGE = BETWEEN_500_AND_1000_MILLISECONDS_PERCENTAGE = BETWEEN_1000_AND_5000_MILLISECONDS_PERCENTAGE = BETWEEN_5000_AND_10000_MILLISECONDS_PERCENTAGE = MORE_THAN_10000_MILLISECONDS_PERCENTAGE = 0.0;
			  }
			  Thread.sleep((1000/writesPerSecond).toLong());
		  }
	  }finally {
		  threadPool.shutdown()
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
  
	private long getAppendDuration(String filename, long length) {
		long initialTime = new Date().getTime();
		String string = randomAlphaNumeric(length);
		try {
		  Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true), "utf-8"))
		  writer.append(string);
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

	  long initialTrialsTime = new Date().getTime();
	  
	  LatencyBenchmark ds;
	  long testTotalDurationInSeconds;
	  boolean runContinously;
	  int writesPerSecond;
	  long writeSize;
	  int numberOfIOThreads;
	  int calculationPeriod;
	  
	  String type = (args) ? args[0] : "";
	  
	  if (type == "LoadGen") {
		  if(args.length < 7) {
			  System.out.println("Usage: java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar <type> <file and path to write data> <Test Duration in seconds> <run continously? [true||false]> <writes per second> <number of IO threads> <writes size> <statistics calculation period>");
			  System.exit(1);
		  }
		  ds = new LatencyBenchmark(args[1]);
		  testTotalDurationInSeconds = Integer.parseInt(args[2]);
		  runContinously = Boolean.parseBoolean(args[3]);
		  writesPerSecond = Integer.parseInt(args[4]);
		  numberOfIOThreads = Integer.parseInt(args[5]);
		  writeSize = Long.parseLong(args[6]);
		  calculationPeriod = Integer.parseInt(args[7]);
	  } else if (type == "Monitoring") {
		  if(args.length < 9) {
			  System.out.println("Usage: java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar <type> <file and path to write data> <Test Duration in seconds> <run continously? [true||false]> <writes per second> <number of IO threads> <writes size> <statistics calculation period> <Cloudwatch Dimension> <Cloudwatch Namespace>");
			  System.exit(1);
		  }
		  ds = new LatencyBenchmark(args[1], args[8], args[9]);
		  testTotalDurationInSeconds = Integer.parseInt(args[2]);
		  runContinously = Boolean.parseBoolean(args[3]);
		  writesPerSecond = Integer.parseInt(args[4]);
		  numberOfIOThreads = Integer.parseInt(args[5]);
		  writeSize = Long.parseLong(args[6]);
		  calculationPeriod = Integer.parseInt(args[7]);
	  } else {
		  System.out.println("You must provide type of the tool: LoadGen or Monitoring.");
		  System.exit(1);
	  }
	  
	  System.out.println("Writting " + writesPerSecond + " per second...");
	  
	  try {
		  //System.out.println(testTotalDurationInSeconds+ "|" + runContinously+ "|" + writesPerSecond+ "|" + numberOfIOThreads+ "|" + writeSize+ "|" + calculationPeriod+ "|" + type);
		  ds.getWriteDurationAverage(testTotalDurationInSeconds, runContinously, writesPerSecond, numberOfIOThreads, writeSize, calculationPeriod, type);
	  } catch (InterruptedException e) {
		  e.printStackTrace();
	  }
	  
	  long finalTrialsTime = new Date().getTime();
	  long testsDuration = finalTrialsTime - initialTrialsTime;
	  System.out.println("Tests Duration: " + testsDuration + " ms.");
	}
  
}
