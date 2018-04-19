import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Operator {
	
	public static Wheel wheel;
	public static int totalPlayers;
	public static CyclicBarrier gate;
	public static Queue<Player> queue = new LinkedList<>();
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));
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
		Thread t = new Thread(){
			public void run(){
				try {
					gate.await();
					while(true){
						synchronized(queue){
							if(queue.isEmpty()){}
							else{
								if(wheel.nOnboard < wheel.capacity){
									Player p = queue.poll();
									System.out.println("Player " + p.ID + " on board, capacity: " + (wheel.nOnboard + 1));
									wheel.load_players(p);
								}
								if(wheel.nOnboard == wheel.capacity){
									System.out.println("wheel end sleep");
									wheel.interrupt();
									wheel.run_ride();
									wheel.end_ride();
									Thread t = new Thread(){
										public void run(){
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
	
	public static class Wheel extends Thread{
		int capacity;
		int nOnboard;
		ArrayList<Player> onboard;
		int maxWaitingTime;
		public Wheel(int capacity, int maxWaitingTime){
			this.capacity = capacity;
			this.nOnboard = 0;
			this.onboard = new ArrayList<>();
			this.maxWaitingTime = maxWaitingTime;
		}
		@Override
		public void run(){
			try{
				gate.await();
				System.out.println("wheel start sleep");
				Wheel.sleep(maxWaitingTime);
			}catch(InterruptedException | BrokenBarrierException e){}
		}
		
		public synchronized void load_players(Player player){
			nOnboard++;
			totalPlayers--;
			onboard.add(player);
		}
		public synchronized void run_ride(){
			System.out.println("Wheel is ready, Let's go for a ride");
			System.out.println("Threads in this ride are:");
			for(Player p : onboard){
				System.out.print(p.ID+", ");
			}
			System.out.println();
		}
		public synchronized void end_ride(){
			nOnboard = 0;
			onboard = new ArrayList<>();
			if(totalPlayers==0){
				System.exit(0);
			}
		}
		public void sleep(){
			try{
				System.out.println("wheel start sleep");
				Wheel.sleep(maxWaitingTime);
			}catch(InterruptedException e){}
		}
	}
	public static class Player extends Thread{
		public int ID;
		int waitingTime;
		public Player(int ID, int waitingTime){
			this.ID = ID;
			this.waitingTime = waitingTime;
		}
		
		@Override
		public void run(){
			try{
				gate.await();
				Player.sleep(waitingTime);
				System.out.println("player wakes up: " + this.ID);
				System.out.println("passing player: " + this.ID + " to the operator");
				queue.add(this);
			}catch(InterruptedException | BrokenBarrierException e){}
		}
	}
	
}
