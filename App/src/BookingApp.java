import java.io.*;
import java.util.*;

// Serializable Reservation class
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
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

// Serializable RoomInventory class
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> inventory;
    private int roomCounter;

    public RoomInventory() {
        inventory = new HashMap<>();
        roomCounter = 100;
    }

    public void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public synchronized String allocateRoom(String roomType) throws Exception {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) throw new Exception("No available rooms for type: " + roomType);
        inventory.put(roomType, available - 1);
        roomCounter++;
        return roomType.substring(0, 2).toUpperCase() + roomCounter;
    }

    public synchronized void releaseRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, available + 1);
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }

    public int getRoomCounter() { return roomCounter; }
    public void setRoomCounter(int counter) { this.roomCounter = counter; }

    public void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
    }
}

// PersistenceService class for saving and loading system state
class PersistenceService {
    private static final String FILE_NAME = "hotel_state.dat";

    public static void saveState(RoomInventory inventory, List<Reservation> reservations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(reservations);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Object[] loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            List<Reservation> reservations = (List<Reservation>) ois.readObject();
            System.out.println("System state restored successfully.");
            return new Object[]{inventory, reservations};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state: " + e.getMessage());
            return null;
        }
    }
}

// Main program
public class BookingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory;
        List<Reservation> reservations;

        // Attempt to load previous state
        Object[] loadedState = PersistenceService.loadState();
        if (loadedState != null) {
            inventory = (RoomInventory) loadedState[0];
            reservations = (List<Reservation>) loadedState[1];
        } else {
            inventory = new RoomInventory();
            reservations = new ArrayList<>();
            // Initialize default inventory
            inventory.addRoom("Single Room", 2);
            inventory.addRoom("Double Room", 2);
            inventory.addRoom("Suite Room", 1);
        }

        boolean running = true;
        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. Book a room");
            System.out.println("2. View inventory");
            System.out.println("3. View confirmed reservations");
            System.out.println("4. Cancel reservation");
            System.out.println("5. Save and Exit");

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    try {
                        System.out.print("Guest name: ");
                        String name = scanner.nextLine().trim();
                        if (name.isEmpty()) throw new Exception("Guest name cannot be empty.");

                        System.out.print("Room type (Single Room / Double Room / Suite Room): ");
                        String type = scanner.nextLine().trim();

                        String roomId = inventory.allocateRoom(type);
                        Reservation res = new Reservation(name, type, roomId);
                        reservations.add(res);
                        System.out.println("Booking Confirmed: " + res);
                    } catch (Exception e) {
                        System.out.println("Booking Error: " + e.getMessage());
                    }
                    break;

                case "2":
                    inventory.printInventory();
                    break;

                case "3":
                    System.out.println("\nConfirmed Reservations:");
                    if (reservations.isEmpty()) {
                        System.out.println("No reservations yet.");
                    } else {
                        for (Reservation res : reservations) {
                            System.out.println(res);
                        }
                    }
                    break;

                case "4":
                    System.out.print("Enter Room ID to cancel: ");
                    String roomId = scanner.nextLine().trim();
                    Optional<Reservation> resOpt = reservations.stream()
                            .filter(r -> r.getRoomId().equals(roomId))
                            .findFirst();
                    if (resOpt.isPresent()) {
                        Reservation r = resOpt.get();
                        if (!r.isCancelled()) {
                            r.cancel();
                            inventory.releaseRoom(r.getRoomType());
                            System.out.println("Reservation cancelled: " + r);
                        } else {
                            System.out.println("Reservation already cancelled: " + roomId);
                        }
                    } else {
                        System.out.println("No reservation found with Room ID: " + roomId);
                    }
                    break;

                case "5":
                    PersistenceService.saveState(inventory, reservations);
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
        System.out.println("\nApplication Terminated.");
    }
}