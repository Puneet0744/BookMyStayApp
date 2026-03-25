import java.util.*;

// Custom exception for invalid operations
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean cancelled;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.cancelled = false;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return cancelled; }

    public void cancel() { this.cancelled = true; }

    @Override
    public String toString() {
        return guestName + " | " + roomType + " | " + roomId +
                (cancelled ? " [Cancelled]" : "");
    }
}

// RoomInventory class
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void allocateRoom(String roomType) throws BookingException {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new BookingException("No available rooms for type: " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public void releaseRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, available + 1);
    }

    public void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
    }
}

// CancellationService class
class CancellationService {
    private RoomInventory inventory;
    private Stack<String> rollbackStack; // stores cancelled room IDs

    public CancellationService(RoomInventory inventory) {
        this.inventory = inventory;
        this.rollbackStack = new Stack<>();
    }

    public void cancelReservation(Reservation res) throws BookingException {
        if (res.isCancelled()) {
            throw new BookingException("Reservation already cancelled: " + res.getRoomId());
        }

        // Rollback inventory
        inventory.releaseRoom(res.getRoomType());

        // Record room ID for rollback
        rollbackStack.push(res.getRoomId());

        // Mark reservation as cancelled
        res.cancel();

        System.out.println("Reservation cancelled successfully: " + res);
    }

    public void printRollbackStack() {
        System.out.println("\nRecently Cancelled Room IDs (LIFO): " + rollbackStack);
    }
}

// Main program
public class BookingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Double Room", 2);
        inventory.addRoom("Suite Room", 1);

        List<Reservation> reservations = new ArrayList<>();
        CancellationService cancellationService = new CancellationService(inventory);

        // Pre-fill reservations
        try {
            inventory.allocateRoom("Single Room");
            reservations.add(new Reservation("Alice", "Single Room", "SI100"));
            inventory.allocateRoom("Double Room");
            reservations.add(new Reservation("Bob", "Double Room", "DO101"));
            inventory.allocateRoom("Single Room");
            reservations.add(new Reservation("Charlie", "Single Room", "SI102"));
        } catch (BookingException e) {
            System.out.println("Error initializing reservations: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. View confirmed reservations");
            System.out.println("2. Cancel a reservation");
            System.out.println("3. View inventory");
            System.out.println("4. View rollback stack");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\nConfirmed Reservations:");
                    if (reservations.isEmpty()) {
                        System.out.println("No reservations yet.");
                    } else {
                        for (Reservation res : reservations) {
                            System.out.println(res);
                        }
                    }
                    break;

                case "2":
                    System.out.print("Enter Room ID to cancel: ");
                    String roomId = scanner.nextLine().trim();
                    Optional<Reservation> optionalRes = reservations.stream()
                            .filter(r -> r.getRoomId().equals(roomId))
                            .findFirst();
                    if (optionalRes.isPresent()) {
                        try {
                            cancellationService.cancelReservation(optionalRes.get());
                        } catch (BookingException e) {
                            System.out.println("Cancellation Error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("No reservation found with Room ID: " + roomId);
                    }
                    break;

                case "3":
                    inventory.printInventory();
                    break;

                case "4":
                    cancellationService.printRollbackStack();
                    break;

                case "5":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }

        scanner.close();
        System.out.println("\nApplication Terminated.");
    }
}