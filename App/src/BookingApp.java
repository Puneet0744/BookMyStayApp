import java.util.*;
import java.util.concurrent.*;

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
        return guestName + " booked " + roomType + " (Room ID: " + roomId + ")";
    }
}

// Thread-safe RoomInventory
class RoomInventory {
    private Map<String, Integer> inventory;
    private int roomCounter = 100;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public synchronized void addRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // Synchronized allocation to prevent race conditions
    public synchronized String allocateRoom(String roomType) throws Exception {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new Exception("No available rooms for type: " + roomType);
        }
        inventory.put(roomType, available - 1);
        roomCounter++;
        return roomType.substring(0, 2).toUpperCase() + roomCounter;
    }

    public synchronized void printInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " available");
        }
    }
}

// BookingRequest represents a guest's booking request
class BookingRequest {
    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// BookingProcessor handles concurrent booking requests
class BookingProcessor implements Runnable {
    private BlockingQueue<BookingRequest> requestQueue;
    private RoomInventory inventory;
    private List<Reservation> confirmedReservations;

    public BookingProcessor(BlockingQueue<BookingRequest> queue, RoomInventory inventory,
                            List<Reservation> confirmedReservations) {
        this.requestQueue = queue;
        this.inventory = inventory;
        this.confirmedReservations = confirmedReservations;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request = null;
            try {
                request = requestQueue.poll(1, TimeUnit.SECONDS);
                if (request == null) break; // no more requests
                String roomId = inventory.allocateRoom(request.getRoomType());
                Reservation res = new Reservation(request.getGuestName(), request.getRoomType(), roomId);
                synchronized (confirmedReservations) {
                    confirmedReservations.add(res);
                }
                System.out.println("Confirmed: " + res);
            } catch (Exception e) {
                System.out.println("Booking failed for " +
                        (request != null ? request.getGuestName() : "Unknown") +
                        ": " + e.getMessage());
            }
        }
    }
}

// Main program
public class BookingApp {

    public static void main(String[] args) throws InterruptedException {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 3);
        inventory.addRoom("Double Room", 2);
        inventory.addRoom("Suite Room", 1);

        List<Reservation> confirmedReservations = Collections.synchronizedList(new ArrayList<>());

        // Shared booking request queue
        BlockingQueue<BookingRequest> bookingQueue = new LinkedBlockingQueue<>();
        bookingQueue.add(new BookingRequest("Alice", "Single Room"));
        bookingQueue.add(new BookingRequest("Bob", "Double Room"));
        bookingQueue.add(new BookingRequest("Charlie", "Single Room"));
        bookingQueue.add(new BookingRequest("David", "Suite Room"));
        bookingQueue.add(new BookingRequest("Eve", "Single Room"));
        bookingQueue.add(new BookingRequest("Frank", "Double Room"));

        // Create multiple threads to simulate concurrent guests
        int numThreads = 3;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(new BookingProcessor(bookingQueue, inventory, confirmedReservations));
            threads.add(t);
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\nAll booking requests processed.");
        inventory.printInventory();

        System.out.println("\nConfirmed Reservations:");
        for (Reservation res : confirmedReservations) {
            System.out.println(res);
        }
    }
}