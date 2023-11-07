import java.util.LinkedList;
import java.util.Random;

public class Guest implements Runnable {

    private Hotel hotel;
    public final static int MAX_BAGS = 5, MIN_BAGS = 0;
    public int guestID;
    public int numBags;
    public int helpedByEmployee;
    public int helpedByBellHop;
    Thread guestThread;
    Room guestRoom;


   public Guest(int guestID) {
        this.guestID = guestID;
        this.numBags = assignRandomNumberOfBags();
        this.guestThread = new Thread(this); // create a thread for current guest object
        System.out.println("Guest " + this.guestID + " created!");
        this.hotel = Hotel.getInstance();

    }

    public static int assignRandomNumberOfBags() {
       Random random = new Random();
       return random.nextInt(MAX_BAGS - MIN_BAGS + 1); // giving the range of random numbers
    }


    // This method will execute when we start the thread for a guest object
    @Override
    public void run() {
        System.out.println("Guest " + this.guestID + " enters the hotel with " + this.numBags + " bags");
        try {
            hotel.aGuestHasArrived(this);
            
            hotel.waitForFrontDeskEmployee.acquire(); // wait for front desk employee to free up
            hotel.customerIsReady.release(); // signal that customer is ready

            hotel.finishedFromFrontDesk.get(this.guestID).acquire(); // waiting for employee to finish front desk process for particular guest
            hotel.guestIsAssignedRoom.acquire(); // wait for employee to assign room

            System.out.println("Guest " + this.guestID + " receives room key for room " + this.guestRoom.roomNumber + " from Employee " + this.helpedByEmployee);

            hotel.customerLeavingDesk.release(); // signal that customer is leaving front desk

            // Go directly in the room
            if(numBags < 3) {
                hotel.arrivedInRoom.get(guestID).release();
                System.out.println("Guest " + guestID + " enters room " + guestRoom.roomNumber);
            } else {
                // Since the guest has more than or equal to 3 bags, they will have to visit the bellhop.
                hotel.guestRequestsBellHop(this);
                hotel.bellHopRecievesBags.acquire();
                System.out.println("Guest " + guestID + " enters room " + guestRoom.roomNumber);
                hotel.arrivedInRoom.get(guestID).release();


                hotel.bellHopDeliverBagsToRoom.acquire(); // wait for bellhop to deliver bags to room
                System.out.println("Guest " + guestID + " recieves bags from bellhop " + helpedByBellHop);
            }

            System.out.println("Guest " + guestID + " retires for the evening!");

                // join the threads after the guest is finished their entire process.
                System.out.println("Guest " + guestID + " joined!");
                hotel.joinGuest();
                guestThread.join();

        } catch (InterruptedException e) {
            System.out.println("Exception in guest thread with ID " + guestID + '\n' + e);
        }
    }

}
