import javafx.scene.paint.Color;

public enum Category {
    BUS("Bus", Color.RED),
    TRAIN("Train", Color.GREEN),
    UNDERGROUND("Underground", Color.BLUE);

    private final String name;
    private final Color color;

    Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    private String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
