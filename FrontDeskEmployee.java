import java.util.Random;

public class FrontDeskEmployee implements Runnable {

    private Hotel hotel;
    public int employeeID;
    public int currentRoomNumber = 0; // This variable keeps track of which room number is assigned
    // Assigning room numbers in sequence for simplicity and easier design
    public Thread employeeThread;

    public FrontDeskEmployee(int employeeID) {
        this.employeeID = employeeID;
        employeeThread = new Thread(this); // create thread for current employee object
        System.out.println("Front desk employee " + employeeID + " created!");
        this.hotel = Hotel.getInstance();
    }


    public void run() {
        try {
            while (true) {
                if (hotel.JOINED_GUESTS == hotel.NUMBER_OF_GUESTS)
                    break; // get out of the thread if all guests are processed
                hotel.customerIsReady.acquire();
                hotel.mutual_exclusion_room_assignment.acquire(); // ensure that no other customer is in the midst of room assignment
                Guest guest = hotel.guest_q.poll(); // get the guest in the top of the queue for front desk service
                guest.guestRoom = assignRoomToGuest(guest);
                hotel.mutual_exclusion_room_assignment.release(); // signal that room assignment process is done for the guest

                guest.helpedByEmployee = employeeID;
                System.out.println("Front desk employee " + employeeID + " registers guest " + guest.guestID + " and assigns room " + guest.guestRoom.roomNumber);
                hotel.guestIsAssignedRoom.release();

                hotel.finishedFromFrontDesk.get(guest.guestID).release();
                hotel.customerLeavingDesk.acquire(); // Wait for customer to leave desk
                hotel.waitForFrontDeskEmployee.release();

            }
        } catch (InterruptedException e) {
            System.out.println("Exception in thread for employee with ID " + '\n' + e);
        }

    }

    // This method is going to keep checking for rooms until it finds a room that is not occupied. The room is then assigned to the guest.
    public Room assignRoomToGuest(Guest selectedGuest) {
        while (true) {
            int index = new Random().nextInt(hotel.NUMBER_OF_GUESTS);
            Room randomRoomChoice = hotel.allAvailableRooms[index];
            if (!randomRoomChoice.isOccupied) {
                randomRoomChoice.isOccupied = true; // set the flag to occupied
                randomRoomChoice.currentOcupent = selectedGuest;
                return randomRoomChoice;
            }
        }
    }
}