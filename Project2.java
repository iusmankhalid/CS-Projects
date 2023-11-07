public class Project2 {
    public static void main(String[] args) throws InterruptedException {
        Hotel hotel = Hotel.getInstance(); // initializing all semaphores for concurrent tasks
        System.out.println("Simulation starts!");

       Guest[] allGuests = new Guest[hotel.NUMBER_OF_GUESTS];
       FrontDeskEmployee[] allFrontDeskEmployees = new FrontDeskEmployee[hotel.NUMBER_OF_STAFF_MEMBERS];
       BellHop[] allBellHops = new BellHop[hotel.NUMBER_OF_STAFF_MEMBERS];

        for(int i = 0; i < hotel.NUMBER_OF_STAFF_MEMBERS; i++) {
            allFrontDeskEmployees[i] = new FrontDeskEmployee(i);
        }

        for(int i = 0; i < hotel.NUMBER_OF_STAFF_MEMBERS; i++) {
            allBellHops[i] = new BellHop(i);
        }

       for(int i = 0; i < hotel.NUMBER_OF_GUESTS; i++) {
            allGuests[i] = new Guest(i); // create new guest with unique ID
       }


       for(int i = 0; i < hotel.NUMBER_OF_STAFF_MEMBERS; i++) {
           allFrontDeskEmployees[i].employeeThread.start();
       }

       for(int i = 0; i < hotel.NUMBER_OF_STAFF_MEMBERS; i++) {
           allBellHops[i].bellHopThread.start();
       }

       for(int i = 0; i < hotel.NUMBER_OF_GUESTS; i++) {
           allGuests[i].guestThread.start();
       }


       // This loop is to make sure that the program doesn't terminate until all guests have completed their entire process.
       while(hotel.JOINED_GUESTS < hotel.NUMBER_OF_GUESTS) {System.currentTimeMillis();} // This is just to stall the program (no functionality)

        System.out.println("Simulation ends!");
        System.exit(0);
    }
}
