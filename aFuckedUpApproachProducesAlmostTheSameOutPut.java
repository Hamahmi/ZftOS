package os;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MiniProj3 {

	public static int max_wait_time;
	public static int[] wheel = new int[5];
	public static int index = -1;
	public static HashMap<Integer, Integer> players;
	public static HashMap<Integer, Boolean> flages;
	public static ArrayList<Integer> queue = new ArrayList<Integer>();
	public static Wheel theWheel;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));

		players = new HashMap<Integer, Integer>();
		flages = new HashMap<Integer, Boolean>();

		String line = br.readLine();

		max_wait_time = Integer.parseInt(line);
		int nummer = Integer.parseInt(br.readLine());

		theWheel = new Wheel(5, 0, new ArrayList<Integer>(), max_wait_time);
		theWheel.start();
		;

		line = br.readLine();
		while (line != null) {

			if (!(line.equals(""))) {

				// System.out.println(line);
				String[] l = line.split(",");
				int id = Integer.parseInt(l[0]);
				int wt = Integer.parseInt(l[1]);

				players.put(id, wt);
				flages.put(id, false);
				new Player(id, wt).start();
			}
			line = br.readLine();
		}
		br.close();

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
			run_ride();

		}

		public void wSleep() {
			// output "wheel start sleep";
			System.out.println("wheel start sleep");
			try {
				Wheel.sleep(this.mwt);
			} catch (InterruptedException e) {
				// System.out.println("full");
				run_ride();
			}

		}

		public void load_players() {
			// output "passing player: x to the operator"
			// ""Player x on board, capacity: x"

		}

		public void run_ride() {
			// output "Wheel is full, Let's go for a ride
			// Threads in this ride are:
			// a, b, c, d, e, ""
			System.out.println("Wheel is full, Let's go for a ride");
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
			// output "wheel end sleep"
			System.out.println();
			wSleep();
			System.out.println("wheel end sleep");
			index = -1;
			wheel = new int[5];
			for (int i : queue) {
				if (index < 4) {
					wheel[++index] = i;
					System.out.println("passing player: " + i + " to the operator");
					System.out.println("Player " + i + " on board, capacity: " + (index + 1));
					// System.out.println("index : " + index);
					//queue.remove(queue.indexOf(i));

					if (index == 4) {
						theWheel.interrupt();
					}
				} else {
					//queue.add(i);
				}
			}

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
				synchronized (wheel) {

					System.out.println("player wakes up: " + this.id);

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
