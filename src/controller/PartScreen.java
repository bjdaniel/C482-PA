package controller;

// JavaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.*;

// Java imports
import java.util.Optional;

// Project imports
import model.*;

/**
 * This is the controller for the part add/modify screen.
 */
public class PartScreen {
    // Class public member variables
    public static enum Function {ADD_PART, MODIFY_PART};

    // Class private member variables
    private static Function partScreenFunction;
    private static Part partScreenPart;

    // PartScreen controls
    @FXML
    private RadioButton inhouseRadioButton;
    @FXML
    private RadioButton outsourcedRadioButton;
    @FXML
    private Label actionPlaceholder;
    @FXML
    private Label machOrCompPlaceholder;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField maxField;
    @FXML
    private TextField machOrCompField;
    @FXML
    private TextField minField;

    /**
     * Initializes the scene components. This includes setting the label placeholder text and updating the screen
     * title based on the Function enum (ADD_PART or MODIFY_PART) set by the main screen controller. It also includes
     * setting the text of the part ID text field, and calling a method to fill the part details in the text fields
     * if the screen function is modifying a part.
     */
    public void initialize() {
        // Update window title and form label based on selected screen function
        if(partScreenFunction == Function.ADD_PART) {
            MainScreen.setChildScreenTitle("Add Part");
            actionPlaceholder.setText("Add Part");
            idField.setText("Auto Generated");
        }
        else if(partScreenFunction == Function.MODIFY_PART){
            MainScreen.setChildScreenTitle("Modify Part");
            actionPlaceholder.setText("Modify Part");
            fillPartDetails();
        }
    }

    /**
     * Fills the text fields with the appropriate Part object properties. If the part is an InHouse object then
     * the screen will display the Machine ID text field. If the part is an Outsourced object then the screen will
     * display the Company Name text field. The respective part or product radio button will be set to selected.
     */
    @FXML
    private void fillPartDetails() {
        idField.setText(Integer.toString(partScreenPart.getId()));
        nameField.setText(partScreenPart.getName());
        priceField.setText(Double.toString(partScreenPart.getPrice()));
        stockField.setText(Integer.toString(partScreenPart.getStock()));
        minField.setText(Integer.toString(partScreenPart.getMin()));
        maxField.setText(Integer.toString(partScreenPart.getMax()));

        if(partScreenPart instanceof InHouse) {
            machOrCompPlaceholder.setText("Machine ID");
            inhouseRadioButton.setSelected(true);
            machOrCompField.setText(Integer.toString(((InHouse) partScreenPart).getMachineId()));
        }
        else {
            machOrCompPlaceholder.setText("Company Name");
            outsourcedRadioButton.setSelected(true);
            machOrCompField.setText(((Outsourced) partScreenPart).getCompanyName());
        }
    }

    /**
     * Sets the function of the screen (ADD_PART or MODIFY_PART).
     *
     * @param function A Function enum describing the function of the screen. The enum constant can be either ADD_PART
     *                 or MODIFY_PART.
     */
    public static void setPartScreenFunction(Function function) {
        partScreenFunction = function;
    }

    /**
     * Updates the part screen controller's private static Part variable to reference the Part object to be modified.
     *
     * @param part The Part object reference passed to the controller.
     */
    public static void setPart(Part part) {
        partScreenPart = part;
    }

    /**
     * Handles when the user selects the Inhouse radio button and updates the Machine ID/Company Name label accordingly.
     */
    @FXML
    private void onInhouseAction() {
        machOrCompPlaceholder.setText("Machine ID");
    }

    /**
     * Handles when the user selects the Outsourced radio button and updates the Machine ID/Company Name label accordingly.
     */
    @FXML
    private void onOutsourcedAction() {
        machOrCompPlaceholder.setText("Company Name");
    }

    /**
     * Validates all fields against the constraints defined in the checkAllFields() method. If they pass, then the part
     * will be either added to inventory or updated depending on the screen function.
     */
    @FXML
    private void onSaveClicked() {
        // Validate all fields against their desired constraints
        if (checkAllFields()) {
            // Add Part object to Inventory
            if (partScreenFunction == Function.ADD_PART)
                // Add InHouse object to Inventory
                if (inhouseRadioButton.isSelected())
                    addPartToInventory(nameField.getText(),
                            Double.parseDouble(priceField.getText()),
                            Integer.parseInt(stockField.getText()),
                            Integer.parseInt(minField.getText()),
                            Integer.parseInt(maxField.getText()),
                            Integer.parseInt(machOrCompField.getText()));
                // Add Outsourced object to Inventory
                else
                    addPartToInventory(nameField.getText(),
                            Double.parseDouble(priceField.getText()),
                            Integer.parseInt(stockField.getText()),
                            Integer.parseInt(minField.getText()),
                            Integer.parseInt(maxField.getText()),
                            machOrCompField.getText());
            // Modify Part object in Inventory
            else if (partScreenFunction == Function.MODIFY_PART)
                // Update part as an InHouse object
                if (inhouseRadioButton.isSelected())
                   Inventory.updatePart(Inventory.getAllParts().indexOf(partScreenPart),
                                        new InHouse(partScreenPart.getId(),
                                                nameField.getText(),
                                                Double.parseDouble(priceField.getText()),
                                                Integer.parseInt(stockField.getText()),
                                                Integer.parseInt(minField.getText()),
                                                Integer.parseInt(maxField.getText()),
                                                Integer.parseInt(machOrCompField.getText())));
                else
                    Inventory.updatePart(Inventory.getAllParts().indexOf(partScreenPart),
                            new Outsourced(partScreenPart.getId(),
                                    nameField.getText(),
                                    Double.parseDouble(priceField.getText()),
                                    Integer.parseInt(stockField.getText()),
                                    Integer.parseInt(minField.getText()),
                                    Integer.parseInt(maxField.getText()),
                                    machOrCompField.getText()));
        MainScreen.closeChildScreen();
        }
    }

    /**
     * Handles the user clicking the CANCEL button. Ask for confirmation and if the user clicks YES then close the
     * screen without changing any Part object or the inventory.
     */
    @FXML
    private void onCancelClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MainScreen.getChildScreenTitle());
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Any changes will be lost.\nAre you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        // If user clicks yes then go ahead and close the screen
        if(result.isPresent() && result.get() == ButtonType.YES)
            MainScreen.closeChildScreen();
    }

    /**
     * Performs a validation of all text fields by passing the text field and the desired constraints as defined in
     * the FieldConstraint enum in the FieldValidation class. Returns true if all validation tests are successful. Will
     * display an error message and return false if any validation fails.
     *
     * @return Returns true if all validations pass.
     */
    @FXML
    private boolean checkAllFields() {
        // Check name field
        if(!FieldValidation.CheckFieldConstraint(nameField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Name", "Value cannot be empty or blank.");
            return false;
        }

        // Check price field
        if(!FieldValidation.CheckFieldConstraint(priceField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Price", "Value cannot be empty or blank.");
            return false;
        }

        if(!FieldValidation.CheckFieldConstraint(priceField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM)) {
            FieldValidation.ValidationAlert("Price", "Invalid input.\nValue must be a positive number.");
            return false;
        }

        // Check inv field
        if(!FieldValidation.CheckFieldConstraint(stockField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Inv", "Value cannot be empty or blank.");
            return false;
        }

        if(!FieldValidation.CheckFieldConstraint(stockField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Inv", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Check max field
        if(!FieldValidation.CheckFieldConstraint(maxField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Max", "Value cannot be empty or blank.");
            return false;
        }

        if(!FieldValidation.CheckFieldConstraint(maxField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Max", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Check min field
        if(!FieldValidation.CheckFieldConstraint(minField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Min", "Value cannot be empty or blank.");
            return false;
        }

        if(!FieldValidation.CheckFieldConstraint(minField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Min", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Is Max greater than Min?
        if(!FieldValidation.CheckFieldConstraint(maxField, minField, FieldValidation.FieldConstraint.X_GREATER_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Max", "Max must be greater than Min.");
            return false;
        }

        // Does Inv fall between Min and Max?
        if(!FieldValidation.CheckFieldConstraint(stockField, minField, FieldValidation.FieldConstraint.X_GREATER_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Inv", "Inv must be greater than or equal to Min.");
            return false;
        }

        if(!FieldValidation.CheckFieldConstraint(stockField, maxField, FieldValidation.FieldConstraint.X_LESS_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Inv", "Inv must be less than or equal to Max.");
            return false;
        }

        // Check Machine ID/Company Name field
        if(!FieldValidation.CheckFieldConstraint(machOrCompField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert(inhouseRadioButton.isSelected() ? "Machine ID" : "Company Name",
                                            "Value cannot be empty or blank.");
            return false;
        }

        // Check the Machine ID text field if this is an InHouse object
        if(inhouseRadioButton.isSelected()) {
            if (!FieldValidation.CheckFieldConstraint(machOrCompField,
                    FieldValidation.FieldConstraint.IS_NUMBER,
                    FieldValidation.FieldConstraint.POSITIVE_NUM,
                    FieldValidation.FieldConstraint.IS_INT)) {
                FieldValidation.ValidationAlert("Machine ID", "Invalid input.\nValue must be a positive number without a decimal.");
                return false;
            }
        }

        return true;
    }

    /**
     * Overloaded function to add a new InHouse object to inventory.
     *
     * @param partName The part's name.
     * @param partPrice The part's price.
     * @param partStock The part's inventory level.
     * @param partMin The part's minimum inventory level.
     * @param partMax The part's maximum inventory level.
     * @param partMachineId The (inhouse) part's machine ID.
     */
    private void addPartToInventory(String partName, double partPrice, int partStock, int partMin, int partMax, int partMachineId)
    {
        Inventory.addPart(new InHouse(Inventory.generatePartID(), partName, partPrice, partStock, partMin, partMax, partMachineId));
    }

    /**
     * Overloaded function to add a new Outsourced part to inventory.
     *
     * @param partName The part's name.
     * @param partPrice The part's price.
     * @param partStock The part's inventory level.
     * @param partMin The part's minimum inventory level.
     * @param partMax The part's maximum inventory level.
     * @param partCompany The (outsourced) part's company name.
     */
    private void addPartToInventory(String partName, double partPrice, int partStock, int partMin, int partMax, String partCompany)
    {
        Inventory.addPart(new Outsourced(Inventory.generatePartID(), partName, partPrice, partStock, partMin, partMax, partCompany));
    }
}
