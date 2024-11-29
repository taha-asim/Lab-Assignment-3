
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label fullNameLabel = new Label("Full Name:");
        TextField fullNameField = new TextField();

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();

        Label genderLabel = new Label("Gender:");
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("Male");
        maleRadio.setToggleGroup(genderGroup);
        RadioButton femaleRadio = new RadioButton("Female");
        femaleRadio.setToggleGroup(genderGroup);
        HBox genderBox = new HBox(10, maleRadio, femaleRadio);
        genderBox.setAlignment(Pos.CENTER_LEFT);

        Label provinceLabel = new Label("Home Province:");
        ComboBox<String> provinceComboBox = new ComboBox<>();
        provinceComboBox.getItems().addAll("Punjab", "Sindh", "Balochistan", "kpk", "AJK");
        provinceComboBox.setPromptText("Select Province");

        Label dobLabel = new Label("DOB:");
        DatePicker dobPicker = new DatePicker();

        Button newRecordButton = new Button("New");
        Button deleteButton = new Button("Delete");
        Button restoreButton = new Button("Restore");
        Button findPrevButton = new Button("Find Prev");
        Button findNextButton = new Button("Find Next");
        Button criteriaButton = new Button("Criteria");
        Button closeButton = new Button("Close");

        newRecordButton.setOnAction(e -> handleNewRecord(fullNameField, idField, genderGroup, provinceComboBox, dobPicker));

        findPrevButton.setOnAction(e -> openFindRecordWindow());

        closeButton.setOnAction(e -> primaryStage.close());

        GridPane formGrid = createFormGrid(fullNameLabel, fullNameField, idLabel, idField, genderLabel, genderBox, provinceLabel, provinceComboBox, dobLabel, dobPicker);

        VBox buttonBox = createButtonBox(newRecordButton, deleteButton, restoreButton, findPrevButton, findNextButton, criteriaButton, closeButton);

        HBox mainLayout = new HBox(20, formGrid, buttonBox);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 450, 300);
        primaryStage.setTitle("Form ");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleNewRecord(TextField fullNameField, TextField idField, ToggleGroup genderGroup, ComboBox<String> provinceComboBox, DatePicker dobPicker) {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String gender = ((RadioButton) genderGroup.getSelectedToggle()) != null ? ((RadioButton) genderGroup.getSelectedToggle()).getText() : null;
        String province = provinceComboBox.getValue();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        if (fullName.isEmpty() || id.isEmpty() || gender == null || province == null || dob.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields before saving.");
            return;
        }

        File file = new File("Data.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("ID: " + id + "\n");
            writer.write("Full Name: " + fullName + "\n");
            writer.write("Gender: " + gender + "\n");
            writer.write("Home Province: " + province + "\n");
            writer.write("DOB: " + dob + "\n");
            writer.write("---------------\n");
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error writing to file: " + ex.getMessage());
        }
    }

    private void openFindRecordWindow() {
        Stage findStage = new Stage();
        findStage.setTitle("Find Record by ID");

        Label findIdLabel = new Label("Enter ID:");
        TextField findIdField = new TextField();
        Button searchButton = new Button("Search");
        Label resultLabel = new Label();

        searchButton.setOnAction(event -> {
            String searchId = findIdField.getText();
            File file = new File("Data.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    boolean recordFound = false;
                    StringBuilder result = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("ID: " + searchId)) {
                            recordFound = true;
                            result.append("Record found:\n").append(line).append("\n");
                            for (int i = 0; i < 4; i++) {
                                line = reader.readLine();
                                result.append(line).append("\n");
                            }
                            result.append("---------------\n");
                            break;
                        }
                    }

                    resultLabel.setText(recordFound ? result.toString() : "No record found for ID: " + searchId);
                } catch (IOException ex) {
                    resultLabel.setText("Error reading file: " + ex.getMessage());
                }
            }
        });

        VBox findLayout = new VBox(10, findIdLabel, findIdField, searchButton, resultLabel);
        findLayout.setPadding(new Insets(10));
        findStage.setScene(new Scene(findLayout, 400, 200));
        findStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message);
        alert.setTitle(title);
        alert.show();
    }

    private GridPane createFormGrid(Label fullNameLabel, TextField fullNameField, Label idLabel, TextField idField,
                                    Label genderLabel, HBox genderBox, Label provinceLabel, ComboBox<String> provinceComboBox,
                                    Label dobLabel, DatePicker dobPicker) {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        formGrid.add(fullNameLabel, 0, 0);
        formGrid.add(fullNameField, 1, 0);
        formGrid.add(idLabel, 0, 1);
        formGrid.add(idField, 1, 1);
        formGrid.add(genderLabel, 0, 2);
        formGrid.add(genderBox, 1, 2);
        formGrid.add(provinceLabel, 0, 3);
        formGrid.add(provinceComboBox, 1, 3);
        formGrid.add(dobLabel, 0, 4);
        formGrid.add(dobPicker, 1, 4);
        return formGrid;
    }

    private VBox createButtonBox(Button newRecordButton, Button deleteButton, Button restoreButton,
                                 Button findPrevButton, Button findNextButton, Button criteriaButton, Button closeButton) {
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(newRecordButton, deleteButton, restoreButton, findPrevButton, findNextButton, criteriaButton, closeButton);
        return buttonBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
