import java.util.*;

//--------------------------------------------------------------------------
//
// Define transaction queues in a checkout area. Queues hold references to 
// Customer & Cashier objects
//
// Customer queue is used to hold waiting customers. If the queue is too long
// (i.e. >  customerQLimnit), customer goes away without entering customer queue
//
// There are several cashiers in a checkout area. Use PriorityQueue to 
// hold BUSY cashiers and FIFO queue to hold FREE cashiers, 
// i.e. a cashier that is FREE for the longest time should start be used first.
//
// To handle cashier in PriorityQueue, we need to define comparator 
// for comparing 2 cashier objects. Here is a constructor from Java API:
//
// 	PriorityQueue(int initialCapacity, Comparator<? super E> comparator) 
//
// For priority queue, the default compare function is "natural ordering"
// i.e. for numbers, minimum value is returned first
//
// User can define own comparator class for PriorityQueue.
// For cashier objects, we like to have smallest end transaction time first.
//
// The following class define compare() for two cashiers :

class CompareCashier implements Comparator<Cashier>{
	// overide compare() method
 	public int compare(Cashier o1, Cashier o2) {
		return o1.getEndTransactionTime() - o2.getEndTransactionTime(); 
	}
}

class CheckoutArea {
	private PriorityQueue <Cashier> busyCashierQ;

	private Queue<Customer> customerQ;
	private Queue<Cashier> freeCashierQ;
	
	private int customerQLimit;
	
	
	public CheckoutArea()
	{
		customerQ = new ArrayDeque<Customer>();
		freeCashierQ = new ArrayDeque<Cashier>();
	}
	
	public CheckoutArea(int numCashiers, int customerQlimit, int startCashierID){
	
		customerQ = new ArrayDeque<Customer>(customerQlimit);
		freeCashierQ = new ArrayDeque<Cashier>(numCashiers);
		
		busyCashierQ = new PriorityQueue<Cashier>(numCashiers, new CompareCashier()); 
		
		customerQLimit = customerQlimit;
		
		for(int i = 0; i < numCashiers; i++)
		{
			Cashier cashier = new Cashier(startCashierID+i);
			freeCashierQ.add(cashier);
		}
	}
	
	public Cashier removeFreeCashierQ()
	{
		return (Cashier)freeCashierQ.remove();
	}
	
	public Cashier removeBusyCashierQ() 
	{
		return (Cashier)busyCashierQ.remove();
	}
	
	public Customer removeCustomerQ()
	{
		return (Customer)customerQ.remove();
	}
	
	public void insertFreeCashierQ(Cashier cashier)
	{
		freeCashierQ.add(cashier);
	}
	
	public void insertBusyCashierQ(Cashier cashier)
	{
		busyCashierQ.add(cashier);
	}
	
	public void insertCustomerQ(Customer customer)
	{
		customerQ.add(customer);
	}
	
	public boolean emptyFreeCashierQ()
	{
		return (freeCashierQ.peek() == null);
	}
	
	public boolean emptyBusyCashierQ()
	{
		return (busyCashierQ.peek()==null);
	}
	
	public boolean emptyCustomerQ()
	{
		return (customerQ.peek()==null);
	}
	
	 public int numFreeCashiers()
	{
		 return freeCashierQ.size();
	}
	
	public int numBusyCashiers()
	{
		return busyCashierQ.size();
	}
	
	public int numWaitingCustomers()
	{
		return customerQ.size();
	}
	
	public Cashier getFrontBusyCashierQ() 
	{
		return (Cashier)busyCashierQ.peek();
	}
	
	public boolean isCustomerQTooLong()
	{
		return customerQ.size() < customerQLimit;
	}
	
	public void printStatistics()
	{
	  	System.out.println("\t# waiting customers  : " + numWaitingCustomers());
	  	System.out.println("\t# busy cashiers      : " + numBusyCashiers());
	  	System.out.println("\t# free cashiers      : " + numFreeCashiers());
	}
	
	public static void main(String[] args) 
	{
		// quick tests
		CheckoutArea sc = new CheckoutArea(4, 5, 1001);
		Customer c1 = new Customer(1, 18, 10);
		Customer c2 = new Customer(2, 33, 10);
		Customer c3 = new Customer(3, 21, 10);
		Customer c4 = new Customer(4, 37, 10);
		sc.insertCustomerQ(c1);
		sc.insertCustomerQ(c2);
		sc.insertCustomerQ(c3);
		System.out.println("" + sc.customerQ);
		System.out.println("Remove customer: " + sc.removeCustomerQ());
		System.out.println("Remove customer: " + sc.removeCustomerQ());
		System.out.println("Remove customer: " + sc.removeCustomerQ());
	
		System.out.println("" + sc.freeCashierQ);
		Cashier p1 = sc.removeFreeCashierQ();
		Cashier p2 = sc.removeFreeCashierQ();
		Cashier p3 = sc.removeFreeCashierQ();
		Cashier p4 = sc.removeFreeCashierQ();
		
		System.out.println("Remove free cashier: " + p1);
		System.out.println("Remove free cashier: " + p2);
		System.out.println("Remove free cashier: " + p3);
		System.out.println("Remove free cashier: " + p4);
	
		p1.freeToBusy (c1, 13);
		p2.freeToBusy (c2, 13);
		p3.freeToBusy (c3, 13);
		p4.freeToBusy (c4, 13);
		
		sc.insertBusyCashierQ(p1);
		sc.insertBusyCashierQ(p2);
		sc.insertBusyCashierQ(p3);
		sc.insertBusyCashierQ(p4);
		
		System.out.println("" + sc.busyCashierQ);
		p1 = sc.removeBusyCashierQ();
		p2 = sc.removeBusyCashierQ();
		p3 = sc.removeBusyCashierQ();
		p4 = sc.removeBusyCashierQ();
		
		System.out.println("Remove busy cashier: " + p1);
		System.out.println("Remove busy cashier: " + p2);
		System.out.println("Remove busy cashier: " + p3);
		System.out.println("Remove busy cashier: " + p4);
	}
}