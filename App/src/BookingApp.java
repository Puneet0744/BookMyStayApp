import java.util.*;

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

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room Type: " + roomType + " [Room ID: " + roomId + "]";
    }
}

// Add-On Service class
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Add-On Service Manager
class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    // Add a service to a reservation by room ID
    public void addService(String roomId, AddOnService service) {
        reservationServices.computeIfAbsent(roomId, k -> new ArrayList<>()).add(service);
    }

    // Retrieve all services for a reservation
    public List<AddOnService> getServices(String roomId) {
        return reservationServices.getOrDefault(roomId, new ArrayList<>());
    }

    // Calculate total cost for add-ons
    public double calculateTotalCost(String roomId) {
        double total = 0;
        for (AddOnService service : getServices(roomId)) {
            total += service.getCost();
        }
        return total;
    }

    // Print all services for all reservations
    public void printAllServices() {
        System.out.println("\n=== Add-On Services Summary ===");
        for (Map.Entry<String, List<AddOnService>> entry : reservationServices.entrySet()) {
            System.out.println("Reservation Room ID: " + entry.getKey());
            List<AddOnService> services = entry.getValue();
            if (services.isEmpty()) {
                System.out.println("  No add-on services selected.");
            } else {
                for (AddOnService service : services) {
                    System.out.println("  - " + service);
                }
                System.out.println("  Total Add-On Cost: ₹" + calculateTotalCost(entry.getKey()));
            }
            System.out.println();
        }
    }
}

// Main program
public class BookingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Example reservations from previous allocation
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation("Alice", "Single Room", "SI100"));
        reservations.add(new Reservation("Bob", "Double Room", "DO101"));
        reservations.add(new Reservation("Charlie", "Single Room", "SI102"));
        reservations.add(new Reservation("David", "Suite Room", "SU103"));

        // Initialize Add-On Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        System.out.println("Welcome to Add-On Service Selection.\n");

        for (Reservation res : reservations) {
            System.out.println("Select services for reservation: " + res);
            boolean adding = true;

            while (adding) {
                System.out.println("Available Services: ");
                System.out.println("1. Breakfast (₹300)");
                System.out.println("2. Airport Pickup (₹500)");
                System.out.println("3. Spa Access (₹800)");
                System.out.println("4. Done selecting services");

                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        serviceManager.addService(res.getRoomId(), new AddOnService("Breakfast", 300));
                        System.out.println("Added Breakfast.");
                        break;
                    case "2":
                        serviceManager.addService(res.getRoomId(), new AddOnService("Airport Pickup", 500));
                        System.out.println("Added Airport Pickup.");
                        break;
                    case "3":
                        serviceManager.addService(res.getRoomId(), new AddOnService("Spa Access", 800));
                        System.out.println("Added Spa Access.");
                        break;
                    case "4":
                        adding = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select again.");
                        break;
                }
            }
            System.out.println();
        }

        // Print all selected services
        serviceManager.printAllServices();

        scanner.close();
        System.out.println("\nApplication Terminated.");
    }
}