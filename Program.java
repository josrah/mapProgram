import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.*;


public class Program extends Application {
    private Stage stage;
    private ImageView imageView = new ImageView();
    private RadioButton namedButton = new RadioButton("Named");
    private RadioButton describedButton = new RadioButton("Described");
    private ListView<Category> categories = new ListView<>();
    private TextField textField = new TextField();
    private Pane center;
    private boolean changed = false;
    private Map<Position, Place> places = new HashMap<>(); //datastruktur som samlar platser per position
    private Map<Category, Set<Place>> byCategory = new HashMap<>(); //datastruktur som samlar alla platser för varje kategori
    private Map<String, Set<Place>> byName = new HashMap<>(); //datastruktur som samlar alla platser med samma namn
    private Set<Place> markedPlaces = new HashSet<>(); //datastruktur som samlar markerade platser

    @Override public void start(Stage stage){
        this.stage = stage;
        BorderPane root = new BorderPane();

        VBox top = new VBox();
        MenuBar menuBar = new MenuBar();
        top.getChildren().add(menuBar);
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);

        MenuItem loadItem = new MenuItem("Load Map");
        fileMenu.getItems().add(loadItem);
        loadItem.setOnAction(new LoadMapHandler());

        MenuItem loadPlaces = new MenuItem("Load Places");
        fileMenu.getItems().add(loadPlaces);
        loadPlaces.setOnAction(new LoadPlacesHandler());

        MenuItem saveItem = new MenuItem("Save");
        fileMenu.getItems().add(saveItem);
        saveItem.setOnAction(new SaveHandler());

        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);
        exitItem.setOnAction(new ExitHandler());

        FlowPane pane = new FlowPane();
        pane.setAlignment(Pos.CENTER);

        Button newButton = new Button("New");
        pane.getChildren().add(newButton);
        newButton.setOnAction(new NewHandler());

        VBox types = new VBox();
        types.getChildren().add(namedButton);
        types.getChildren().add(describedButton);
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(namedButton, describedButton);
        namedButton.setSelected(true);
        pane.getChildren().add(types);
        types.setPadding(new Insets(5));
        types.setSpacing(5);

        textField = new TextField("Search");
        pane.getChildren().add(textField);
        Button searchButton = new Button("Search");
        pane.getChildren().add(searchButton);
        searchButton.setOnAction(new SearchHandler());

        Button hideButton = new Button("Hide");
        pane.getChildren().add(hideButton);
        hideButton.setOnAction(new HideHandler());

        Button removeButton = new Button("Remove");
        pane.getChildren().add(removeButton);
        removeButton.setOnAction(new RemoveHandler());

        Button coordinatesButton = new Button("Coordinates");
        pane.getChildren().add(coordinatesButton);
        coordinatesButton.setOnAction(new CoordinatesHandler());

        pane.setPadding(new Insets(5));
        pane.setHgap(5);
        top.getChildren().add(pane);
        root.setTop(top);

        VBox right = new VBox();
        right.getChildren().add(new Label("Categories"));
        categories.setItems(FXCollections.observableArrayList(Category.values()));
        right.getChildren().add(categories);
        categories.setPrefSize(100,100);
        categories.getSelectionModel().selectedItemProperty().addListener(new ListHandler());

        Button hideCategoryButton = new Button("Hide Category");
        right.getChildren().add(hideCategoryButton);
        hideCategoryButton.setOnAction(new HideCategoryHandler());

        right.setPadding(new Insets(5));
        right.setSpacing(5);
        root.setRight(right);

        center = new Pane();
        center.getChildren().add(imageView);
        root.setCenter(center);

        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.setTitle("Josefin's mapProgram");
        stage.setOnCloseRequest(new ExitTopHandler());
        stage.show();
    }

    //Loads image as map (background image)
    class LoadMapHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            if (unsavedWarning(event)) return;
            removeAllPlaces();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Map File");
            File file = fileChooser.showOpenDialog(stage);
            if (file == null)
                return;
            String fileName = file.getAbsolutePath();
            Image image = new Image("file:" + fileName);
            imageView.setImage(image);
            stage.sizeToScene();
            changed = false;
        }


    }

    //Loads places on map
    class LoadPlacesHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event) {
            if (unsavedWarning(event)) return;
            removeAllPlaces();
            try {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(null);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();

                FileReader infile = new FileReader(fileName);
                BufferedReader in = new BufferedReader(infile);
                String line;
                while ((line = in.readLine()) != null) {
                    String[] tokens = line.split(",");
                    String type = tokens[0];
                    String categoryName = tokens[1];
                    Category category;
                    if (categoryName.equalsIgnoreCase("none") || categoryName.equalsIgnoreCase("null")){
                        category = null;
                    } else {
                        category = Category.valueOf(categoryName.toUpperCase());
                    }
                    int x = Integer.parseInt(tokens[2]);
                    int y = Integer.parseInt(tokens[3]);
                    Position position = new Position(x,y);
                    String name = tokens[4];
                    Place place;
                    if (type.equals("Named")){
                        if (category != null){
                            place = new NamedPlace(category, position, name);
                        } else {
                            place = new NamedPlace(position, name);
                        }
                    } else {
                        String description = tokens[5];
                        if (category != null){
                            place = new DescribedPlace(category, position, name, description);
                        } else {
                            place = new DescribedPlace(position, name, description);
                        }
                    }
                    addPlace(place);
                }
                in.close();
                infile.close();
                changed = false;
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File not found");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IOException!");
                alert.showAndWait();
            }
        }
    }

    //Saves places as a text file
    class SaveHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            try {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(null);
                if (file == null)
                    return;
                String fileName = file.getAbsolutePath();

                FileWriter outfile = new FileWriter(fileName);
                PrintWriter out = new PrintWriter(outfile);
                String type;
                for (Place p : places.values()){
                    if (p instanceof NamedPlace){
                        type = "Named";
                    }
                    else {
                        type = "Described";
                    }
                    out.println(type + "," + p.getCategory() + "," + p.toString());
                }
                out.close();
                outfile.close();
                changed = false;
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File not found");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "IOException!");
                alert.showAndWait();
            }
        }
    }

    //Exit on button
    class ExitHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    //Exit in right top corner
    class ExitTopHandler implements EventHandler<WindowEvent>{
        @Override public void handle(WindowEvent event){
            if (changed){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Unsaved changes, exit anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL)
                    event.consume();
            }
        }
    }

    class NewHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            center.setOnMouseClicked(new NewClickHandler());
            center.setCursor(Cursor.CROSSHAIR);
        }
    }

    //Search place by name
    class SearchHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event) {
            unMarkPlaces();
            String name = textField.getText();
            if (!byName.containsKey(name)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No place with searched name");
                alert.showAndWait();
            } else {
                Set<Place> places = byName.get(name);
                for (Place place : places){
                    place.setVisible();
                    place.mark();
                    markedPlaces.add(place);
                }
            }
        }
    }

    //Hides marked places
    class HideHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            for (Place place : markedPlaces){
                place.setInvisible();
                place.unMark();
            }
            markedPlaces.clear();
        }
    }

    //Removes marked places
    class RemoveHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            if (markedPlaces.isEmpty()){
                return;
            }
            for (Place place : markedPlaces) {
                place.unMark();
                place.setInvisible();
                place = null;
            }
            markedPlaces.clear();
            changed = true;
        }
    }

    //I den här dialogen bör det kontrolleras att de inmatade värdena är numeriska
    class CoordinatesHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event){
            //Eventuella platser som var markerade innan ska avmarkeras.
            unMarkPlaces();
            //Öppnar en lite dialogruta där användaren kan mata in koordinater.
            CoordinatesDialog coordinatesDialog = new CoordinatesDialog();
            Optional<ButtonType> answer = coordinatesDialog.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.OK) {
                int x = coordinatesDialog.getXCoordinate(); //kontrollera om numeriska
                int y = coordinatesDialog.getYCoordinate();
                Position position = new Position(x, y);
                //Om det finns en plats på dessa koordinater så ska platsen göras synlig
                //och markerad.
                if (places.containsKey(position)){
                    Place place = places.get(position);
                    place.mark();
                } else {
                    //Om det inte finns någon plats på dessa koordinater ska en dialogruta med
                    //meddelande visas
                    Alert alert = new Alert(Alert.AlertType.ERROR, "No place at coordinates");
                    alert.showAndWait();
                }
            }
        }
    }

    //Hide places by category
    class HideCategoryHandler implements EventHandler<ActionEvent>{
        @Override public void handle(ActionEvent event) {
            Category category = categories.getSelectionModel().getSelectedItem();
            Set<Place> places = byCategory.get(category);
            for (Place place : places){
                place.setInvisible();
                place.unMark();
                markedPlaces.remove(place);
            }
        }
    }

    //Create new place
    class NewClickHandler implements EventHandler<MouseEvent> {
        @Override public void handle(MouseEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Position position = new Position(x, y);
            if (places.containsKey(position)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Place already exists at location");
                alert.showAndWait();
                return;
            }
            Place place;
            String name;
            Category category = categories.getSelectionModel().getSelectedItem();
            if (namedButton.isSelected()){
                NamedPlaceDialog namedPlaceDialog = new NamedPlaceDialog();
                Optional<ButtonType> answer = namedPlaceDialog.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    name = namedPlaceDialog.getName();
                    if (name.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Textfield is empty");
                        alert.showAndWait();
                        return;
                    }
                    if (category != null){
                        place = new NamedPlace(category, position, name);
                    } else {
                        place = new NamedPlace(position, name);
                    }
                    addPlace(place);
                }
            } else {
                DescribedPlaceDialog describedPlaceDialog = new DescribedPlaceDialog();
                Optional<ButtonType> answer = describedPlaceDialog.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    name = describedPlaceDialog.getName();
                    String description = describedPlaceDialog.getDescription();
                    if (name.isEmpty() || description.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Textfield is empty");
                        alert.showAndWait();
                        return;
                    }
                    if (category != null){
                        place = new DescribedPlace(category, position, name, description);
                    } else {
                        place = new DescribedPlace(position, name, description);
                    }
                    addPlace(place);
                }
            }
            center.setOnMouseClicked(null);
            center.setCursor(Cursor.DEFAULT);

        }
    }

    //Sets places visible
    class ListHandler implements ChangeListener<Category> {
        @Override public void changed(ObservableValue obs, Category oldValue, Category newValue){
            if (byCategory.containsKey(newValue)){
                Set<Place> places = byCategory.get(newValue);
                for (Place place : places){
                    place.setVisible();
                }
            }
        }
    }

    //Adds place to data collections
    private void addPlace(Place place){
        places.put(place.getPosition(), place);
        addPlaceToCategory(place);
        addPlaceToName(place);
        center.getChildren().add(place);
        changed = true;
        place.setOnMouseClicked(p -> {
            if (p.getButton() == MouseButton.PRIMARY){
                if (place.getMarked()){
                    place.unMark();
                    markedPlaces.remove(place);
                } else if (!place.getMarked()){
                    place.mark();
                    markedPlaces.add(place);
                }
            } else if (p.getButton() == MouseButton.SECONDARY){
                place.information();
            }
        });
    }

    private void addPlaceToCategory(Place place){
        Set<Place> places = byCategory.computeIfAbsent(place.getCategory(), k -> new HashSet<>());
        places.add(place);
    }

    private void addPlaceToName(Place place){
        Set<Place> places = byName.computeIfAbsent(place.getName(), k -> new HashSet<>());
        places.add(place);
    }

    private void unMarkPlaces(){
        for (Place place : markedPlaces){
            place.unMark();
        }
        markedPlaces.clear();
    }

    private boolean unsavedWarning(ActionEvent event) {
        if (changed){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Unsaved changes, load anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL){
                event.consume();
                return true;
            }
        }
        return false;
    }

    private void removeAllPlaces(){
        for (Map.Entry<Position, Place> positionPlaceEntry : places.entrySet()) {
            Place place = (Place) ((Map.Entry) positionPlaceEntry).getValue();
            place.unMark();
            place.setInvisible();
            place = null;
        }
        markedPlaces.clear();
        byCategory.clear();
        byName.clear();
        places.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

}