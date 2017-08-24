package com.monitor;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

/*----------------------------------------------------------------
 *  Author:        Rodrigo Guimaraes
 *  Written:       8/12/2017
 *  Last updated:  -
 *
 *  Compilation:   javac DiskStats.java
 *  Execution:     -
 *  
 *  % java DiskStats
 *  
 *----------------------------------------------------------------*/

public class DiskStats {

  private String filename;
  
  public DiskStats(String filename) {
	  this.filename = filename;
  }
  
  public long getWriteDurationAverage(long testTotalDurationInSeconds) throws InterruptedException {
	long writeDuration = 0;
	long writeDurationSum = 0;
	long writeDurationSumInLastMinute = 0;
	long writeDurationAverage = 0;
	long writeDurationAverageInLastMinute = 0;
	long n = 0;
	while (n < testTotalDurationInSeconds) {
		n++;
		writeDuration = getWriteDuration(filename, 16384);
		writeDurationSum += writeDuration;
		writeDurationSumInLastMinute += writeDuration;
		Thread.sleep(1000);
		System.out.println("n:" + n + "," + writeDuration);
		if(n % 60 == 0) {
			writeDurationAverageInLastMinute = writeDurationSumInLastMinute / 60;
			//System.out.println("Write Duration Sum in the last 60 seconds: " + writeDurationSumInLastMinute + " ms.");
			System.out.println(new Date().toString() + "," + n + "," + writeDurationAverageInLastMinute);
			writeDurationSumInLastMinute = 0;
			writeDurationAverageInLastMinute = 0;
			writeDurationAverage = writeDurationSum / n;
			System.out.println("TOTAL Write Duration Sum: " + writeDurationSum + " ms.");
			System.out.println("TOTAL Write Duration Average:" + writeDurationAverage + " ms.");			
		}
	}
	return writeDurationAverage;
  }
  
  private long getWriteDuration(String filename, int length) {
	  char[] data = new char[length];
	  long initialTime = new Date().getTime();
	  String string = new String(data);
	  try (Writer writer = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(filename), "utf-8"))) {
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

  public static void main(String[] args) {
	if(args.length < 2) {
		System.out.println("Usage: java -jar diskLatencyMonitor-Rodrigo-1.0-all.jar <file and path to write data> <Test Duration in seconds>");
		System.exit(1);
	}
	long initialTrialsTime = new Date().getTime();
	DiskStats ds = new DiskStats(args[0]);
	long testTotalDurationInSeconds = Integer.parseInt(args[1]);
	try {
		ds.getWriteDurationAverage(testTotalDurationInSeconds);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	long finalTrialsTime = new Date().getTime();
	long testsDuration = finalTrialsTime - initialTrialsTime;
	System.out.println("Tests Duration: " + testsDuration + " ms.");
  }

}