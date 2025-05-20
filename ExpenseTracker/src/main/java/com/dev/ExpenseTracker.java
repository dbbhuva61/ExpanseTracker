package com.dev;


import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transactions{

    enum Type{ Income, Expense }

    private Type type;
    private String category;
    private double amount;
    private LocalDate date;

    public Transactions(Type type, String category, double amount, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "type=" + type +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}


public class ExpenseTracker {

    private static final List<Transactions> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean track = true;
        while (track) {
            System.out.println("\n--Track your Expense--");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. Load from File");
            System.out.println("5. Save to File");
            System.out.println("6. Exit");

            System.out.print("Select an option: ");
            switch (scanner.nextInt()) {
                case 1 -> addTransaction(Transactions.Type.Income);
                case 2 -> addTransaction(Transactions.Type.Expense);
                case 3 -> CalMonthlySalary();
                case 4 -> LoadFromFile();
                case 5 -> SaveToFile();
                case 6 -> track = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void addTransaction(Transactions.Type type) {
        scanner.nextLine();

        System.out.println("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Enter category: ");
        String category = scanner.nextLine();

        System.out.println("Enter date(yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.next());

        transactions.add(new Transactions(type, category, amount, date));
        System.out.println("Transaction added");
    }

    private static void CalMonthlySalary() {

        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();
        double totalIncome = 0;
        double totalExpense = 0;

        System.out.print("Enter month and year (yyyy-MM): ");
        scanner.nextLine();
        String monthInput = scanner.nextLine();


        for (Transactions transaction : transactions) {
            String month = transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (month.equals(monthInput)) {
                if(transaction.getType() == Transactions.Type.Income) {
                    incomeMap.put(transaction.getCategory(),
                            incomeMap.getOrDefault(transaction.getCategory(), 0.0)
                                    + transaction.getAmount());
                    totalIncome += transaction.getAmount();
                }
                else {
                    expenseMap.put(transaction.getCategory(),
                            expenseMap.getOrDefault(transaction.getCategory(), 0.0)
                                    + transaction.getAmount());
                    totalExpense += transaction.getAmount();
                }
            }
        }
        System.out.println("--Monthly summary --> "+monthInput);
        System.out.println();

        System.out.println("Total income: " + totalIncome);
        incomeMap.forEach((k, v) -> {
            System.out.println(k + ": " + v);});
        System.out.println();;

        System.out.println("Total expense: " + totalExpense);
        expenseMap.forEach((k, v) -> {
            System.out.println(k + ": " + v);});
        System.out.println();

        System.out.println("Net Saving: " + (totalIncome - totalExpense));
        System.out.println();
    }

    private static void LoadFromFile() {
        scanner.nextLine();

        System.out.println("Enter FileName: ");
        String filename = scanner.nextLine();

        InputStream inputStream = ExpenseTracker.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            System.out.println("File not found in resources!");
            return;
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;}

                String[] values = line.split(",");
                Transactions.Type type = Transactions.Type.valueOf(values[0].trim());
                String category = values[1].trim();
                double amount = Double.parseDouble(values[2].trim());
                LocalDate date = LocalDate.parse(values[3].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                transactions.add(new Transactions(type, category, amount, date));
            }
            System.out.println("Transaction loaded");

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }

    private static void SaveToFile() {
        scanner.nextLine();

        System.out.print("Enter filename to save(ex., output.csv): ");
        String filename = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Type,Category,Amount,Date");
            writer.newLine();

            for (Transactions t : transactions) {
                writer.write(t.getType() + "," + t.getCategory() + "," + t.getAmount() + "," + t.getDate());
                writer.newLine();
            }
            
            System.out.println("Transactions saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }


}
