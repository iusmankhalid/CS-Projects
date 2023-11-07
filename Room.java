public class Room {
    public int roomNumber;
    public boolean isOccupied;
    Guest currentOcupent;

    public Room(int roomNumber, boolean isOccupied) {
        this.roomNumber = roomNumber;
        this.isOccupied = isOccupied;
        this.currentOcupent = null;
    }
}