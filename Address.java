package com.example.addressbook; //package declaration for Address class
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Address extends Application { //main class representing Address Book application, extending the JavaFx Application class

    private static final String FILE_PATH = "contact.dat"; //file path to save/read contact information

    //declaration for various contact information
    private TextField firstNameTextField, lastNameTextField, spouseTextField, addressTextField, cityTextField;
    private RadioButton maleRadioButton, femaleRadioButton;
    private TextField stateTextField, zipCodeTextField, phone1TextField, phone2TextField;
    private ComboBox addressTypeComboBox, phone1TypeComboBox, phone2TypeComboBox;
    private ImageView contactImageView;
    private TextArea memoTextArea;
    private static final double IMAGE_WIDTH = 100, IMAGE_HEIGHT = 100;
    private String homeAddress, workAddress;


    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Address Book");

        //create form controls
        firstNameTextField = createTextField();
        lastNameTextField = createTextField();
        spouseTextField = createTextField();
        maleRadioButton = new RadioButton("Male");
        femaleRadioButton = new RadioButton("Female");
        setupGenderRadioButtons();
        addressTextField = createTextField();
        cityTextField = createTextField();
        stateTextField = createTextField();
        zipCodeTextField = createTextField();
        addressTypeComboBox = new ComboBox<>();
        addressTypeComboBox.getItems().addAll("Home", "Work");
        phone1TextField = createTextField();
        phone2TextField = createTextField();
        phone1TypeComboBox = new ComboBox<>();
        phone1TypeComboBox.getItems().addAll("Home", "Work", "Mobile", "Other");
        phone2TypeComboBox = new ComboBox<>();
        phone2TypeComboBox.getItems().addAll("Home", "Work", "Mobile", "Other");
        contactImageView = new ImageView();
        memoTextArea = new TextArea();
        memoTextArea.setPrefRowCount(5);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        Button loadImageButton = new Button("Load Image");
        loadImageButton.setOnAction(e -> loadImage());

        //set default values
        addressTypeComboBox.getSelectionModel().selectFirst();
        phone1TypeComboBox.getSelectionModel().selectFirst();
        phone2TypeComboBox.getSelectionModel().selectFirst();

        //set event handlers
        addressTypeComboBox.setOnAction(e -> {
            String selectedAddressType = (String) addressTypeComboBox.getValue();


            if ("Work".equals(selectedAddressType)) {
                homeAddress = addressTextField.getText();
                saveAddressToFile();
                addressTextField.setText(workAddress);
            } else if ("Home".equals(selectedAddressType)) {
                workAddress = addressTextField.getText();
                saveAddressToFile();
                addressTextField.setText(homeAddress);
            }
        });

        saveButton.setOnAction(event -> saveContactToFile());
        cancelButton.setOnAction(event -> clearFields());

        //create form layout using GridPane to structure layout of controls in rows and columns
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.add(loadImageButton, 0, 14);

        contactImageView.setFitHeight(IMAGE_HEIGHT);
        contactImageView.setFitWidth(IMAGE_WIDTH);
        gridPane.add(contactImageView, 1, 14, 1, 1);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setMinWidth(200); //set a fixed width for text fields
        gridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.SOMETIMES);
        gridPane.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints,
                rowConstraints, rowConstraints, rowConstraints, rowConstraints, rowConstraints,
                rowConstraints, rowConstraints, rowConstraints);

        Label firstNameLabel = new Label("First Name:");
        Label lastNameLabel = new Label("Last Name:");
        gridPane.add(firstNameLabel, 1, 0);
        gridPane.add(lastNameLabel, 2, 0);
        gridPane.add(firstNameTextField, 1, 1);
        gridPane.add(lastNameTextField, 2, 1);

        gridPane.add(new Label("Spouse:"), 1, 2);
        gridPane.add(spouseTextField, 1, 3);

        gridPane.add(new Label("Gender:"), 1, 4);
        gridPane.add(maleRadioButton, 1, 5);
        gridPane.add(femaleRadioButton, 2, 5);

        gridPane.add(new Label("Address:"), 1, 6);
        gridPane.add(addressTypeComboBox, 0, 7, 1, 1);
        gridPane.add(addressTextField, 1, 7, 1, 1);

        gridPane.add(new Label("City:"), 1, 8);
        gridPane.add(cityTextField, 1, 9);

        gridPane.add(new Label("State:"), 2, 8);
        gridPane.add(stateTextField, 2, 9);

        gridPane.add(new Label("Zip Code:"), 3, 8);
        gridPane.add(zipCodeTextField, 3, 9);

        gridPane.add(new Label("Phone 1:"), 1, 10);
        gridPane.add(phone1TypeComboBox, 0, 11, 1, 1);
        gridPane.add(phone1TextField, 1, 11, 1, 1);

        gridPane.add(new Label("Phone 2:"), 1, 12);
        gridPane.add(phone2TypeComboBox, 0, 13, 1, 1);
        gridPane.add(phone2TextField, 1, 13, 1, 1);

        gridPane.add(new Label("Memo:"), 1, 16);
        gridPane.add(memoTextArea, 1, 17);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        gridPane.add(buttonBox, 0, 22, 3, 1);

        //save and load default address values and contact information from file
        saveAddressToFile();
        loadContactFromFile();

        //set up primary stage with the scene
        primaryStage.setScene(new Scene(gridPane, 900, 800));
        primaryStage.show();
    }

    //method to create text field with a fixed width
    private TextField createTextField() {
        TextField textField = new TextField();
        textField.setMinWidth(200); //set a fixed width for text fields
        return textField;
    }

    //event handler for loading an image using FileChooser
    private void loadImage() {
        FileChooser fileChooser = new FileChooser(); //allows user to select image file and displays using ImageView
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if ( selectedFile != null )
            try{
                Image image = new Image(selectedFile.toURI().toString());
                contactImageView.setImage(image);
            } catch (Exception e){
                e.printStackTrace();
            }
    }
    //set up gender radio buttons in a ToggleGroup
    private void setupGenderRadioButtons() {
        ToggleGroup genderToggleGroup = new ToggleGroup(); //ToggleGroup ensures only one radio button selected at a time
        maleRadioButton.setToggleGroup(genderToggleGroup);
        femaleRadioButton.setToggleGroup(genderToggleGroup);
    }

    //save home and work addresses to the file
    private void saveAddressToFile() {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(FILE_PATH, true))) {
            //uses DataOutputStream to write home and work addresses to the file
            if (homeAddress != null) {
                dataOutputStream.writeUTF("Home");
                dataOutputStream.writeUTF(homeAddress);
            }
            if (workAddress != null) {
                dataOutputStream.writeUTF("Work");
                dataOutputStream.writeUTF(workAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //save rest of contact information to the file
    private void saveContactToFile() {
        //uses DataOutputStream to write contact information to file
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(FILE_PATH, false))){;
            dataOutputStream.writeUTF("First Name");
            dataOutputStream.writeUTF(firstNameTextField.getText());
            dataOutputStream.writeUTF("Last Name");
            dataOutputStream.writeUTF(lastNameTextField.getText());
            dataOutputStream.writeUTF("Spouse");
            dataOutputStream.writeUTF(spouseTextField.getText());
            dataOutputStream.writeUTF("Gender");
            dataOutputStream.writeUTF(maleRadioButton.isSelected() ? "Male" : "Female");

            String addressType = (String) addressTypeComboBox.getValue();

            if ("Home".equals(addressType)) {
                dataOutputStream.writeUTF("Home");
                dataOutputStream.writeUTF(homeAddress);
                if (workAddress != null) {
                    dataOutputStream.writeUTF("Work");
                    dataOutputStream.writeUTF(workAddress);
                }
            } else if ("Work".equals(addressType)) {
                if (homeAddress != null) {
                    dataOutputStream.writeUTF("Home");
                    dataOutputStream.writeUTF(homeAddress);
                }
                dataOutputStream.writeUTF("Work");
                dataOutputStream.writeUTF(workAddress);
            }

            dataOutputStream.writeUTF("City");
            dataOutputStream.writeUTF(cityTextField.getText());
            dataOutputStream.writeUTF("State");
            dataOutputStream.writeUTF(stateTextField.getText());
            dataOutputStream.writeUTF("Zip Code");
            dataOutputStream.writeUTF(zipCodeTextField.getText());
            dataOutputStream.writeUTF("Phone 1");
            dataOutputStream.writeUTF(phone1TextField.getText());
            dataOutputStream.writeUTF("Phone 2");
            dataOutputStream.writeUTF(phone2TextField.getText());
            dataOutputStream.writeUTF("Phone 1 Type");
            dataOutputStream.writeUTF(phone1TypeComboBox.getValue().toString());
            dataOutputStream.writeUTF("Phone 2 Type");
            dataOutputStream.writeUTF(phone2TypeComboBox.getValue().toString());
            dataOutputStream.writeUTF("Memo");
            dataOutputStream.writeUTF(memoTextArea.getText());

            System.out.println("Data has been written to " + FILE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //load contact information from the file
    private void loadContactFromFile() {
        if (Files.exists(Paths.get(FILE_PATH))) {
            try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(FILE_PATH))) {
                //uses DataInputStream to read and populate contact information from file
                String line;
                homeAddress = null;
                workAddress = null;

                while (dataInputStream.available() > 0) {
                    String field = dataInputStream.readUTF();
                    String value = dataInputStream.readUTF();

                    switch (field) {
                        case "First Name":
                            firstNameTextField.setText(value);
                            break;
                        case "Last Name":
                            lastNameTextField.setText(value);
                            break;
                        case "Spouse":
                            spouseTextField.setText(value);
                            break;
                        case "Gender":
                            if (value.equals("Male")) {
                                maleRadioButton.setSelected(true);
                            } else if (value.equals("Female")) {
                                femaleRadioButton.setSelected(true);
                            }
                            break;
                        case "Home Address":
                            homeAddress = value;
                            break;
                        case "Work Address":
                            workAddress = value;
                            break;
                        case "City":
                            cityTextField.setText(value);
                            break;
                        case "State":
                            stateTextField.setText(value);
                            break;
                        case "Zip Code":
                            zipCodeTextField.setText(value);
                            break;
                        case "Phone 1":
                            phone1TextField.setText(value);
                            break;
                        case "Phone 2":
                            phone2TextField.setText(value);
                            break;
                        case "Phone 1 Type":
                            phone1TypeComboBox.setValue(value);
                            break;
                        case "Phone 2 Type":
                            phone2TypeComboBox.setValue(value);
                            break;
                        case "Memo":
                            memoTextArea.setText(value);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //clears all input fields
    private void clearFields() {
        firstNameTextField.clear();
        lastNameTextField.clear();
        spouseTextField.clear();
        maleRadioButton.setSelected(false);
        femaleRadioButton.setSelected(false);
        addressTextField.clear();
        cityTextField.clear();
        stateTextField.clear();
        zipCodeTextField.clear();
        addressTypeComboBox.getSelectionModel().selectFirst();
        phone1TextField.clear();
        phone2TextField.clear();
        phone1TypeComboBox.getSelectionModel().selectFirst();
        phone2TypeComboBox.getSelectionModel().selectFirst();
        memoTextArea.clear();
    }

    public static void main(String[] args) { //main method to launch JavaFx application
        launch(args);
    }
}
