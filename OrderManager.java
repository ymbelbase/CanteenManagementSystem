import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private List<Order> orders;

    public OrderManager() {
        this.orders = new ArrayList<>();
    }

    public void placeOrder(Order order) {
        orders.add(order);
    }

    public Order findOrder(String orderID) {
        for (Order order : orders) {
            if (order.getOrderID().equals(orderID)) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Order> getOrdersByCustomer(Customer customer) {
        List<Order> customerOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCustomer().equals(customer)) {
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

    public List<Order> getOrdersByVendor(Vendor vendor) {
        List<Order> vendorOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getVendor().equals(vendor)) {
                vendorOrders.add(order);
            }
        }
        return vendorOrders;
    }
}