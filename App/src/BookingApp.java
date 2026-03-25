import java.util.*;

// Reservation class
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRequestedRoomType() {
        return requestedRoomType;
    }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room Type: " + requestedRoomType;
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

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean allocateRoom(String roomType) {
        int available = getAvailability(roomType);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }
}

// BookingRequestQueue class
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation pollNextRequest() {
        return requestQueue.poll();
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    public int size() {
        return requestQueue.size();
    }
}

// RoomAllocationService class
class RoomAllocationService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRoomIds;
    private int roomCounter; // to generate unique IDs

    public RoomAllocationService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRoomIds = new HashMap<>();
        roomCounter = 100; // starting room number
    }

    public void processReservation(Reservation reservation) {
        String roomType = reservation.getRequestedRoomType();
        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("Sorry " + reservation.getGuestName() + ", no " + roomType + " available.");
            return;
        }

        // Generate unique room ID
        String roomId = generateUniqueRoomId(roomType);

        // Allocate room
        if (inventory.allocateRoom(roomType)) {
            allocatedRoomIds.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);
            System.out.println("Reservation Confirmed: " + reservation.getGuestName() +
                    " -> " + roomType + " [Room ID: " + roomId + "]");
        } else {
            System.out.println("Failed to allocate " + roomType + " for " + reservation.getGuestName());
        }
    }

    private String generateUniqueRoomId(String roomType) {
        String id;
        do {
            id = roomType.substring(0, 2).toUpperCase() + roomCounter++;
        } while (allocatedRoomIds.containsKey(roomType) && allocatedRoomIds.get(roomType).contains(id));
        return id;
    }

    public void printAllocatedRooms() {
        System.out.println("\n=== Allocated Rooms ===");
        for (Map.Entry<String, Set<String>> entry : allocatedRoomIds.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

// Main class
public class BookingApp {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Double Room", 2);
        inventory.addRoom("Suite Room", 1);

        // Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Single Room"));
        bookingQueue.addRequest(new Reservation("David", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Eve", "Double Room"));
        bookingQueue.addRequest(new Reservation("Frank", "Suite Room")); // should fail

        // Process reservations
        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        while (!bookingQueue.isEmpty()) {
            Reservation reservation = bookingQueue.pollNextRequest();
            allocationService.processReservation(reservation);
        }

        // Show allocated rooms
        allocationService.printAllocatedRooms();

        // Show remaining inventory
        System.out.println("\nRemaining Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.getInventorySnapshot().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }

        System.out.println("\nApplication Terminated.");
    }
}