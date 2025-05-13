import java.util.ArrayList;
import java.util.List;

public class Customer {
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

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void submitFeedback(Feedback feedback) {
        feedbackList.add(feedback);
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public String getName() {
        return name;
    }
}