import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    static class Ball {
        public final AtomicInteger bounces;
        
        public Ball(int bounces) {
        	this.bounces = new AtomicInteger(bounces); 
        }
        
        public int bounce(){
        	return bounces.decrementAndGet();
        }
        
    }

    static class Player extends Thread {

        private final String sound;   
        private final BlockingQueue<Ball> queue;

        public Player(String sound, BlockingQueue<Ball> queue) {
            this.sound = sound;
            this.queue = queue;
        }
        
        public void ready(){
        	start();
        }

        @Override
        public void run() {
            Ball ball;
            boolean running = true;
            while (running) {
                try {
					if(sound.equals("Ping!")) Thread.sleep(1000L);
                    ball = queue.take(); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
					System.out.println("Where the ball mate? -- " + sound);
                    return;
                }
				
				
				
				
                System.out.println(sound);

                switch (ball.bounce()) {
                    case 1: //one player will get here
                        running = false;
                        break;
                    case 0: //another player(last) will get here, at that point the game is done
                    	System.out.println("Done!");
                        return; //since game is done don't put the ball back on the Q
                }

                try {
                    queue.put(ball);    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    
    static class Referee extends SynchronousQueue<Ball>{
		private static final long serialVersionUID = 1L;

		public void startGame(Ball ball) throws InterruptedException{
    		System.out.println("Ready...Set...Go!");
    		put(ball);
    	}
    }
    
    public static void main(String[] args) throws InterruptedException {
        Referee referee = new Referee();             
        new Player("Ping!", referee).ready();
        new Player("Pong!", referee).ready();
		new Player("AHA!", referee).ready();
        referee.startGame(new Ball(7));               
    }

}

