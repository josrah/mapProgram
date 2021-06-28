import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class NamedPlaceDialog extends Alert {
    private TextField nameField = new TextField();

    public NamedPlaceDialog(){
        super(Alert.AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name:"), nameField);
        getDialogPane().setContent(grid);
        setHeaderText(null);
    }

    public String getName(){
        return nameField.getText();
    }

}
