import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CoordinatesDialog extends Alert {
    private TextField xCoordinate = new TextField();
    private TextField yCoordinate = new TextField();

    public CoordinatesDialog(){
        super(Alert.AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("x:"), xCoordinate);
        grid.addRow(1, new Label("y:"), yCoordinate);
        getDialogPane().setContent(grid);
        setHeaderText(null);
    }

    public int getXCoordinate(){
        return Integer.parseInt(xCoordinate.getText());
    }

    public int getYCoordinate(){
        return Integer.parseInt(yCoordinate.getText());
    }
}
