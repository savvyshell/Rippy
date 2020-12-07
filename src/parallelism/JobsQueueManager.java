package parallelism;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import tools.Display;

public class JobsQueueManager implements Runnable {

	public Display display;
	
	public static int ThreadCount = 3;
	public static int count = 0;
	public int id = 0;
	public static Queue<Map<String, String>> jobsQueue = new LinkedList<Map<String, String>>();

	public JobsQueueManager(RProcess p) {
		this.id = JobsQueueManager.count;
		JobsQueueManager.count += 1;
		this.display = p.display;
	}
	
	public void run() {	
		display.header(display.getThread(id) + " starting...");
		while (true) {
			
			while (JobsQueueManager.jobsQueue.peek() != null) {
				
				Map<String, String> row = JobsQueueManager.jobsQueue.poll();
				String href = row.get("href");
				String text = row.get("text");
				
				
				
			}
			
			try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
			
		}
	}
	
}
