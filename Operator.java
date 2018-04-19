import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Operator {

	public static Wheel wheel;
	public static int totalPlayers;
	public static CyclicBarrier gate;
	public static Queue<Player> queue = new LinkedList<>();
	public static List<String> lines = new ArrayList<String>();

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("input-2.txt"));
		int maxWaitingTime = Integer.parseInt(br.readLine());

		totalPlayers = Integer.parseInt(br.readLine());
		wheel = new Wheel(5, maxWaitingTime);
		gate = new CyclicBarrier((totalPlayers + 3));
		wheel.start();
		String line = "";
		while ((line = br.readLine()) != null) {
			if (!(line.equals(""))) {
				String[] l = line.split(",");
				int id = Integer.parseInt(l[0]);
				int wt = Integer.parseInt(l[1]);
				Player p = new Player(id, wt);
				p.start();
			}
		}
		br.close();
		Thread t = new Thread() {
			public void run() {
				try {
					gate.await();
					while (true) {
						synchronized (queue) {
							if (queue.isEmpty()) {
							} else {
								if (wheel.nOnboard < wheel.capacity) {
									Player p = queue.poll();
									lines.add("Player " + p.ID + " on board, capacity: " + (wheel.nOnboard + 1));
									wheel.load_players(p);
								}
								if (wheel.nOnboard == wheel.capacity) {
									wheel.interrupt();
									wheel.run_ride();
									Thread t = new Thread() {
										public void run() {
											wheel.sleep();
										}
									};
									t.start();
								}
							}
						}
					}
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}

	public static class Wheel extends Thread {
		int capacity;
		int nOnboard;
		ArrayList<Player> onboard;
		int maxWaitingTime;

		public Wheel(int capacity, int maxWaitingTime) {
			this.capacity = capacity;
			this.nOnboard = 0;
			this.onboard = new ArrayList<>();
			this.maxWaitingTime = maxWaitingTime;
		}

		@Override
		public void run() {
			try {
				gate.await();
				lines.add("wheel start sleep");
				Wheel.sleep(maxWaitingTime);
				run_ride();
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		}

		public synchronized void load_players(Player player) {
			nOnboard++;
			totalPlayers--;
			onboard.add(player);
		}

		public synchronized void run_ride() {
			if (onboard.size() == 0) {
				lines.add("wheel end sleep");
				Thread t = new Thread() {
					public void run() {
						wheel.sleep();
					}
				};
				t.start();
				return;
			}
			lines.add("wheel end sleep");
			lines.add("Wheel is ready, Let's go for a ride");
			lines.add("Threads in this ride are:");
			String l = "";
			for (Player p : onboard) {
				l += p.ID + ", ";
			}
			lines.add(l);
			lines.add("");
			end_ride();
		}

		public void end_ride() {
			nOnboard = 0;
			onboard = new ArrayList<>();
			if (totalPlayers == 0) {
				Path file = Paths.get("the-file-name.txt");
				try {
					Files.write(file, lines, Charset.forName("UTF-8"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}

		public void sleep() {
			try {
				lines.add("wheel start sleep");
				Wheel.sleep(maxWaitingTime);
				run_ride();
			} catch (InterruptedException e) {
			}
		}
	}

	public static class Player extends Thread {
		public int ID;
		int waitingTime;

		public Player(int ID, int waitingTime) {
			this.ID = ID;
			this.waitingTime = waitingTime;
		}

		@Override
		public void run() {
			try {
				gate.await();
				Player.sleep(waitingTime);
				synchronized (queue) {
					lines.add("player wakes up: " + this.ID);
					lines.add("passing player: " + this.ID + " to the operator");
					queue.add(this);
				}
			} catch (InterruptedException | BrokenBarrierException e) {
			}
		}
	}

}
