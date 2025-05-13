import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

interface Payable {
    boolean processPayment(Order order);
}

abstract class Payment {
    protected String paymentID;
    protected double amount;

    public Payment(String paymentID, double amount) {
        this.paymentID = paymentID;
        this.amount = amount;
    }
}

class CashPayment extends Payment implements Payable {
    private double cashReceived;

    public CashPayment(String paymentID, double amount, double cashReceived) {
        super(paymentID, amount);
        if (cashReceived < 0) {
            throw new IllegalArgumentException("Cash received cannot be negative.");
        }
        this.cashReceived = cashReceived;
    }

    @Override
    public boolean processPayment(Order order) {
        if (cashReceived >= amount) {
            order.getVendor().updateEarnings(amount);
            return true;
        }
        return false;
    }

    public double calculateChange() {
        return cashReceived - amount;
    }
}

class DigitalPayment extends Payment implements Payable {
    private String transactionID;

    public DigitalPayment(String paymentID, double amount, String transactionID) {
        super(paymentID, amount);
        if (transactionID == null || transactionID.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        this.transactionID = transactionID;
    }

    @Override
    public boolean processPayment(Order order) {
        if (transactionID.startsWith("TXN")) {
            order.getVendor().updateEarnings(amount);
            return true;
        }
        return false;
    }
}

class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

class FoodItem {
    private String itemID;
    private String name;
    private double price;
    private String category;

    public FoodItem(String itemID, String name, double price, String category) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getItemID() { return itemID; }
}

class Cart {
    private String cartID;
    private Customer customer;
    private Map<FoodItem, Integer> items;

    public Cart(String cartID, Customer customer) {
        this.cartID = cartID;
        this.customer = customer;
        this.items = new HashMap<>();
    }

    public void addItem(FoodItem item) {
        items.put(item, items.getOrDefault(item, 0) + 1);
    }

    public void removeItem(FoodItem item) {
        if (items.containsKey(item)) {
            int quantity = items.get(item);
            if (quantity > 1) {
                items.put(item, quantity - 1);
            } else {
                items.remove(item);
            }
        }
    }

    public void removeAll(FoodItem item) {
        items.remove(item);
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double calculateTotal() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    public Map<FoodItem, Integer> getItems() { return items; }
}

class Order {
    private static int orderCounter = 1;
    private String orderID;
    private Customer customer;
    private Vendor vendor;
    private Map<FoodItem, Integer> items;
    private String status;
    private JLabel statusLabel;
    private long cookingTime = 5000;
    private Timer timer;

    public Order(Customer customer, Vendor vendor, Map<FoodItem, Integer> items, JLabel statusLabel) {
        this.orderID = "ORD-" + orderCounter++;
        this.customer = customer;
        this.vendor = vendor;
        this.items = new HashMap<>(items);
        this.status = "Pending";
        this.statusLabel = statusLabel;
        this.timer = new Timer();
        startCookingTimer();
    }

    private void startCookingTimer() {
        long startTime = System.currentTimeMillis();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = cookingTime - elapsed;
                if (remaining <= 0) {
                    status = "Completed";
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Order " + orderID + ": " + status);
                        JOptionPane.showMessageDialog(null, "Order " + orderID + " is ready!", "Order Ready", JOptionPane.INFORMATION_MESSAGE);
                    });
                    timer.cancel();
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Order " + orderID + ": " + status + " (Time remaining: " + (remaining / 1000) + "s)");
                    });
                }
            }
        }, 0, 1000);
    }

    public String getOrderID() { return orderID; }
    public String getStatus() { return status; }
    public Map<FoodItem, Integer> getItems() { return items; }
    public Vendor getVendor() { return vendor; }
    public Customer getCustomer() { return customer; }
}

class Feedback {
    private String feedbackID;
    private Customer customer;
    private Order order;
    private int rating;
    private String comments;

    public Feedback(String feedbackID, Customer customer, Order order, int rating, String comments) {
        this.feedbackID = feedbackID;
        this.customer = customer;
        this.order = order;
        this.rating = rating;
        this.comments = comments;
    }

    public int getRating() { return rating; }
    public String getComments() { return comments; }
    public Order getOrder() { return order; }

    public void saveToFile() {
        try (FileWriter writer = new FileWriter("feedback.txt", true)) {
            writer.write("Feedback ID: " + feedbackID + "\n");
            writer.write("Order ID: " + order.getOrderID() + "\n");
            writer.write("Rating: " + rating + "\n");
            writer.write("Comments: " + comments + "\n");
            writer.write("------------------------\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving feedback: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class Customer {
    private String customerID;
    private String name;
    private List<Order> orderHistory;
    private List<Feedback> feedbackList;

    public Customer(String customerID, String name) {
        this.customerID = customerID;
        this.name = name;
        this.orderHistory = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
    }

    public void placeOrder(Order order) {
        orderHistory.add(order);
    }

    public void submitFeedback(Feedback feedback) {
        feedbackList.add(feedback);
    }

    public List<Order> getOrderHistory() { return orderHistory; }
    public List<Feedback> getFeedbackList() { return feedbackList; }
    public String getCustomerID() { return customerID; }
    public String getName() { return name; }
}

class Vendor {
    private String vendorID;
    private String name;
    private Menu menu;
    private double earnings;
    private List<Feedback> feedbackList;

    public Vendor(String vendorID, String name) {
        this.vendorID = vendorID;
        this.name = name;
        this.menu = new Menu();
        this.earnings = 0.0;
        this.feedbackList = new ArrayList<>();
    }

    public void addFoodItem(FoodItem item) {
        menu.addItem(item);
    }

    public Menu getMenu() { return menu; }
    public String getName() { return name; }
    public String getVendorID() { return vendorID; }

    public void updateEarnings(double amount) {
        earnings += amount;
    }

    public void addFeedback(Feedback feedback) {
        feedbackList.add(feedback);
    }
}

class GUIHelper {
    public static void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
}

public class CanteenManagementSystem {
    private JFrame frame;
    private Vendor vendor;
    private Customer customer;
    private Cart cart;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel grandTotalLabel;
    private JTextField searchField;
    private JLabel orderStatusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CanteenManagementSystem().createAndShowGUI());
    }

    public CanteenManagementSystem() {
        this.vendor = new Vendor("V001", "Abhyasi Cafe");
        this.customer = new Customer("C001", "John Doe");
        this.cart = new Cart("Cart001", customer);

        vendor.getMenu().addItem(new FoodItem("F001", "Veg Momo", 12.5, "Snacks"));
        vendor.getMenu().addItem(new FoodItem("F002", "Burger", 15.0, "Snacks"));
        vendor.getMenu().addItem(new FoodItem("F003", "Cold Coffee", 10.0, "Beverages"));
    }

    private void createAndShowGUI() {
        frame = new JFrame("Canteen Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        // Menu Panel
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Menu", 0, 0, labelFont));

        searchField = new JTextField(15);
        searchField.setFont(buttonFont);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterMenu(); }
            public void removeUpdate(DocumentEvent e) { filterMenu(); }
            public void changedUpdate(DocumentEvent e) { filterMenu(); }
        });
        menuPanel.add(searchField, BorderLayout.NORTH);

        JPanel menuItemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        menuItemsPanel.setOpaque(false);
        updateMenuItems(menuItemsPanel);
        menuPanel.add(new JScrollPane(menuItemsPanel), BorderLayout.CENTER);

        // Cart Panel
        cartModel = new DefaultTableModel(new Object[]{"Item", "Quantity", "Price", "Total", "Actions"}, 0);
        cartTable = new JTable(cartModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        cartTable.setRowHeight(35);
        cartTable.setFont(buttonFont);
        cartTable.getTableHeader().setFont(labelFont);
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setOpaque(false);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Cart", 0, 0, labelFont));

        grandTotalLabel = new JLabel("Grand Total: ¥0.00");
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        grandTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        orderStatusLabel = new JLabel("No orders yet.");
        orderStatusLabel.setFont(buttonFont);

        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setFont(buttonFont);
        checkoutBtn.setBackground(new Color(255, 165, 0));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> handlePayment());

        JPanel checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setOpaque(false);
        checkoutPanel.add(orderStatusLabel, BorderLayout.WEST);
        checkoutPanel.add(grandTotalLabel, BorderLayout.CENTER);
        checkoutPanel.add(checkoutBtn, BorderLayout.EAST);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setOpaque(false);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setOpaque(false);

        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.setFont(buttonFont);
        clearCartBtn.setBackground(new Color(255, 99, 71));
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.addActionListener(e -> {
            cart.clearCart();
            updateCartTable();
            GUIHelper.showMessage("Cart Cleared", "All items removed from cart.");
        });

        JButton viewHistoryBtn = new JButton("View History");
        viewHistoryBtn.setFont(buttonFont);
        viewHistoryBtn.setBackground(new Color(255, 215, 0));
        viewHistoryBtn.addActionListener(e -> viewOrderHistoryWithFeedback());

        controlPanel.add(clearCartBtn);
        controlPanel.add(viewHistoryBtn);

        // Assemble Main Frame
        frame.add(menuPanel, BorderLayout.WEST);
        frame.add(cartPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(900, 650);
        frame.setVisible(true);
        updateCartTable();
    }

    private void updateMenuItems(JPanel panel) {
        panel.removeAll();
        String searchText = searchField.getText().toLowerCase();
        for (FoodItem item : vendor.getMenu().getItems()) {
            if (searchText.isEmpty() || item.getName().toLowerCase().contains(searchText)) {
                JButton btn = new JButton(item.getName() + " - ¥" + item.getPrice());
                btn.setBackground(new Color(100, 149, 237));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    cart.addItem(item);
                    updateCartTable();
                });
                panel.add(btn);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void filterMenu() {
        updateMenuItems((JPanel) ((JScrollPane) frame.getContentPane().getComponent(0)).getViewport().getView());
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        for (Map.Entry<FoodItem, Integer> entry : cart.getItems().entrySet()) {
            FoodItem item = entry.getKey();
            int quantity = entry.getValue();
            double price = item.getPrice();
            double total = price * quantity;

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton plusBtn = new JButton("+");
            plusBtn.addActionListener(e -> {
                cart.addItem(item);
                updateCartTable();
            });
            
            JButton minusBtn = new JButton("-");
            minusBtn.addActionListener(e -> {
                cart.removeItem(item);
                updateCartTable();
            });

            JButton removeBtn = new JButton("X");
            removeBtn.addActionListener(e -> {
                cart.removeAll(item);
                updateCartTable();
            });

            buttonPanel.add(plusBtn);
            buttonPanel.add(minusBtn);
            buttonPanel.add(removeBtn);

            cartModel.addRow(new Object[]{
                item.getName(),
                quantity,
                String.format("¥%.2f", price),
                String.format("¥%.2f", total),
                buttonPanel
            });
        }
        grandTotalLabel.setText("Grand Total: " + String.format("¥%.2f", cart.calculateTotal()));
    }

    private void handlePayment() {
        if (cart.getItems().isEmpty()) {
            GUIHelper.showMessage("Error", "Cart is empty!", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = {"Cash", "Digital"};
        int choice = JOptionPane.showOptionDialog(
            frame,
            "Choose payment method:",
            "Payment",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        try {
            Payment payment;
            if (choice == 0) {
                String cashInput = JOptionPane.showInputDialog("Enter cash amount:");
                double cash = Double.parseDouble(cashInput);
                payment = new CashPayment("PAY-" + System.currentTimeMillis(), cart.calculateTotal(), cash);
                CashPayment cashPayment = (CashPayment) payment;
                Order newOrder = new Order(customer, vendor, cart.getItems(), orderStatusLabel);
                if (cashPayment.processPayment(newOrder)) {
                    GUIHelper.showMessage("Payment Success", 
                        "Payment processed!\nChange: ¥" + String.format("%.2f", cashPayment.calculateChange()));
                    customer.placeOrder(newOrder); // Add order to customer history
                    cart.clearCart();
                    updateCartTable();
                    promptForFeedback(newOrder); // Prompt for feedback after successful payment
                } else {
                    throw new PaymentException("Insufficient cash.");
                }
            } else if (choice == 1) {
                String txnID = JOptionPane.showInputDialog("Enter transaction ID (TXN...):");
                payment = new DigitalPayment("PAY-" + System.currentTimeMillis(), cart.calculateTotal(), txnID);
                Order newOrder = new Order(customer, vendor, cart.getItems(), orderStatusLabel);
                if (((Payable) payment).processPayment(newOrder)) {
                    GUIHelper.showMessage("Success", "Payment processed!");
                    customer.placeOrder(newOrder); // Add order to customer history
                    cart.clearCart();
                    updateCartTable();
                    promptForFeedback(newOrder); // Prompt for feedback after successful payment
                } else {
                    throw new PaymentException("Invalid transaction ID.");
                }
            } else return;

        } catch (Exception e) {
            GUIHelper.showMessage("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void promptForFeedback(Order order) {
        int rating = -1;
        String comments = null;

        while (rating < 1 || rating > 5) {
            String ratingStr = JOptionPane.showInputDialog("Enter rating (1-5):");
            if (ratingStr == null) {
                GUIHelper.showMessage("Feedback Cancelled", "Feedback submission cancelled.");
                return;
            }
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    GUIHelper.showMessage("Invalid Rating", "Please enter a rating between 1 and 5.", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                GUIHelper.showMessage("Invalid Rating", "Please enter a valid number between 1 and 5.", JOptionPane.ERROR_MESSAGE);
                rating = -1;
            }
        }

        comments = JOptionPane.showInputDialog("Enter comments:");
        if (comments == null) {
            comments = "";
        }

        Feedback feedback = new Feedback("FB" + System.currentTimeMillis(), customer, order, rating, comments);
        customer.submitFeedback(feedback);
        vendor.addFeedback(feedback);
        feedback.saveToFile();
        GUIHelper.showMessage("Feedback Submitted", "Thank you for your feedback!");
    }

    private void viewOrderHistoryWithFeedback() {
        StringBuilder sb = new StringBuilder();
        for (Order order : customer.getOrderHistory()) {
            sb.append("Order ID: ").append(order.getOrderID()).append("\n");
            sb.append("Status: ").append(order.getStatus()).append("\n");
            sb.append("Items:\n");
            for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
                sb.append(" - ").append(entry.getKey().getName()).append(" x ").append(entry.getValue()).append("\n");
            }

            // Find feedback for this order
            Feedback matchingFeedback = null;
            for (Feedback feedback : customer.getFeedbackList()) {
                if (feedback.getOrder().getOrderID().equals(order.getOrderID())) {
                    matchingFeedback = feedback;
                    break;
                }
            }

            if (matchingFeedback != null) {
                sb.append("Rating: ").append(matchingFeedback.getRating()).append("/5\n");
                sb.append("Comments: ").append(matchingFeedback.getComments()).append("\n");
            } else {
                sb.append("No feedback for this order.\n");
            }

            sb.append("\n");
        }

        if (sb.toString().trim().isEmpty()) {
            sb.append("No order history found.");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JFrame historyFrame = new JFrame("Order History");
        historyFrame.add(scrollPane);
        historyFrame.setSize(400, 300);
        historyFrame.setVisible(true);
    }

    class ButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JPanel) {
                return (JPanel) value;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof JPanel) {
                panel = (JPanel) value;
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel;
        }
    }
}