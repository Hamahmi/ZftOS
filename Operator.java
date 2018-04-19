import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Operator {
	
	public static Wheel wheel;
	public static int totalPlayers;
	public static CyclicBarrier gate;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("input-1.txt"));
		int maxWaitingTime = Integer.parseInt(br.readLine());
		totalPlayers = Integer.parseInt(br.readLine());
		wheel = new Wheel(5, maxWaitingTime);
		gate = new CyclicBarrier((totalPlayers + 2));
	}
	
	public static class Wheel extends Thread{
		int capacity;
		int nOnboard;
		ArrayList<Player> onbaord;
		int maxWaitingTime;
		public Wheel(int capacity, int maxWaitingTime){
			this.capacity = capacity;
			this.nOnboard = 0;
			this.onbaord = new ArrayList<>();
			this.maxWaitingTime = maxWaitingTime;
		}
		@Override
		public void run(){
			try{
				System.out.println("wheel start sleep");
				Wheel.sleep(maxWaitingTime);
			}catch(InterruptedException e){}
			finally{
				System.out.println("wheel end sleep");
			}
		}
		
		public synchronized void load_players(Player player){
			nOnboard++;
			totalPlayers--;
			onbaord.add(player);
		}
		public synchronized void run_ride(){
			System.out.println("Wheel is ready, Let's go for a ride");
			System.out.println("Threads in this ride are:");
			for(Player p : onbaord){
				System.out.print(p.ID+", ");
			}
			System.out.println();
		}
		public synchronized void end_ride(){
			nOnboard = 0;
			onbaord = new ArrayList<>();
		}
	}
	public static class Player extends Thread{
		public int ID;
		int waitingTime;
		public Player(int ID, int waitingTime){
			this.ID = ID;
			this.waitingTime = waitingTime;
		}
	}
	
}
