import java.util.*;
import java.sql.*;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String RED = "\u001B[31m";

    private static final String DATE_REGEX = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$";
    private static Scanner scanner = new Scanner(System.in);
    private static double monthlyBudget = 0;

    public static void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        System.out.println(CYAN + "========================================");
        System.out.println("   Welcome to Your Expense Manager!   ");
        System.out.println("========================================" + RESET);

        
        
        DBHelper.createDatabase();
        DBHelper.createTable();

        while (true) {
            showMenu();
            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
                pressEnterToContinue();
                continue;
            }

            switch (choice) {
                case 1: addExpense(); break;
                case 2: viewAllExpenses(); break;
                case 3: viewTotalExpenses(); break;
                case 4: viewExpensesByCategory(); break;
                case 5: setMonthlyBudget(); break;
                case 6: checkBudgetStatus(); break;
                case 7: deleteExpense(); break;
                case 8:
                    System.out.println(CYAN + "\nThank you for using Expense Manager! Goodbye! 👋" + RESET);
                    return;
                default:
                    System.out.println(RED + "Invalid choice! Please select an option from 1 to 8." + RESET);
            }
            pressEnterToContinue();
        }
    }

    public static void showMenu() {
        System.out.println(BLUE + "\n+-------------------- MENU --------------------+" + RESET);
        System.out.println("| 1. Add Expense                         |");
        System.out.println("| 2. View All Expenses                   |");
        System.out.println("| 3. View Total Expenses                 |");
        System.out.println("| 4. View Expenses by Category           |");
        System.out.println("| 5. Set Monthly Budget                  |");
        System.out.println("| 6. Check Budget Status                 |");
        System.out.println("| 7. Delete Expense                      |");
        System.out.println("| 8. Exit                                |");
        System.out.println(BLUE + "+------------------------------------------+" + RESET);
        System.out.print("Enter your choice (1-8): ");
    }

    public static void addExpense() {
        System.out.println(YELLOW + "\n--- Add New Expense ---" + RESET);
        String date;
        while (true) {
            System.out.print("Enter date (DD/MM/YYYY): ");
            date = scanner.nextLine();
            if (date.matches(DATE_REGEX)) break;
            else System.out.println(RED + "Invalid date format. Please use DD/MM/YYYY." + RESET);
        }

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        double amount = -1;
        while (amount < 0) {
            try {
                System.out.print("Enter amount: ");
                amount = Double.parseDouble(scanner.nextLine());
                if (amount < 0) System.out.println(RED + "Amount cannot be negative." + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }

        System.out.print("Enter category (Food/Transport/Entertainment/Shopping/Bills/Other): ");
        String category = scanner.nextLine();

        try (Connection conn = DBHelper.connectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO expenses (date, description, amount, category) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, date);
            ps.setString(2, description);
            ps.setDouble(3, amount);
            ps.setString(4, category);
            ps.executeUpdate();
            System.out.println(GREEN + "\n✓ Expense added successfully!" + RESET);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewAllExpenses() {
        System.out.println(YELLOW + "\n--- All Expenses ---" + RESET);
        try (Connection conn = DBHelper.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses")) {

            String header = String.format("%-5s %-12s %-25s %-15s %s", "ID", "Date", "Description", "Amount", "Category");
            System.out.println(BLUE + header + RESET);
            System.out.println(BLUE + "---------------------------------------------------------------------------" + RESET);

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-5d %-12s %-25s ₹%-14.2f %s\n",
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"));
            }
            if (!hasRows) System.out.println("No expenses found in database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteExpense() {
        viewAllExpenses();
        System.out.print(YELLOW + "\nEnter the expense ID to delete (or 0 to cancel): " + RESET);
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid input." + RESET);
            return;
        }
        if (id == 0) return;

        try (Connection conn = DBHelper.connectDB();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id=?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println(GREEN + "✓ Expense deleted successfully!" + RESET);
            else System.out.println(RED + "No expense found with that ID." + RESET);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setMonthlyBudget() {
        System.out.print(YELLOW + "\nEnter your monthly budget: ₹" + RESET);
        try {
            monthlyBudget = Double.parseDouble(scanner.nextLine());
            System.out.printf("%s✓ Monthly budget set to: ₹%.2f%s\n", GREEN, monthlyBudget, RESET);
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid input. Try again." + RESET);
        }
    }

    public static void viewTotalExpenses() {
        try (Connection conn = DBHelper.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS total FROM expenses")) {
            if (rs.next()) {
                double total = rs.getDouble("total");
                System.out.printf("Total Spent: %s₹%.2f%s\n", GREEN, total, RESET);
            } else System.out.println("No expenses recorded.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewExpensesByCategory() {
        System.out.println(YELLOW + "\n--- Expenses by Category ---" + RESET);
        try (Connection conn = DBHelper.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category")) {
            System.out.printf("%-20s | %s\n", "Category", "Total Amount");
            System.out.println(BLUE + "----------------------------------" + RESET);
            while (rs.next()) {
                System.out.printf("%-20s | ₹%.2f\n",
                        rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void checkBudgetStatus() {
        if (monthlyBudget == 0) {
            System.out.println(YELLOW + "\nSet your monthly budget first (Option 5)" + RESET);
            return;
        }
        try (Connection conn = DBHelper.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS total FROM expenses")) {
            if (rs.next()) {
                double totalSpent = rs.getDouble("total");
                double remaining = monthlyBudget - totalSpent;
                System.out.printf("Monthly Budget: ₹%.2f\nTotal Spent: ₹%.2f\n", monthlyBudget, totalSpent);
                if (remaining >= 0)
                    System.out.printf("%sRemaining Budget: ₹%.2f%s\n", GREEN, remaining, RESET);
                else
                    System.out.printf("%sOver Budget by: ₹%.2f%s\n", RED, -remaining, RESET);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
