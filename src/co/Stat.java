package co;

import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements the necessary statistical logic.
 *
 * FEATURES:
 *
 * - Records timings (recorded by the processing thread(s))
 * - Able to write the data out to a CSV file, for further analysis
 */ 
public class Stat {

	protected static class Timings {
		private long arrivalNs, startNs, finishNs;
		
		public Timings(long arrivalNs, long startNs, long finishNs){
			this.arrivalNs = arrivalNs;
			this.startNs = startNs;
			this.finishNs = finishNs;
		}
		
		public int compareTo(Timings timings) {
			if (arrivalNs > timings.arrivalNs) return 1;
			if (arrivalNs == timings.arrivalNs) return 0;
			return -1;
		}		
	}
	
	private List<Timings> timingsList;
	
	protected Stat(){ timingsList = new ArrayList<Timings>(); }
	
	public void record(long arrivalNs, long startNs, long finishNs) {
		timingsList.add(new Timings(arrivalNs, startNs, finishNs));
	}
	
	public void toCSV(String path) throws IOException {
		File csvFile = new File(path);
		
		try (
			FileWriter fileWriter = new FileWriter(csvFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		) {
			bufferedWriter.write("arrival;start;finish;idle;processing;total");
			bufferedWriter.newLine();
			for (Timings timings : timingsList) {
				long arrival, start, finish, idle, processing, total;

				arrival = timings.arrivalNs - Sys.TIME_ZERO;
				start = timings.startNs - Sys.TIME_ZERO;
				finish = timings.finishNs - Sys.TIME_ZERO;
				idle = start - arrival;
				processing = finish - start;
				total = idle + processing;
				
				String timingsString = String.format("%d;%d;%d;%d;%d;%d", arrival, start, finish, idle, processing, total);
				bufferedWriter.write(timingsString);
				bufferedWriter.newLine();
			}
		}
	}
}