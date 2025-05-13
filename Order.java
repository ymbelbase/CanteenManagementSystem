import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Order {
    private static int orderCounter = 1;
    private String orderID;
    private Customer customer;
    private Vendor vendor;
    private Map<FoodItem, Integer> items;
    private String status;
    private LocalDateTime orderTime;
    private static final long PREPARATION_TIME = 5000;

    public Order(Customer customer, Vendor vendor, Map<FoodItem, Integer> items) {
        this.orderID = "ORD-" + orderCounter++;
        this.customer = customer;
        this.vendor = vendor;
        this.items = new HashMap<>(items);
        this.status = "Pending";
        this.orderTime = LocalDateTime.now();
        startPreparation();
    }

    private void startPreparation() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                updateStatus("Preparing");
            }
        }, PREPARATION_TIME / 2);
        timer.schedule(new TimerTask() {
            public void run() {
                updateStatus("Ready");
            }
        }, PREPARATION_TIME);
    }

    public String getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void cancelOrder() {
        if (!status.equals("Ready")) {
            updateStatus("Cancelled");
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", customer=" + customer.getName() +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }
}