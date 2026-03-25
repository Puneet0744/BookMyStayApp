import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

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

// Centralized Inventory Class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int change) {
        int current = getAvailability(roomType);
        inventory.put(roomType, current + change);
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }
}

// Read-only search service with interactive input
class RoomSearchService {

    private RoomInventory inventory;
    private List<Room> rooms;

    public RoomSearchService(RoomInventory inventory, List<Room> rooms) {
        this.inventory = inventory;
        this.rooms = rooms;
    }

    public void interactiveSearch() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Room Search. Type 'exit' to quit.\n");

        while (true) {
            System.out.print("Enter room type (Single Room, Double Room, Suite Room) to search: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting application.");
                break;
            }

            int availability = inventory.getAvailability(input);

            if (availability > 0) {
                boolean found = false;
                for (Room room : rooms) {
                    if (room.getRoomType().equalsIgnoreCase(input)) {
                        room.displayRoomDetails();
                        System.out.println("Available: " + availability);
                        System.out.println("-----------------------------------");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Room type '" + input + "' not recognized in system.");
                }
            } else {
                System.out.println("Room type '" + input + "' is not available or does not exist.");
            }
        }

        scanner.close();
    }
}

// Main Class
public class BookingApp {

    public static void main(String[] args) {

        // Initialize rooms
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Initialize inventory with some availability
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 5);
        inventory.addRoom("Double Room", 3);
        inventory.addRoom("Suite Room", 0); // Suites currently unavailable

        // Initialize search service
        RoomSearchService searchService = new RoomSearchService(inventory, rooms);

        // Start interactive search loop
        searchService.interactiveSearch();

        System.out.println("\nApplication Terminated.");
    }
}