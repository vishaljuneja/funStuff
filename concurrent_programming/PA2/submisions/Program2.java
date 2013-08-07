import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.*;

public class Program2{
	
	//this is just a main, it creates main thread and start it	
        public static void main(String[] args) {
        	final Program2 P=new Program2();
        	P.start();
        }
        
        //how many philosophers and forks
        private static int MAX=5;
        //how many times each philosopher should eat
        private static int MEALS=5;
        
        //global variables - for our threads and resources
        public Vector<Chopstick> chopsticks=new Vector<Chopstick>();
        public Vector<Thread> philosophers=new Vector<Thread>();
        
        public void start(){
        	//create chopsticks
        	for (int i=1;i<=MAX;++i){
        		chopsticks.add(new Chopstick());
        	}
        	
        	//create philosophers, and assign chopstick numbers to them
        	//philosoph_1 sits beetwen 1(first) and 2(second) chopstick
        	//...
        	//philosoph_N sits beetwen N(last) and 1(first) chopstick
        	int leftChopstick,rightChopstick;
        	for (int i=1;i<=MAX;++i){
        		leftChopstick=i;
        		rightChopstick=(i<MAX) ? (i+1) : (1);        		
        		philosophers.add(new Thread( new Philosopher(MEALS,leftChopstick,rightChopstick)));
        		philosophers.get(i-1).setName("Philosopher_"+(i));
        	}
        	
        	
        	//let's start dinner
        	try{
        		System.out.println("Dinner is starting...");
        		for (int i=1;i<=MAX;++i){
        			philosophers.get(i-1).start();
        		}
        		for (int i=1;i<=MAX;++i){
        			philosophers.get(i-1).join();
        		}
        		System.out.println("Dinner is over...");
        	} 
        	catch (InterruptedException e){System.out.println(Thread.currentThread().getName()+" InterruptedException");}
        	
        }
        
        //this class implements chopstick object (and inside monitor object pattern)
        //thread(philosoph) can try to pick up object
        //1) if it is not used -> thread changes inUse to true,
        //2) if it is used -> thread waits to be signaled
        //2a) on being signaled and inUse=false -> thread changes inUse is changed to true,
        //2b) on being signaled and inUse=true -> thread goes back to waits to be signaled,
        public class Chopstick{
        	private AtomicBoolean inUse=new AtomicBoolean(false);
        	private final Lock lock = new ReentrantLock();
        	private final Condition condition=lock.newCondition();
        	
        	public void pickUp(String name){
        		lock.lock();
        		try{
        			//chopstick is not being used, lets pick it up
        			if ((inUse.get())==false){
        				System.out.println(Thread.currentThread().getName()+" picks up "+name+" chopstick");
        				inUse.set(true);
        			}
        			//chopstick is being used, lets wait for it
        			else{
						System.out.println(Thread.currentThread().getName()+" "+name+" chopstick is being used... waiting...");
        				//let's wait for availability of chopstick
        				while(inUse.get()==true) {
        					condition.await();
        				}
        				System.out.println(Thread.currentThread().getName()+" picks up "+name+" chopstick");
        				inUse.set(true);
        			}
        		} 
        		catch (InterruptedException e){System.out.println(Thread.currentThread().getName()+" InterruptedException");}
        		finally{
        			lock.unlock();
        		}
        	}
        	
        	public void putDown(String name){
        		lock.lock();
        		try{
        			//put down chopstick and notify other philosopher - he might be hungry
       				System.out.println(Thread.currentThread().getName()+" puts down "+name+" chopstick");
       				inUse.set(false);
       				condition.signal();
        		} 
        		finally{
        			lock.unlock();
        		}
        	}
        }
        
        //Philosophers behaviour avoids deadlock issue
        //it is based on simplest solution available here
        //http://en.wikipedia.org/wiki/Dining_philosophers_problem#Resource_hierarchy_solution
        //forks(or chopsticks - does not matter) and philosophers are numbered from 1-N
        //philosoph is first trying to pick up fork with lower available number
        // -- so p1 is trying to pick up f1(because f1<fN), 
        // -- p2 is trying to pickup f1(because f1<f2), 
        // -- p3 is trying to pickup f2(because f2<f3),
        // ... 
        // -- pN is trying to pickup fN-1 (as N-1<N)
        // 
        //so if all of them are going to pick up forks at once, all forks except N fork will be picked up, and this N fork will be still available - so pN is able to eat
        public class Philosopher implements Runnable {
        	private int piecesToEat;
        	private int alreadyEatenPieces=0;
        	
        	//identification of chopsticks
        	private int lowerNumber;
        	private int higherNumber;
        	//names of chopstick (either left or right)
        	String lowerName;
        	String higherName;
        	
        	private Random rnd = new Random();
        	
        	public Philosopher(int piecesToEat,int leftNumber,int rightNumber){
        		//this code is to assign properly forks names(left/right) and numbers
        		this.piecesToEat=piecesToEat;
        		if (leftNumber<rightNumber){
        			this.lowerNumber=leftNumber;
        			this.higherNumber=rightNumber;
        			this.lowerName="left";
        			this.higherName="right";
        		}
        		else{
        			this.lowerNumber=rightNumber;
        			this.higherNumber=leftNumber;
        			this.lowerName="right";
        			this.higherName="left";
        		}
        		//System.out.println("left:"+leftNumber+" right:"+rightNumber+"  lower:"+this.lowerNumber+this.lowerName+"  higher:"+this.higherNumber+this.higherName);
        	}
        	
        	//behaviour is next (in loop)
        	//1) think or eat
        	//if (eat) , then grab one chopstick, grab second one, eat and put them down, and having 2 chopsticks -> eat and put down chopstick -> go to Loop
        	public void run(){
        		boolean eat;
        		for(;;){
        			eat=rnd.nextBoolean();
        			if (eat){
        				chopsticks.get(lowerNumber-1).pickUp(lowerName);
        				chopsticks.get(higherNumber-1).pickUp(higherName);
        				++alreadyEatenPieces;
						//you can uncomment this to have some delays
						//try{Thread.sleep(25);} catch (InterruptedException e){System.out.println(Thread.currentThread().getName()+" InterruptedException");}
        				System.out.println(Thread.currentThread().getName()+" eats (" + alreadyEatenPieces + " time(s))");
        				chopsticks.get(lowerNumber-1).putDown(lowerName);
        				chopsticks.get(higherNumber-1).putDown(higherName);
        				if (piecesToEat==alreadyEatenPieces) {
        					System.out.println(Thread.currentThread().getName()+" is going home...");
        					break;
        				}
        			}else{
						//just think - do nothing
						
						//or make some computations, like counting to 1000
						//int k=0;for (int i=0;i<1000;++i) k=k+i;
        				//System.out.println(Thread.currentThread().getName()+" thinking...");
						
						//you can uncomment this to have some delays
						//try{Thread.sleep(25);} catch (InterruptedException e){System.out.println(Thread.currentThread().getName()+" InterruptedException");}
        			}
        		}
        	}
        }
        
}
