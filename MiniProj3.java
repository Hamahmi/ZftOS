package os;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MiniProj3 {

	public static int max_wait_time;
	public static int[] wheel = new int[5];
	public static HashMap<Integer, Integer> players;
	public static HashMap<Integer, Boolean> flages;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));

		players = new HashMap<Integer, Integer>();
		flages = new HashMap<Integer, Boolean>();

		String line = br.readLine();

		max_wait_time = Integer.parseInt(line);
		int nummer = Integer.parseInt(br.readLine());

		line = br.readLine();
		while (line != null) {

			if (!(line.equals(""))) {

				System.out.println(line);
				String[] l = line.split(",");

				players.put(Integer.parseInt(l[0]), Integer.parseInt(l[1]));
				flages.put(Integer.parseInt(l[0]), false);
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

		}

		public void wSleep() {
			// output "wheel start sleep";
			try {
				Wheel.sleep(this.mwt);
			} catch (InterruptedException e) {
				e.printStackTrace();
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

		}

		public void end_ride() {
			// output "wheel end sleep"
			this.onboard = new ArrayList<Integer>();
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

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
