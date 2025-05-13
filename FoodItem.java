public class FoodItem {
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

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}