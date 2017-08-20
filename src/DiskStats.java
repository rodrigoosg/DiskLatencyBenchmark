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
  private long samples;
  long writeDuration;
  long writeDurationAverage;  
  static long readDuration;
  
  public DiskStats(String filename, long samples) {
	  this.filename = filename;
	  this.samples = samples;
  }
  
  public long run() throws InterruptedException {
	writeDuration = 0;
	readDuration = 0;
	long n = 0;
	while (n < samples) {
		n++;
		writeDuration += write(filename, 16384);
		//readDuration = read(file);
		Thread.sleep(10);
		//System.out.println("n:" + n);
	}
	writeDuration = writeDuration / n;
	return writeDuration;
  }
  
  private long write(String filename, int length) {
	  char[] data = new char[length];
	  for (int i = 0; i < length; i++) {
		  data[i] = 'a';
	  }
	  long initialTime = new Date().getTime();
	  String string = new String(data);
	  //System.out.println(string);
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
	  //System.out.println("writeDuration:" + duration);
	  return duration;
  }

  public static void main(String[] args) {
	long initialTrialsTime = new Date().getTime();
	DiskStats ds = new DiskStats("tests/DiskStats.txt", 100);
	int trials = 1;
	try {
		int n = 0;
		while (n < trials) {
			n++;
			ds.run();
			ds.writeDurationAverage += ds.writeDuration;
			System.out.println("Write Duration: " + ds.writeDuration + " ms.");
			//Thread.sleep(1000);
		}
		ds.writeDurationAverage = ds.writeDurationAverage / n;	
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	long finalTrialsTime = new Date().getTime();
	long testsDuration = finalTrialsTime - initialTrialsTime;
	System.out.println("Write Duration Average: " + ds.writeDurationAverage + " ms.");	
	System.out.println("Tests Duration: " + testsDuration + " ms.");
  }

}