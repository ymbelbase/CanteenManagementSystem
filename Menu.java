import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<FoodItem> items;

    public Menu() {
        this.items = new ArrayList<>();
    }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public void removeItem(FoodItem item) {
        items.remove(item);
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public FoodItem findItemByName(String name) {
        for (FoodItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}