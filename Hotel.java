import java.util.concurrent.Semaphore;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

///*
//* Hotel is a singleton class since it is only acting as a container for all three classes that we need to interact with each other
//* All the semaphores are initialized in this class, and then are used by the BellHops, Guests and Front-desk employees to sync operations
//* All semaphores defined have fairness enabled to ensure that each service is given in the order it was requested.
// All semaphores are initialized as fair to make sure that each guest is helped in the order it was requested
//* */
public class Hotel {

    private static Hotel hotelObj;
    public int JOINED_GUESTS = 0;

    public final int NUMBER_OF_GUESTS = 25, NUMBER_OF_STAFF_MEMBERS = 2;
    public Semaphore customerIsReady; // custReady
    public Semaphore customerLeavingDesk; // leaveEmployee
    public Semaphore customerRequiresBellHop; // custReqHelp
    public Semaphore  guestIsAssignedRoom; // assignedRoom
    public Semaphore bellHopDeliverBagsToRoom;
    public Semaphore bellHopAvailable;
    public Semaphore mutual_exclusion_guest_q, mutual_exclusion_room_assignment, mutual_exclusion_bag_q;
    public Queue<Guest> guest_q; // To track the guests who need help from front desk
    public Queue<Guest> bag_q; // To track the guests who need help from bellhops
    public Room[] allAvailableRooms;
    public Semaphore waitForFrontDeskEmployee; // named as employee in sample project
    public Semaphore bellHopRecievesBags;

   public List<Semaphore> finishedFromFrontDesk;
   public List<Semaphore> arrivedInRoom;

   public void aGuestHasArrived(Guest arrivedGuest) throws InterruptedException {
       mutual_exclusion_guest_q.acquire(); // Wait if any other guest is joining the queue
       guest_q.offer(arrivedGuest); // current guest joins q for front desk service
       mutual_exclusion_guest_q.release(); // release permit for guest_q 
   }

   public void guestRequestsBellHop(Guest requestorGuest) throws InterruptedException {
       this.bellHopAvailable.acquire(); // check to see if bellHop is available to assist
       System.out.println("Guest " + requestorGuest.guestID + " requests help with bags");
       this.bag_q.offer(requestorGuest); // add this particular guest in the q for those who require bellhop services
       this.customerRequiresBellHop.release();
   }

   public static Hotel getInstance() {
       if(hotelObj == null) hotelObj = new Hotel();
       return hotelObj;
   }


   Hotel() {
    customerIsReady = new Semaphore(0, true);
    guest_q = new LinkedList<>();
    mutual_exclusion_guest_q = new Semaphore(1, true);
    mutual_exclusion_room_assignment = new Semaphore(1, true);
    mutual_exclusion_bag_q = new Semaphore(1, true);

    waitForFrontDeskEmployee = new Semaphore(2, true);
    bag_q = new LinkedList<>();
    finishedFromFrontDesk = IntStream.range(0, 25).mapToObj(i -> new Semaphore(0, true)).collect(Collectors.toList());
    arrivedInRoom = IntStream.range(0, 25).mapToObj(i -> new Semaphore(0, true)).collect(Collectors.toList());
    allAvailableRooms = new Room[NUMBER_OF_GUESTS];
    guestIsAssignedRoom = new Semaphore(0, true);
    customerLeavingDesk = new Semaphore(0, true);

    customerRequiresBellHop = new Semaphore(0, true);
    bellHopAvailable = new Semaphore(NUMBER_OF_STAFF_MEMBERS, true);
    bellHopRecievesBags = new Semaphore(0, true);
    bellHopDeliverBagsToRoom = new Semaphore(0, true);

    for(int i = 0; i < NUMBER_OF_GUESTS; i++) allAvailableRooms[i] = new Room(i, false); // give each room unique number and set occupied to false
    }
    public void joinGuest() {
        JOINED_GUESTS++; // increment the number of joined guests
    }

}
