import java.util.ArrayList;
import java.util.List;

public class Vendor {
    private String vendorID;
    private String canteenName;
    private Menu menu;
    private List<Feedback> feedbackList;
    private double earnings;

    public Vendor(String vendorID, String canteenName) {
        this.vendorID = vendorID;
        this.canteenName = canteenName;
        this.menu = new Menu();
        this.feedbackList = new ArrayList<>();
        this.earnings = 0.0;
    }

    public void addFoodItem(FoodItem item) {
        menu.addItem(item);
    }

    public Menu getMenu() {
        return menu;
    }

    public void addFeedback(Feedback feedback) {
        feedbackList.add(feedback);
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void updateEarnings(double amount) {
        this.earnings += amount;
    }

    public double getEarnings() {
        return earnings;
    }
}