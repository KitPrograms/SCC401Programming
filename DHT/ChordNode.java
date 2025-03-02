import java.util.Vector;

class Finger{
	public int key;
	public ChordNode node;
	}

class Store{
	String key;
	byte[] value;
	}

public class ChordNode implements Runnable{
	
	static final int KEY_BITS = 8;
	
	//for each peer link that we have, we store a reference to the peer node plus a "cached" copy of that node's key; this means that whenever we change e.g. our successor reference we also set successorKey by doing successorKey = successor.getKey()
	ChordNode successor;
	int successorKey;
	
	ChordNode predecessor;
	int predecessorKey;
	
	//my finger table; note that all "node" entries will initially be "null"; your code should handle this
	int fingerTableLength;
	Finger finger[];
	int nextFingerFix;
	
	Vector<Store> dataStore = new Vector<Store>();
	
	//note: you should always use getKey() to get a node's key; this will make the transition to RMI easier
	private int myKey;
	
	ChordNode(String myKeyString)
		{
		myKey = hash(myKeyString);
		
		successor = this;
		successorKey = myKey;
		
		//initialise finger table (note all "node" links will be null!)
		finger = new Finger[KEY_BITS];
		for (int i = 0; i < KEY_BITS; i++)
			finger[i] = new Finger();
		fingerTableLength = KEY_BITS;
		
		//start up the periodic maintenance thread
		new Thread(this).start();
		}
	
	// -- API functions --
	
	void put(String key, byte[] value)
		{
		//find the node that should hold this key and add the key and value to that node's local store
		}
	
	byte[] get(String key)
		{
		//find the node that should hold this key, request the corresponding value from that node's local store, and return it
		
		return null;
		}
	
	// -- state utilities --
	
	int getKey()
		{
		return myKey;
		}
	
	ChordNode getPredecessor()
		{
		return predecessor;
		}
	
	// -- topology management functions --
	void join(ChordNode atNode)
		{
		predecessor = null;
		predecessorKey = 0;
		
		//TODO: fill in the remaining details of this function
		}
	
	// -- utility functions --
	ChordNode findSuccessor(int key)
		{
		return null;
		}
	
	ChordNode closestPrecedingNode(int key)
		{
		return this;
		}
	
	// -- range check functions; they deal with the added complexity of range wraps --
	// x is in [a,b] ?
	boolean isInClosedRange(int key, int a, int b)
		{
		if (b > a) return key >= a && key <= b;
		else return key >= a || key <= b;
		}
	
	// x is in (a,b) ?
	boolean isInOpenRange(int key, int a, int b)
		{
		if (b > a) return key > a && key < b;
		else return key > a || key < b;
		}
	
	// x is in [a,b) ?
	boolean isInHalfOpenRangeL(int key, int a, int b)
		{
		if (b > a) return key >= a && key < b;
		else return key >= a || key < b;
		}
	
	// x is in (a,b] ?
	boolean isInHalfOpenRangeR(int key, int a, int b)
		{
		if (b > a) return key > a && key <= b;
		else return key > a || key <= b;
		}
	
	// -- hash functions --
	//this function converts a string "s" to a key that can be used with the DHT's API functions
	int hash(String s)
		{
		int hash = 0;
		
		for (int i = 0; i < s.length(); i++)
			hash = hash * 31 + (int) s.charAt(i);
		
		if (hash < 0) hash = hash * -1;
		
		return hash % ((int) Math.pow(2, KEY_BITS));
		}
	
	// -- maintenance --
	void notify(ChordNode potentialPredecessor)
		{
		}
	
	void stabilise()
		{
		}
	
	void fixFingers()
		{
		}
	
	void checkPredecessor()
		{
		}
	
	void checkDataMoveDown()
		{
		//if I'm storing data that my current predecessor should be holding, move it
		}
	
	public void run()
		{
		while (true)
			{
			try{
				Thread.sleep(1000);
				}
				catch (InterruptedException e){
				System.out.println("Interrupted");
				}
			
			try{
				stabilise();
				}
				catch (Exception e){e.printStackTrace();}
			
			try{
				fixFingers();
				}
				catch (Exception e){e.printStackTrace();}
			
			try{
				checkPredecessor();
				}
				catch (Exception e){e.printStackTrace();}
			
			try{
				checkDataMoveDown();
				}
				catch (Exception e){e.printStackTrace();}
			}
		}
	
	public static void main(String args[])
		{
		// --- local test ---
		ChordNode n1 = new ChordNode("yorkshire");
		ChordNode n2 = new ChordNode("lancashire");
		ChordNode n3 = new ChordNode("cheshire");
		
		System.out.println("Joining nodes to network...");
		
		n1.join(n2);
		n3.join(n1);
		
		// -- wait a bit for stabilisation --
		
		System.out.println("Waiting for topology to stabilise...");
		
		try{
			Thread.sleep(7000);
			}
			catch (InterruptedException e){
			System.out.println("Interrupted");
			}
		
		System.out.println("Inserting keys...");
		
		String key1 = "alex";
		byte[] data1 = new byte[128];
		
		String key2 = "sam";
		byte[] data2 = new byte[64];
		
		String key3 = "jamie";
		byte[] data3 = new byte[256];
		
		n1.put(key1, data1);
		n1.put(key2, data2);
		n3.put(key3, data3);
		
		System.out.println("All done (press ctrl-c to quit)");
		}
	
	}