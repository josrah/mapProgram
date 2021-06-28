import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DescribedPlaceDialog extends Alert {
    private TextField nameField = new TextField();
    private TextField descriptionField = new TextField();

    public DescribedPlaceDialog(){
        super(Alert.AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Description:"), descriptionField);
        getDialogPane().setContent(grid);
        setHeaderText(null);
    }

    public String getName(){
        return nameField.getText();
    }

    public String getDescription(){
        return descriptionField.getText();
    }
}
