import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.awt.event.FocusListener;

abstract class Place extends Polygon {
    private Category category;
    private Position position;
    private String name;
    private boolean marked;

    public Place(Category category, Position position, String name){
        super(position.getX(), position.getY(), position.getX()-15, position.getY()-30, position.getX()+15, position.getY()-30);
        this.category = category;
        this.position = position;
        this.name = name;
        this.marked = false;
        setVisible();
    }

    public Place(Position position, String name) {
        this(null, position, name);
    }

    public Category getCategory(){
        return category;
    }

    public String getName(){
        return name;
    }

    public Position getPosition(){
        return position;
    }

    public boolean getMarked(){
        return marked;
    }

    public void mark(){
        setStroke(Color.YELLOW);
        setStrokeWidth(5);
        marked = true;
    }

    public void unMark(){
        setStroke(null);
        marked = false;
    }

    public void information(){
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setContentText(toString());
        info.setHeaderText("Information about place");
        info.showAndWait();
    }

    public void setVisible(){
        if (category != null) {
            setFill(category.getColor());
        } else {
            setFill(Color.BLACK);
        }
    }

    public void setInvisible(){
        setFill(Color.TRANSPARENT);
    }

    @Override
    public String toString(){
        return position + "," + name;
    }

}
