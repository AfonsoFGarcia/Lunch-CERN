package pt.afonsogarcia.lunchcern.domain;

public class DayMenuItem {
    private String type;
    private String description;
    private String price;

    public DayMenuItem(String type, String description, String price) {
        this.type = type;
        this.description = description;
        this.price = price.toUpperCase();
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }
}
