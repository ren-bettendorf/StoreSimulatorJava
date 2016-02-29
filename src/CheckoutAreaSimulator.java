import java.util.*;
import java.io.*;
import javax.swing.*;

class CheckoutAreaSimulator {

  // input parameters
	
  private int numCashiers, customerQLimit;
  private int simulationTime, dataSource;
  private int chancesOfArrival, maxTransactionTime;

  // statistical data
  private int numGoAway, numServed, totalWaitingTime;
  
  // internal data
  private int customerIDCounter;
  private CheckoutArea checkoutarea; // checkout area object
  private Scanner dataFile;	     // get customer data from file
  private int data1;
  private int data2;
  private Random dataRandom;	     // get customer data using random function

  // most recent customer arrival info, see getCustomerData()
  private boolean anyNewArrival;  
  private int transactionTime;
  
  //File Name Input
  private String fileName;
  Scanner input;
  
  private CheckoutAreaSimulator()
  {
	  customerIDCounter = 1;
	  numGoAway = 0;
	  numServed = 0;
	  totalWaitingTime = 0;
  }

  private void setupParameters() throws FileNotFoundException{
	  // add statements, read input parameters
	  input = new Scanner(System.in);
	  customerIDCounter = 1;
	  System.out.println("Enter Simulation Time: ");
	  simulationTime = input.nextInt();
	  System.out.println("Enter the number of cashiers: ");
	  numCashiers = input.nextInt();
	  System.out.println("Enter maximum transaction time: ");
	  maxTransactionTime = input.nextInt();
	  System.out.println("Enter chances (0< X <= 100) of new customer: ");
	  chancesOfArrival = input.nextInt();
	  System.out.println("Enter Customer Queue Limit: ");
	  customerQLimit = input.nextInt();
	  System.out.println("Enter 1/0 to get data from File/Random: ");
	  dataSource = input.nextInt();
	  if(dataSource == 1){
		  JFileChooser fileChooser = new JFileChooser();
		  if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			  File file = fileChooser.getSelectedFile();
			  dataFile = new Scanner(file);
		  }
	  }
  }

  private void getCustomerData()
  {
	// get next customer data : from file or random number generator
	// set anyNewArrival and transactionTime
	if(dataSource == 1){
		
		//Reads first positive integer of line
		data1 = Integer.parseInt(dataFile.next());
		anyNewArrival = (((data1%100)+1)<=chancesOfArrival);
		
		//Reads second positive integer of line
		data2 = Integer.parseInt(dataFile.next());
		transactionTime = (data2%maxTransactionTime)+1;
		
		//Move to next line for next iteration
		dataFile.nextLine();
	}else{
		dataRandom = new Random();
		anyNewArrival = ((dataRandom.nextInt(100)+1)<=chancesOfArrival);
	  	transactionTime = dataRandom.nextInt(maxTransactionTime) + 1;
	}
  }

  private void doSimulation()
  {
	  	checkoutarea = new CheckoutArea(numCashiers, customerQLimit,customerIDCounter);
	  	// Time driver simulation loop
  		for (int currentTime = 0; currentTime < simulationTime; currentTime++) {
  			System.out.println("Time: "+currentTime);
  			getCustomerData();
    		// Step 1: any new customer enters the checkout area?
  			if (anyNewArrival) {
      		    // Step 1.1: setup customer data
    			Customer customer = new Customer(customerIDCounter, transactionTime, currentTime);
    			System.out.println("Customer #"+customerIDCounter+" arrives at time "+currentTime);
    			// Step 1.2: check customer waiting queue too long?
    			if(checkoutarea.isCustomerQTooLong()){
    				checkoutarea.insertCustomerQ(customer);
    				System.out.println("Customer #"+customerIDCounter+" waits in the customer queue");
    			}else{
    				numGoAway++;
    				System.out.println("Customer #"+customerIDCounter+" arrives but queue is to long so they leave");
    			}
    			customerIDCounter++;	
    		}
  			int numBusy = checkoutarea.numBusyCashiers();
    		// Step 2: free busy cashiers, add to free cashierQ
  			for(int i = 0;i<numBusy;i++){
  				Cashier cashier = checkoutarea.getFrontBusyCashierQ();
  				if(cashier.getEndTransactionTime()==currentTime){
  					numServed++;
  					checkoutarea.removeBusyCashierQ();
  					checkoutarea.insertFreeCashierQ(cashier);
  					Customer customer = cashier.busyToFree(currentTime);
  					System.out.println("Customer #"+customer.getCustomerID()+" is done");
  				}else break;
  			}
    		// Step 3: get free cashiers to serve waiting customers
  			int maxFor;
    			
  			if(checkoutarea.numFreeCashiers()<checkoutarea.numWaitingCustomers()){
  				maxFor = checkoutarea.numFreeCashiers();
  			}else maxFor = checkoutarea.numWaitingCustomers();
    			
  			for(int i = 0;i<maxFor;i++){
  				if((checkoutarea.emptyCustomerQ())||(checkoutarea.emptyFreeCashierQ())) break;
  				Cashier cashier = (Cashier)checkoutarea.removeFreeCashierQ();
  				Customer customer = checkoutarea.removeCustomerQ();
  				cashier.freeToBusy(customer, currentTime);
  				checkoutarea.insertBusyCashierQ(cashier);
  				System.out.println("Customer #"+customer.getCustomerID()+" gets a cashier");
  				System.out.println("Cashier #"+cashier.getCashierID()+" starts serving customer " +
  									"#"+customer.getCustomerID()+" for "+(cashier.getEndTransactionTime()-currentTime)+" units");
  			}	
  			totalWaitingTime += checkoutarea.numWaitingCustomers();
  	} // end simulation loop
  		List<Cashier> hold = new ArrayList<Cashier>(numCashiers);
  		// clean-up
  		int numberBusy = checkoutarea.numBusyCashiers();
  		for(int i = 0;i<numberBusy;i++){
  			Cashier cashier = checkoutarea.removeBusyCashierQ();
  			cashier.setEndTransactionTime(simulationTime-1, 1);
  			hold.add(cashier);
  		}
  		for(int i = 0;i<numberBusy;i++){
  			Cashier cashier = hold.remove(0);
  			checkoutarea.insertBusyCashierQ(cashier);
  		}
  		hold.clear();
  		int numberFree = checkoutarea.numFreeCashiers();
  		for(int i = 0;i<numberFree;i++){
  			Cashier cashier = checkoutarea.removeFreeCashierQ();
  			cashier.setEndTransactionTime(simulationTime-1,0);
  			hold.add(cashier);
  		}
  		for(int i = 0;i<numberFree;i++){
  			Cashier cashier = hold.remove(0);
  			checkoutarea.insertFreeCashierQ(cashier);
  		}
  }

  private void printStatistics()
  {
	// print out simulation results
	  System.out.print("End of Simulation Report:\n" +
	  					"# total arrival customers:\t"+(numServed+numGoAway)+"\n" +
						"# customers leave:\t\t"+numGoAway+"\n" +
						"# customers served:\t\t"+numServed);
	  System.out.println("\n");
	  System.out.print("***Current Cashiers Info***\n" +
	  					"# Waiting customers:\t"+checkoutarea.numWaitingCustomers()+"\n" +
	  					"# busy Cashiers:\t"+checkoutarea.numBusyCashiers()+"\n" +
	  					"# free cashiers:\t"+checkoutarea.numFreeCashiers());
	  System.out.println("\n");
	  System.out.print("Total Waiting time: \t"+totalWaitingTime+"\n" +
	  					"Average Waiting time:\t"+(totalWaitingTime/numServed));
	  System.out.println("\n");
	  System.out.print("Busy Cashier Info");
	  System.out.println("\n");
	  int numBusy = checkoutarea.numBusyCashiers();
	  for(int i = 0; i<numBusy;i++){
		  (checkoutarea.removeBusyCashierQ()).printStatistics();
	  }
	  System.out.println("\n");
	  System.out.print("Free Cashier Info");
	  System.out.println("\n");
	  int numFree = checkoutarea.numFreeCashiers();
	  for(int i = 0;i<numFree;i++){
		  (checkoutarea.removeFreeCashierQ()).printStatistics();
	  }
	  
	  					
  }

  // start to run simulation
  public static void main(String[] args) throws FileNotFoundException {
   	CheckoutAreaSimulator checkout_area_simulator=new CheckoutAreaSimulator();
   	checkout_area_simulator.setupParameters();
   	checkout_area_simulator.doSimulation();
   	checkout_area_simulator.printStatistics();
  }

}