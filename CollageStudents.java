package collegestudent.collagestudent;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javafx.concurrent.Task;

public class CollageStudents extends Application {

    // Create Components
    private TextField nameField;
    private TextField birthDateField;
    private TextField levelField;
    private CheckBox DataBasesCheckBox;
    private CheckBox QualityCheckBox;
    private CheckBox securityCheckBox;
    private TextArea displayArea;  // Using TextArea for reliable scrolling
    private ScrollPane scrollPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Information");
        // Set application icon
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Student.icon.png")));
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }
// Use a BorderPane as base pane
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
//  Use a VBOX as left pane for infoPanel
        VBox infoPanel = createInfoPanel();
        root.setCenter(infoPanel);
//  Use a VBOX as Right pane for infoPanel
        VBox detailsPanel = createDetailsPanel();
        root.setRight(detailsPanel);

        Scene scene = new Scene(root, 1000, 600);
//   Create a simple Thread to focus into the root pane
        Platform.runLater(() -> {
            root.requestFocus();
        });

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //  Create a methods for infoPanel
    private VBox createInfoPanel() {

//  Uses VBox layout with spacing and padding
        VBox panel = new VBox(15);
        panel.getStyleClass().add("info-panel");
        panel.setPadding(new Insets(20));

        Label title = new Label("Student Information");
        title.getStyleClass().add("panel-title");

        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Label birthLabel = new Label("Birth Date:");
        birthDateField = new TextField();
        birthDateField.setPromptText("YYYY-MM-DD");

        Label levelLabel = new Label("Level:");
        levelField = new TextField();
        levelField.setPromptText(" junior, senior, etc ");

        Label coursesLabel = new Label("Courses:");
        coursesLabel.getStyleClass().add("section-label");

        DataBasesCheckBox = new CheckBox("DataBases");
        QualityCheckBox = new CheckBox("SoftWareTesting&QualityAssurance");
        securityCheckBox = new CheckBox("Software Security");

        panel.getChildren().addAll(
                title,
                nameLabel, nameField,
                birthLabel, birthDateField,
                levelLabel, levelField,
                coursesLabel,
                DataBasesCheckBox, QualityCheckBox, securityCheckBox
        );

        return panel;
    }
    //  Create a methods for DetailsPanel
    private VBox createDetailsPanel() {

//        Uses VBox layout with spacing and padding
        VBox panel = new VBox(15);
        panel.getStyleClass().add("details-panel");
        panel.setPadding(new Insets(20));

        Label title = new Label("Details");
        title.getStyleClass().add("panel-title");

        // TextArea with proper scrolling setup
        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setWrapText(true);
        displayArea.getStyleClass().add("display-area");
        displayArea.setPrefHeight(Region.USE_COMPUTED_SIZE);

        // ScrollPane with guaranteed scrolling
        scrollPane = new ScrollPane(displayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show scrollbar
        scrollPane.getStyleClass().add("transparent-scroll-pane");
        scrollPane.setPrefViewportHeight(400);

        Button displayButton = new Button("Display Information");
        displayButton.getStyleClass().add("display-button");
        displayButton.setMaxWidth(Double.MAX_VALUE);
        displayButton.setOnAction(e -> displayStudentInfo());

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        panel.getChildren().addAll(title, scrollPane, displayButton);
        return panel;
    }

    //  Create a methods for displayStudentInfo
    private void displayStudentInfo() {
        Task<String> processTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return processStudentInfo();
            }
        };

        processTask.setOnSucceeded(event -> {
            displayArea.setText(processTask.getValue());
            Platform.runLater(() -> {
                displayArea.positionCaret(0); // Reset to top
                scrollPane.setVvalue(0);      // Ensure scroll at top
            });
        });

        processTask.setOnFailed(event -> {
            displayArea.setText("Error: " + processTask.getException().getMessage());
        });

        new Thread(processTask).start();
//  ************ On success, displays the result and resets scroll position **************
//  ************ On failure, shows the error message ************************
//  ************ Runs the task in a separate thread *************************
    }

    //  Create a methods for ProcessStudentInfo
    private String processStudentInfo() throws Exception {
        if (nameField.getText().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        try {
            LocalDate.parse(birthDateField.getText());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }

        if (levelField.getText().isEmpty()) {
            throw new IllegalArgumentException("Level cannot be empty");
        }

        double cost = 0;
        StringBuilder courses = new StringBuilder();

        if (DataBasesCheckBox.isSelected()) {
            cost += 600;
            courses.append("• DataBases\n");
        }
        if (QualityCheckBox.isSelected()) {
            cost += 900;
            courses.append("• SoftWareTesting&QualityAssurance\n");
        }
        if (securityCheckBox.isSelected()) {
            cost += 1200;
            courses.append("• Software Security\n");
        }

        if (courses.length() == 0) {
            throw new IllegalArgumentException("Please select at least one course");
        }

        return String.format(
                "STUDENT INFORMATION SUMMARY\n\n" +
                        "Name: %s\n\n" +
                        "Birth Date: %s\n\n" +
                        "Level: %s\n\n" +
                        "REGISTERED COURSES:\n%s\n" +
                        "COURSE FEES DETAILS:\n" +
                        "• DataBases: %s\n" +
                        "• SoftWareTesting&QualityAssurance: %s\n" +
                        "• Software Security: %s\n\n" +
                        "TOTAL FEES: %.2f SAR\n\n" +
                        "Payment due in 14 days from registration",
                nameField.getText(),
                birthDateField.getText(),
                levelField.getText(),
                courses.toString(),
                (DataBasesCheckBox.isSelected() ? "600 SAR" : "Not selected"),
                (QualityCheckBox.isSelected() ? "900 SAR" : "Not selected"),
                (securityCheckBox.isSelected() ? "1200 SAR" : "Not selected"),
                cost
        );
    }
    //********** Validates all input fields **********
    //********** Calculates total course fees based on selected checkboxes **********
    //********** Builds a formatted summary string with: 1- Student information, 2- Selected courses,>>>>>>
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 3- Fee details, 4- Total cost ********************
    //********** Throws exceptions for invalid input **********
    //********** Returns the formatted summary string **********
}