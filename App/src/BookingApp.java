import java.util.*;

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private double totalCost; // optional cost including add-ons

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.totalCost = 0;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void addToTotalCost(double cost) {
        this.totalCost += cost;
    }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room Type: " + roomType +
                ", Room ID: " + roomId + ", Total Cost: ₹" + totalCost;
    }
}

// BookingHistory class
class BookingHistory {
    private List<Reservation> confirmedReservations;

    public BookingHistory() {
        confirmedReservations = new ArrayList<>();
    }

    // Add a confirmed reservation to history
    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    // Retrieve all reservations
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedReservations);
    }

    // Generate summary report
    public void generateReport() {
        System.out.println("\n=== Booking History Report ===");
        if (confirmedReservations.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        for (Reservation res : confirmedReservations) {
            System.out.println(res);
        }

        // Example: total bookings per room type
        Map<String, Integer> countByType = new HashMap<>();
        for (Reservation res : confirmedReservations) {
            countByType.put(res.getRoomType(), countByType.getOrDefault(res.getRoomType(), 0) + 1);
        }

        System.out.println("\n--- Summary by Room Type ---");
        for (Map.Entry<String, Integer> entry : countByType.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " bookings");
        }
    }
}

// Main class
public class BookingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        BookingHistory history = new BookingHistory();

        System.out.println("Welcome to the Booking History & Reporting System.\n");

        // Simulate confirming some bookings
        history.addReservation(new Reservation("Alice", "Single Room", "SI100"));
        history.addReservation(new Reservation("Bob", "Double Room", "DO101"));
        history.addReservation(new Reservation("Charlie", "Single Room", "SI102"));
        history.addReservation(new Reservation("David", "Suite Room", "SU103"));

        boolean running = true;
        while (running) {
            System.out.println("\nOptions:");
            System.out.println("1. View all booking history");
            System.out.println("2. Generate booking summary report");
            System.out.println("3. Add new reservation");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    List<Reservation> all = history.getAllReservations();
                    System.out.println("\n--- All Reservations ---");
                    if (all.isEmpty()) {
                        System.out.println("No reservations found.");
                    } else {
                        for (Reservation r : all) {
                            System.out.println(r);
                        }
                    }
                    break;

                case "2":
                    history.generateReport();
                    break;

                case "3":
                    System.out.print("Enter guest name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter room type: ");
                    String type = scanner.nextLine().trim();
                    System.out.print("Enter room ID: ");
                    String roomId = scanner.nextLine().trim();

                    Reservation newRes = new Reservation(name, type, roomId);
                    history.addReservation(newRes);
                    System.out.println("Reservation added successfully!");
                    break;

                case "4":
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