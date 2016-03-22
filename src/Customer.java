class Customer
{
    private int customerID;
    private int transactionTime;
    private int arrivalTime;

    Customer()
    {
        customerID = 0;
        transactionTime = 0;
        arrivalTime = 0;
    }

    Customer(int customerid, int transactiontime, int arrivaltime)
    {
        customerID = customerid;
        transactionTime = transactiontime;
        arrivalTime = arrivaltime;
    }

    int getTransactionTime()
    {    
    	return transactionTime;
    }

    int getArrivalTime()
    {
    	return arrivalTime;
    }

    int getCustomerID()
    {
    	return customerID; 
    }

    public String toString()
    {
    	return ""+customerID+":"+transactionTime+":"+arrivalTime;
    }

    public static void main(String[] args) 
    {
    	// quick check!
    	Customer mycustomer = new Customer(20, 30, 40);
    	System.out.println("Customer Info:" + mycustomer);

    }
}