public class DescribedPlace extends Place {
    private String description;

    public DescribedPlace(Category category, Position position, String name, String description) {
        super(category, position, name);
        this.description = description;
    }

    public DescribedPlace(Position position, String name, String description) {
        this(null, position, name, description);
    }

    public String getDescription(){
        return description;
    }

    @Override
    public String toString(){
        return super.toString() + "," + description;
    }

}