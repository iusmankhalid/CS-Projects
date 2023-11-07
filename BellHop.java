
public class BellHop implements Runnable {
    public int bellHopID;
    private Hotel hotel;
    public Thread bellHopThread;

    public BellHop(int bellHopID) {
        this.bellHopID = bellHopID;
        this.bellHopThread = new Thread(this);
        System.out.println("Bellhop " + this.bellHopID + " created!");
        this.hotel = Hotel.getInstance();

    }

    public void run() {
       try {
           while(true) {
               if(hotel.JOINED_GUESTS == hotel.NUMBER_OF_STAFF_MEMBERS) break; // stop thread if all guests have
               hotel.customerRequiresBellHop.acquire();

               hotel.mutual_exclusion_bag_q.acquire();
               Guest guest = hotel.bag_q.poll();
               guest.helpedByBellHop = bellHopID;
               hotel.mutual_exclusion_bag_q.release();

               System.out.println("Bellhop " + bellHopID + " receives bags from guest " + guest.guestID);
               hotel.bellHopRecievesBags.release(); // signal that customer has given bags to bellhop

               hotel.arrivedInRoom.get(guest.guestID).acquire();
               System.out.println("Bellhop " + bellHopID +  " delivers bags to guest " + guest.guestID);
               hotel.bellHopDeliverBagsToRoom.release(); // signal that bellhop is now delivering bags to customer's room

               hotel.bellHopAvailable.release(); // signal that bellhop is done with the customer and mark them as free

           }
       } catch (InterruptedException e) {
           System.out.println("Exception in Bellhop with ID " + bellHopID  + '\n' + e);
       }
    }

}