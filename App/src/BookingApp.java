import java.util.HashMap;
import java.util.Map;

// Abstract Room Class
abstract class Room {
    private String roomType;
    private int numberOfBeds;
    private double size;
    private double price;

    public Room(String roomType, int numberOfBeds, double size, double price) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayRoomDetails();
}

// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 200.0, 1000.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType());
        System.out.println("Beds: " + getNumberOfBeds());
        System.out.println("Size: " + getSize());
        System.out.println("Price: ₹" + getPrice());
    }
}

// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 350.0, 1800.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType());
        System.out.println("Beds: " + getNumberOfBeds());
        System.out.println("Size: " + getSize());
        System.out.println("Price: ₹" + getPrice());
    }
}

// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 600.0, 4000.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType());
        System.out.println("Beds: " + getNumberOfBeds());
        System.out.println("Size: " + getSize());
        System.out.println("Price: ₹" + getPrice());
    }
}

// Centralized Inventory Class (NEW - v3.0 concept)
class RoomInventory {
    private HashMap<String, Integer> inventory;

    // Constructor initializes inventory
    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Add or initialize room type
    public void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability (increment/decrement)
    public void updateAvailability(String roomType, int change) {
        int current = getAvailability(roomType);
        inventory.put(roomType, current + change);
    }

    // Display full inventory
    public void displayInventory() {
        System.out.println("\n===== CURRENT ROOM INVENTORY =====");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Available: " + entry.getValue());
        }
    }
}

// Main Class (v3.1 Refactored)
public class BookingApp {

    public static void main(String[] args) {

        // Room objects (domain model)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Register room availability
        inventory.addRoom(singleRoom.getRoomType(), 5);
        inventory.addRoom(doubleRoom.getRoomType(), 3);
        inventory.addRoom(suiteRoom.getRoomType(), 2);

        // Display room details
        System.out.println("===== ROOM DETAILS =====\n");

        singleRoom.displayRoomDetails();
        System.out.println("-----------------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("-----------------------------------");

        suiteRoom.displayRoomDetails();
        System.out.println("-----------------------------------");

        // Display inventory
        inventory.displayInventory();

        // Example update
        System.out.println("\nUpdating inventory (1 Single Room booked)...");
        inventory.updateAvailability("Single Room", -1);

        // Display updated inventory
        inventory.displayInventory();

        System.out.println("\nApplication Terminated.");
    }
}