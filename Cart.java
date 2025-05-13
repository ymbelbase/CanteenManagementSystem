import java.util.HashMap;
import java.util.Map;

public class Cart {
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

    public void updateItemQuantity(FoodItem item, int quantity) {
        if (quantity <= 0) {
            items.remove(item);
        } else {
            items.put(item, quantity);
        }
    }

    public Map<FoodItem, Integer> getItems() {
        return items;
    }

    public int getItemQuantity(FoodItem item) {
        return items.getOrDefault(item, 0);
    }

    public double calculateTotal() {
        double total = 0.0;
        for (Map.Entry<FoodItem, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}