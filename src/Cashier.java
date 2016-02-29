class Cashier {

   // define constants for representing intervals
   static int BUSY = 1;
   static int FREE = 0;

   // Cashier id and current customer who is served by the cashier  
   private int cashierID;
   private Customer currentCustomer;

   // start time and end time of current interval
   private int startTime;
   private int endTime;

   // for keeping statistical data
   private int totalFreeTime;
   private int totalTransactionTime;
   private int totalCustomers;

   // Constructor
   Cashier(){
        cashierID = 0;
        totalCustomers = 0;
        totalTransactionTime = 0;
        totalFreeTime = 0;
   }


   // Constructor with cashier id
   Cashier(int cashierId){
        cashierID = cashierId;
        totalCustomers = 0;
        totalTransactionTime = 0;
        totalFreeTime = 0;
   }

   // get data member 
   int getCashierID (){
	   return cashierID;
   }

   Customer getCustomer(){
	   return currentCustomer;
   }

   int getEndTransactionTime(){
        // return end time of busy interval
        
        return endTime;
   }

   // functions for state transition
   // FREE -> BUSY :
   void freeToBusy (Customer newCustomer, int currentTime){
  	// Main goal  : switch from free interval to busy interval
  	//
  	// end free interval, start busy interval
  	// steps	: update totalFreeTime
  	// 		  set startTime, endTime, currentCustomer, 
  	// 		  update totalCustomers
	   
	   totalFreeTime += currentTime - startTime;
	   startTime = currentTime;
	   currentCustomer = newCustomer;
	   endTime = newCustomer.getTransactionTime()+currentTime;
	   totalCustomers++;
	   System.out.println("Cashier #"+cashierID+" is now busy");
	   
   }

   // BUSY -> FREE :
   Customer busyToFree (int currentTime){
   	// Main goal : switch from busy interval to free interval
   	// 
  	// steps     : update totalTransactionTime 
  	// 	       set startTime 
  	//             return currentCustomer

        totalTransactionTime += currentCustomer.getTransactionTime();
        startTime = currentTime;
        System.out.println("Cashier #"+cashierID+" is now free");
  	return currentCustomer;
   }

   // need this at the end of simulation 
   void setEndTransactionTime (int endsimulationtime, int intervalType){
  	// for end of simulation
  	// set endTime, 
  	// for FREE interval, update totalFreeTime
  	// for BUSY interval, update totalTransactionTime
	   if(intervalType == BUSY){
		   endTime = endsimulationtime;
		   totalTransactionTime += endTime-startTime;
	   }else{
		   endTime = endsimulationtime;
		   totalFreeTime += endTime -startTime;
	   }
        
   }

   // functions for printing statistics :
   void printStatistics (){
	   // print cashier statistics, see project statement

	   System.out.println("\t\tCashier ID             : "+cashierID);
	   System.out.println("\t\tTotal free time        : "+totalFreeTime);
	   System.out.println("\t\tTotal transaction time : "+totalTransactionTime);
	   System.out.println("\t\tTotal # of customers   : "+totalCustomers);
	   if (totalCustomers > 0)
		   System.out.format("\t\tAverage transaction time : %.2f%n\n",(totalTransactionTime*1.0)/totalCustomers);
   }

   public String toString()
   {
	return "Cashier:"+cashierID+":"+startTime+"-"+endTime+":Customer:"+currentCustomer;
   }

   public static void main(String[] args) {
	   // quick check
        Customer mycustomer = new Customer(20,30,40);
        Cashier mycashier = new Cashier(5);
        mycashier.freeToBusy (mycustomer, 13);
        System.out.println(mycashier);

   }

}

