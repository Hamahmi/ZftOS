import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MiniProj3 {

	public static int[] wheel = new int[5];
	public static HashMap<Integer, Integer> players;
	public static Queue<Integer> queue = new LinkedList<>();
	public static Wheel theWheel;
	public static int nummer;
	public static CyclicBarrier gate;

	public static void main(String[] args) throws IOException, InterruptedException, BrokenBarrierException {

		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));
		players = new HashMap<Integer, Integer>();
		String line = br.readLine();
		int max_wait_time = Integer.parseInt(line);
		nummer = Integer.parseInt(br.readLine());
		theWheel = new Wheel(5, 0, new ArrayList<Integer>(), max_wait_time);
		
		gate = new CyclicBarrier((nummer + 2));
		
		theWheel.start();

		line = br.readLine();

		while (line != null) {

			if (!(line.equals(""))) {

				String[] l = line.split(",");

				int id = Integer.parseInt(l[0]);
				int wt = Integer.parseInt(l[1]);

				players.put(id, wt);
				
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
			try {
				gate.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
			System.out.println("wheel start sleep");
			wSleep();
		}

		public void wSleep() {
//			System.out.println("wheel start sleep");
			try {
				Wheel.sleep(this.mwt);
			} catch (InterruptedException e) {
			} finally {
				System.out.println("wheel end sleep");
				run_ride();
			}
		}
		
		public synchronized void load_players(){
			// output "passing player: x to the operator"
			// ""Player x on board, capacity: x"
			if(on<capacity){
				int player = queue.poll().intValue();
				System.out.println("Player " + player + " on board, capacity: " + (on + 1));
				wheel[on] = player;
				nummer--;
				on++;
			}
			if(on==capacity){
				theWheel.interrupt();
			}
		}

		public void run_ride() {
			
			System.out.println("Wheel is ready, Let's go for a ride");
			System.out.println("Threads in this ride are:");
			int i;
			for (i = 0; i < on; i++) {
				this.onboard.add(wheel[i]);
			}
			for (int t : wheel) {
				System.out.print(t + ", ");
			}
			System.out.println();
			end_ride();
		}

		public void end_ride() {
			if(nummer==0){
				System.exit(0);
			}
			on = 0;
			wheel = new int[5];
			onboard = new ArrayList<>();
			System.out.println("wheel start sleep");
			for(int i : queue){
				load_players();
			}
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
				gate.await();
				Player.sleep(wt);
				// output "player wakes up: x"
				System.out.println("player wakes up: " + this.id);
				System.out.println("passing player: " + this.id + " to the operator");
				synchronized (queue) {
					queue.add(this.id);
					theWheel.load_players();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}

	}

}
