import java.util.*;

// Custom exception for invalid booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room Type: " + roomType + ", Room ID: " + roomId;
    }
}

// RoomInventory class with validation
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoom(String roomType, int count) throws InvalidBookingException {
        if (count < 0) {
            throw new InvalidBookingException("Cannot add negative inventory for " + roomType);
        }
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Room type not found: " + roomType);
        }
        return inventory.get(roomType);
    }

    public void allocateRoom(String roomType) throws InvalidBookingException {
        int available = getAvailability(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("No available rooms for type: " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }
}

// Main program
public class BookingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();

        // Initialize inventory safely
        try {
            inventory.addRoom("Single Room", 2);
            inventory.addRoom("Double Room", 2);
            inventory.addRoom("Suite Room", 1);
        } catch (InvalidBookingException e) {
            System.out.println("Error initializing inventory: " + e.getMessage());
            return;
        }

        System.out.println("Welcome to the Safe Booking System.\n");

        List<Reservation> confirmedReservations = new ArrayList<>();
        boolean running = true;

        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. Book a room");
            System.out.println("2. View inventory");
            System.out.println("3. View confirmed reservations");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter guest name: ");
                        String name = scanner.nextLine().trim();
                        if (name.isEmpty()) {
                            throw new InvalidBookingException("Guest name cannot be empty.");
                        }

                        System.out.print("Enter room type (Single Room / Double Room / Suite Room): ");
                        String roomType = scanner.nextLine().trim();
                        inventory.getAvailability(roomType); // validates room type

                        // Allocate room safely
                        inventory.allocateRoom(roomType);

                        // Generate unique room ID
                        String roomId = roomType.substring(0, 2).toUpperCase() + (100 + confirmedReservations.size());
                        Reservation res = new Reservation(name, roomType, roomId);
                        confirmedReservations.add(res);

                        System.out.println("Booking Confirmed: " + res);

                    } catch (InvalidBookingException e) {
                        System.out.println("Booking Error: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\nCurrent Inventory:");
                    for (Map.Entry<String, Integer> entry : inventory.getInventorySnapshot().entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
                    }
                    break;

                case "3":
                    System.out.println("\nConfirmed Reservations:");
                    if (confirmedReservations.isEmpty()) {
                        System.out.println("No reservations yet.");
                    } else {
                        for (Reservation res : confirmedReservations) {
                            System.out.println(res);
                        }
                    }
                    break;

                case "4":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
        System.out.println("\nApplication Terminated.");
    }
}