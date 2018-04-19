package os;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MiniProj3 {

	public static int[] wheel = new int[5];
	public static int index = -1;
	public static HashMap<Integer, Integer> players;
	public static ArrayList<Integer> queue = new ArrayList<Integer>();
	public static Wheel theWheel;
	public static int nummer;

	public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {

		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));
		players = new HashMap<Integer, Integer>();
		String line = br.readLine();
		int max_wait_time = Integer.parseInt(line);
		nummer = Integer.parseInt(br.readLine());
		theWheel = new Wheel(5, 0, new ArrayList<Integer>(), max_wait_time);

		final CyclicBarrier gate = new CyclicBarrier((nummer + 2));
		
		gate.await();
		theWheel.start();

		line = br.readLine();

		while (line != null) {

			if (!(line.equals(""))) {

				String[] l = line.split(",");

				int id = Integer.parseInt(l[0]);
				int wt = Integer.parseInt(l[1]);

				players.put(id, wt);
				
				gate.await();
				new Player(id, wt).start();
			}
			line = br.readLine();
		}
		br.close();
		gate.await();
	}

	public static class Wheel extends Thread {

		int capacity;
		int on;
		ArrayList<Integer> onboard;
		int mwt;

		public Wheel(int capacity, int on, ArrayList<Integer> onboard, int mwt) {
			
			this.capacity = capacity;
			this.on = on;
			this.onboard = onboard;
			this.mwt = mwt;
		}

		@Override
		public void run() {
			
			wSleep();
		}

		public void wSleep() {
			
			System.out.println("wheel start sleep");
			try {
				Wheel.sleep(this.mwt);
			} catch (InterruptedException e) {
			} finally {
				System.out.println("wheel end sleep");
				run_ride();
			}
		}

		public synchronized void run_ride() {
			
			System.out.println("Wheel is ready, Let's go for a ride");
			System.out.println("Threads in this ride are:");
			int i;
			for (i = 0; i < index; i++) {
				this.onboard.add(wheel[i]);
			}
			this.on = i;
			for (int t : wheel) {
				System.out.print(t + ", ");
			}
			System.out.println();
			end_ride();
		}

		public void end_ride() {
			System.out.println();
			index = -1;
			wheel = new int[5];
			wSleep();
		}

	}

	public static class Player extends Thread {

		int id;
		int wt;
		boolean onboard;
		boolean rideComp;

		public Player(int ID, int WT) {
			this.id = ID;
			this.wt = WT;
			this.onboard = false;
			this.rideComp = false;
		}

		@Override
		public void run() {
			try {
				Player.sleep(wt);
				// output "player wakes up: x"
				System.out.println("player wakes up: " + this.id);
				synchronized (wheel) {

					if (index < 4) {
						wheel[++index] = this.id;
						System.out.println("passing player: " + this.id + " to the operator");
						System.out.println("Player " + this.id + " on board, capacity: " + (index + 1));
						// System.out.println("index : " + index);

						if (index == 4) {
							theWheel.interrupt();
						}
					} else {
						queue.add(this.id);
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
