import java.time.LocalDateTime;

public class Feedback {
    private String feedbackID;
    private Customer customer;
    private Order order;
    private int rating;
    private String comments;
    private LocalDateTime feedbackTime;

    public Feedback(String feedbackID, Customer customer, Order order, int rating, String comments) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.feedbackID = feedbackID;
        this.customer = customer;
        this.order = order;
        this.rating = rating;
        this.comments = comments;
        this.feedbackTime = LocalDateTime.now();
    }

    public Order getOrder() {
        return order;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getFeedbackTime() {
        return feedbackTime;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackID='" + feedbackID + '\'' +
                ", customer=" + customer.getName() +
                ", order=" + order.getOrderID() +
                ", rating=" + rating +
                ", comments='" + comments + '\'' +
                ", feedbackTime=" + feedbackTime +
                '}';
    }
}