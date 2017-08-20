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
  
  public long getWriteDurationAverage(int samples) throws InterruptedException {
	long writeDuration = 0;
	long writeDurationSum = 0;
	long writeDurationAverage = 0;
	long n = 0;
	while (n < samples) {
		n++;
		writeDuration = getWriteDuration(filename, 16384);
		writeDurationSum += writeDuration;
		Thread.sleep(1000);
		System.out.println("Write Duration: " + writeDuration + " ms.");
	}
	writeDurationAverage = writeDurationSum / n;
	System.out.println("Write Duration Sum: " + writeDurationSum + " ms.");
	System.out.println("Write Duration Average: " + writeDurationAverage + " ms.");
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
	long initialTrialsTime = new Date().getTime();
	DiskStats ds = new DiskStats("tests/DiskStats.txt");
	int trials = 500;
	try {
		ds.getWriteDurationAverage(trials);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	long finalTrialsTime = new Date().getTime();
	long testsDuration = finalTrialsTime - initialTrialsTime;
	System.out.println("Tests Duration: " + testsDuration + " ms.");
  }

}