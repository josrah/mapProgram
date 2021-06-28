public class NamedPlace extends Place{

    public NamedPlace (Category category, Position position, String name){
        super(category,position,name);
    }

    public NamedPlace(Position position, String name) {
        this(null, position, name);
    }

}