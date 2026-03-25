import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

// Reservation class: represents a guest's booking request
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

// BookingRequestQueue class: manages incoming booking requests FIFO
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Add a new booking request to the queue
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Booking request added: " + reservation);
    }

    // Peek at next request without removing
    public Reservation peekNextRequest() {
        return requestQueue.peek();
    }

    // Remove and return next request
    public Reservation pollNextRequest() {
        return requestQueue.poll();
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    // Get queue size
    public int size() {
        return requestQueue.size();
    }

    // Return a copy of all requests for safe access
    public List<Reservation> getAllRequests() {
        return new ArrayList<>(requestQueue);
    }
}

// Main class to simulate booking request intake
public class BookingApp {

    public static void main(String[] args) {

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Booking Request System (Type 'exit' to quit)\n");

        while (true) {
            System.out.print("Enter guest name (or 'exit' to quit): ");
            String guestName = scanner.nextLine().trim();
            if (guestName.equalsIgnoreCase("exit")) {
                break;
            }
            if (guestName.isEmpty()) {
                System.out.println("Guest name cannot be empty. Please try again.");
                continue;
            }

            System.out.print("Enter desired room type (e.g., Single Room, Double Room, Suite Room): ");
            String roomType = scanner.nextLine().trim();
            if (roomType.isEmpty()) {
                System.out.println("Room type cannot be empty. Please try again.");
                continue;
            }

            Reservation reservation = new Reservation(guestName, roomType);
            bookingQueue.addRequest(reservation);
            System.out.println("Current queue size: " + bookingQueue.size());
            System.out.println();
        }

        System.out.println("\nAll booking requests received. Total requests: " + bookingQueue.size());
        System.out.println("Booking requests in arrival order:");

        // Display all requests safely using getter
        for (Reservation r : bookingQueue.getAllRequests()) {
            System.out.println(r);
        }

        scanner.close();
        System.out.println("\nApplication Terminated.");
    }
}