import java.util.*;

// Global runtime database to hold users and the active session
class DataStore {
    public static ArrayList<User> users = new ArrayList<>();
    public static User currentUser; 
}

// User Profile model
class User {
    private String name, email, password;
    private double budget;
    private ArrayList<Subscription> subscriptions;

    public User(String name, String email, String password, double budget) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.budget = budget;
        this.subscriptions = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getBudget() { return budget; }
    public ArrayList<Subscription> getSubscriptions() { return subscriptions; }

    public void addSubscription(Subscription sub) {
        subscriptions.add(sub);
    }
}

// Handles simple sign-up validation and login matching
class AuthService {
    public boolean signup(String name, String email, String password, double budget) {
        for (User u : DataStore.users) {
            if (u.getEmail().equals(email)) {
                System.out.println("User already exists");
                return false;
            }
        }
        DataStore.users.add(new User(name, email, password, budget));
        System.out.println("Signup successful");
        return true;
    }

    public boolean login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Fields cannot be empty");
            return false;
        }
        for (User u : DataStore.users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                DataStore.currentUser = u;
                System.out.println("Login successful");
                return true;
            }
        }
        System.out.println("Invalid credentials");
        return false;
    }
}

interface Displayable {
    void displayDetails();
}

// Base tracking template for regular and personalized tier subscriptions
abstract class Subscription implements Displayable {
    protected String name, category, planType;
    protected double cost;
    protected int totalDays, daysUsed, usageHours;

    // Full constructor for existing manual tracking entries
    public Subscription(String name, String category, String planType, double cost, int totalDays, int daysUsed, int usageHours) {
        this.name = name;
        this.category = category;
        this.planType = planType;
        this.cost = cost;
        this.totalDays = totalDays;
        this.daysUsed = daysUsed;
        this.usageHours = usageHours;
    }

    // Overloaded variant initialization for system-generated catalog recommendations
    public Subscription(String name, String category, String planType, double cost, int totalDays) {
        this.name = name;
        this.category = category;
        this.planType = planType;
        this.cost = cost;
        this.totalDays = totalDays;
        this.daysUsed = 0;
        this.usageHours = 0;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getCost() { return cost; }
    public int getUsageHours() { return usageHours; }

    public int getDaysLeft() {
        int remaining = totalDays - daysUsed;
        if (remaining < 0) {
            System.out.println("Days used cannot be greater than total Days.");
            return 0;
        }
        return remaining;
    }

    public double getUsagePercentage() {
        if (totalDays <= 0) return 0;
        return (daysUsed * 100.0) / totalDays;
    }

    public boolean isFree() { return cost == 0; }
}

class OTTSubscription extends Subscription {
    public OTTSubscription(String name, String planType, double cost, int t, int d, int u) {
        super(name, "Entertainment", planType, cost, t, d, u);
    }
    public OTTSubscription(String name, String planType, double cost, int totalDays) {
        super(name, "Entertainment", planType, cost, totalDays);
    }
    @Override
    public void displayDetails() {
        System.out.println(name + " (" + planType + ") - Rs." + cost);
    }
}

class MusicSubscription extends Subscription {
    public MusicSubscription(String name, String planType, double cost, int t, int d, int u) {
        super(name, "Music", planType, cost, t, d, u);
    }
    public MusicSubscription(String name, String planType, double cost, int totalDays) {
        super(name, "Music", planType, cost, totalDays);
    }
    @Override
    public void displayDetails() {
        System.out.println(name + " (" + planType + ") - Rs." + cost);
    }
}

class ShoppingSubscription extends Subscription {
    public ShoppingSubscription(String name, String planType, double cost, int t, int d, int u) {
        super(name, "Shopping", planType, cost, t, d, u);
    }
    public ShoppingSubscription(String name, String planType, double cost, int totalDays) {
        super(name, "Shopping", planType, cost, totalDays);
    }
    @Override
    public void displayDetails() {
        System.out.println(name + " (" + planType + ") - Rs." + cost);
    }
}

class AcademicSubscription extends Subscription {
    public AcademicSubscription(String name, String planType, double cost, int t, int d, int u) {
        super(name, "Academics", planType, cost, t, d, u);
    }
    public AcademicSubscription(String name, String planType, double cost, int totalDays) {
        super(name, "Academics", planType, cost, totalDays);
    }
    @Override
    public void displayDetails() {
        System.out.println(name + " (" + planType + ") - Rs." + cost);
    }
}

// Processing hub for storing and sorting sub feeds
class SubscriptionManager {
    public void addSubscription(User user, Subscription sub) {
        user.addSubscription(sub);
    }

    public void addSubscription(User user, String name, String category, String planType, double cost, int total, int used, int usage) {
        Subscription sub;
        if (category.equalsIgnoreCase("Entertainment")) sub = new OTTSubscription(name, planType, cost, total, used, usage);
        else if (category.equalsIgnoreCase("Music")) sub = new MusicSubscription(name, planType, cost, total, used, usage);
        else if (category.equalsIgnoreCase("Shopping")) sub = new ShoppingSubscription(name, planType, cost, total, used, usage);
        else sub = new AcademicSubscription(name, planType, cost, total, used, usage);
        user.addSubscription(sub);
    }

    public void viewSubscriptions(User user) {
        if (user.getSubscriptions().isEmpty()) {
            System.out.println("No subscriptions found.");
            return;
        }
        for (Subscription s : user.getSubscriptions()) {
            s.displayDetails();
            System.out.println("Days left: " + s.getDaysLeft());
            System.out.printf("Usage: %.2f%%\n", s.getUsagePercentage());
            System.out.println("----------------------");
        }
    }

    public void viewSubscriptionsByCategory(User user, String category) {
        boolean found = false;
        for (Subscription s : user.getSubscriptions()) {
            if (s.getCategory().equalsIgnoreCase(category)) {
                s.displayDetails();
                System.out.println("Days left: " + s.getDaysLeft());
                System.out.printf("Usage: %.2f%%\n", s.getUsagePercentage());
                System.out.println("----------------------");
                found = true;
            }
        }
        if (!found) System.out.println("No subscriptions found in this category.");
    }
}

// Evaluation calculations for financial metrics and tracking flags
class SmartAnalyzer {
    public void showReminders(User user) {
        System.out.println("\nRenewal Reminders:");
        boolean found = false;
        for (Subscription s : user.getSubscriptions()) {
            if (s.getDaysLeft() <= 10) {
                System.out.println(s.getName() + " expires in " + s.getDaysLeft() + " days");
                found = true;
            }
        }
        if (!found) System.out.println("No upcoming renewals.");
    }

    public void suggestCancellation(User user) {
        System.out.println("\nCancellation Suggestions:");
        boolean found = false;
        for (Subscription s : user.getSubscriptions()) {
            if (s.getUsagePercentage() > 50 && s.getUsageHours() < 10) {
                System.out.println("Consider cancelling: " + s.getName());
                found = true;
            }
        }
        if (!found) System.out.println("All subscriptions are being used effectively");
    }

    public double calculateTotalCost(User user) {
        double total = 0;
        for (Subscription s : user.getSubscriptions()) {
            total += s.getCost();
        }
        return total;
    }

    public void checkBudget(User user, SubscriptionManager manager) {
        double total = calculateTotalCost(user);
        System.out.println("\nBudget Analysis:");
        if (total > user.getBudget()) System.out.println("Budget exceeded!");
        else System.out.println("Within budget.");
    }
}

// Fixed plan directory
class PlanData {
    public static ArrayList<Subscription> getPlans() {
        ArrayList<Subscription> plans = new ArrayList<>();
        plans.add(new OTTSubscription("Netflix", "Individual", 199, 30));
        plans.add(new OTTSubscription("JioHotstar", "Individual", 149, 30));
        plans.add(new OTTSubscription("Amazon Prime Video", "Family", 299, 30));
        plans.add(new OTTSubscription("MX Player", "Individual", 0, 30));
        plans.add(new MusicSubscription("Spotify", "Individual", 119, 30));
        plans.add(new MusicSubscription("YouTube Music", "Individual", 129, 30));
        plans.add(new MusicSubscription("Gaana", "Individual", 99, 30));
        plans.add(new ShoppingSubscription("Amazon Prime", "Family", 299, 30));
        plans.add(new ShoppingSubscription("Flipkart Plus", "Individual", 0, 30));
        plans.add(new ShoppingSubscription("BigBasket", "Family", 0, 30));
        plans.add(new AcademicSubscription("Coursera Plus", "Individual", 0, 365));
        plans.add(new AcademicSubscription("Physics Wallah", "Individual", 500, 365));
        plans.add(new AcademicSubscription("Vedantu", "Individual", 1000, 365));
        return plans;
    }
}

// Marketplace pricing filters and dynamic suggestions engine
class RecommendationEngine {
    public void recommendPlans(User user, ArrayList<Subscription> plans, int categoryChoice, int typeChoice, boolean paidOnly) {
        String[] categories = {"Entertainment", "Music", "Shopping", "Academics"};
        String[] types = {"Individual", "Family"};
        ArrayList<Subscription> filtered = new ArrayList<>();

        for (Subscription s : plans) {
            if (s.getCategory().equalsIgnoreCase(categories[categoryChoice - 1]) && 
                s.planType.equalsIgnoreCase(types[typeChoice - 1]) && 
                s.getCost() <= user.getBudget() && 
                (!paidOnly || !s.isFree())) {
                filtered.add(s);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No matching plans.");
            return;
        }

        filtered.sort(Comparator.comparingDouble(Subscription::getCost));
        System.out.println("\nTop 3 Cheapest Plans:");
        for (int i = 0; i < Math.min(3, filtered.size()); i++) {
            filtered.get(i).displayDetails();
        }

        System.out.println("\nFree Options:");
        boolean free = false;
        for (Subscription s : filtered) {
            if (s.isFree()) {
                s.displayDetails();
                free = true;
            }
        }
        if (!free) System.out.println("None");

        System.out.println("\nPaid Options:");
        boolean paid = false;
        for (Subscription s : filtered) {
            if (!s.isFree()) {
                s.displayDetails();
                paid = true;
            }
        }
        if (!paid) System.out.println("None");
    }
}

public class Practice {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService();
        System.out.println("SMART SUBSCRIPTION MANAGEMENT SYSTEM");
        int choice;
        boolean loggedIn = false;

        do {
            System.out.println("\n1. Signup\n2. Login\n3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1: {
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.next();
                    System.out.print("Password: ");
                    String pass = sc.next();
                    System.out.print("Budget: ");
                    double budget = sc.nextDouble();

                    if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                        System.out.println("Fields cannot be empty");
                        break;
                    }
                    if (budget < 0) {
                        System.out.println("Budget cannot be negative");
                        break;
                    }
                    auth.signup(name, email, pass, budget);
                    break;
                }
                case 2: {
                    System.out.print("Email: ");
                    String email = sc.next();
                    System.out.print("Password: ");
                    String pass = sc.next();
                    if (auth.login(email, pass)) loggedIn = true;
                    break;
                }
                case 3: return;
                default: System.out.println("Invalid choice.");
            }
        } while (!loggedIn);

        System.out.println("\nWelcome, " + DataStore.currentUser.getName());
        SubscriptionManager manager = new SubscriptionManager();
        SmartAnalyzer analyzer = new SmartAnalyzer();
        RecommendationEngine engine = new RecommendationEngine();
        ArrayList<Subscription> plans = PlanData.getPlans();

        do {
            System.out.println("\n1. Add Your Existing Subscription");
            System.out.println("2. View Your Subscriptions");
            System.out.println("3. Analyze");
            System.out.println("4. Recommend Plans");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nSelect category:\n1. Entertainment\n2. Music\n3. Shopping\n4. Academics");
                    int cat = sc.nextInt();
                    if (cat < 1 || cat > 4) {
                        System.out.println("Invalid category.");
                        break;
                    }
                    System.out.println("Select Plan Type:\n1. Individual\n2. Family");
                    int typeChoice = sc.nextInt();
                    sc.nextLine();
                    String planType = (typeChoice == 1) ? "Individual" : (typeChoice == 2) ? "Family" : "";
                    if (planType.isEmpty()) {
                        System.out.println("Invalid plan type.");
                        break;
                    }
                    System.out.print("Subscription Name: ");
                    String name = sc.nextLine();
                    System.out.print("Cost: ");
                    double cost = sc.nextDouble();
                    if (cost < 0) {
                        System.out.println("Invalid cost.");
                        break;
                    }
                    System.out.print("Total days: ");
                    int total = sc.nextInt();
                    System.out.print("Days used: ");
                    int used = sc.nextInt();
                    if (used < 0 || used > total) {
                        System.out.println("Invalid usage.");
                        break;
                    }
                    System.out.print("Usage hours: ");
                    int usage = sc.nextInt();
                    if (usage < 0) {
                        System.out.println("Invalid usage hours.");
                        break;
                    }
                    String[] cats = {"Entertainment", "Music", "Shopping", "Academics"};
                    manager.addSubscription(DataStore.currentUser, name, cats[cat - 1], planType, cost, total, used, usage);
                    System.out.println("Added successfully!");
                    break;

                case 2:
                    System.out.println("\n1. Show All\n2. Entertainment\n3. Music\n4. Shopping\n5. Academics");
                    int viewChoice = sc.nextInt();
                    if (viewChoice == 1) manager.viewSubscriptions(DataStore.currentUser);
                    else if (viewChoice >= 2 && viewChoice <= 5) {
                        String[] viewCats = {"Entertainment", "Music", "Shopping", "Academics"};
                        manager.viewSubscriptionsByCategory(DataStore.currentUser, viewCats[viewChoice - 2]);
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;

                case 3:
                    analyzer.showReminders(DataStore.currentUser);
                    analyzer.suggestCancellation(DataStore.currentUser);
                    double ttl = analyzer.calculateTotalCost(DataStore.currentUser);
                    System.out.println("Total Cost: " + ttl);
                    analyzer.checkBudget(DataStore.currentUser, manager);
                    break;

                case 4:
                    System.out.println("1. Entertainment\n2. Music\n3. Shopping\n4. Academics");
                    System.out.println("\nChoose Category:");
                    int c = sc.nextInt();
                    if (c < 1 || c > 4) {
                        System.out.println("Invalid category.");
                        break;
                    }
                    System.out.println("Type: 1. Individual 2. Family");
                    int t = sc.nextInt();
                    if (t != 1 && t != 2) {
                        System.out.println("Invalid type.");
                        break;
                    }
                    System.out.println("Only paid? 1.Yes 2.No");
                    int paidInput = sc.nextInt();
                    if (paidInput != 1 && paidInput != 2) {
                        System.out.println("Invalid choice.");
                        break;
                    }
                    engine.recommendPlans(DataStore.currentUser, plans, c, t, (paidInput == 1));
                    break;

                case 5:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }
}