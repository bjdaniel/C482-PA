package controller;

// JavaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

// Java imports
import java.util.Optional;

// Project imports
import model.Inventory;
import model.Part;
import model.Product;

/**
 * This is the controller for the product add/modify screen.
 */
public class ProductScreen {
    // Class public member variables
    public enum Function {ADD_PRODUCT, MODIFY_PRODUCT};

    // Class private member variables
    private static ProductScreen.Function productScreenFunction;
    private static Product productScreenProduct;

    // Product form controls and labels
    @FXML
    private Label actionPlaceholder;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField maxField;
    @FXML
    private TextField minField;
    @FXML
    private TextField partSearchField;

    // All Parts table view and columns
    @FXML
    private TableView<Part> allPartsTable;
    @FXML
    private TableColumn<Part, Integer> allPartsId;
    @FXML
    private TableColumn<Part, String> allPartsName;
    @FXML
    private TableColumn<Part, Integer> allPartsStock;
    @FXML
    private TableColumn<Part, Double> allPartsPrice;

    // Associated Parts table view and columns
    @FXML
    private TableView<Part> associatedPartsTable;
    @FXML
    private TableColumn<Part, Integer> associatedPartsId;
    @FXML
    private TableColumn<Part, String> associatedPartsName;
    @FXML
    private TableColumn<Part, Integer> associatedPartsStock;
    @FXML
    private TableColumn<Part, Double> associatedPartsPrice;

    /**
     * Initializes the scene components. This includes setting the label placeholder text and updating the screen
     * title based on the Function enum (ADD_PRODUCT or MODIFY_PRODUCT) set by the main screen controller. It also
     * includes binding the all parts table view and associated parts table view to their respective ObservableList
     * datasets. It also includes a call to a method to fill in the product details in the text fields if the screen
     * function is modifying a product.
     */
    public void initialize() {
        allPartsTable.setPlaceholder(new Label("No parts in inventory."));
        associatedPartsTable.setPlaceholder(new Label("No parts associated with this product."));

        // All Parts table columns bindings
        allPartsId.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        allPartsName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        allPartsStock.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        allPartsPrice.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        // Fill All Parts data with all Parts objects in Inventory
        allPartsTable.setItems(Inventory.getAllParts());

        // Associated Parts table columns bindings
        associatedPartsId.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        associatedPartsName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        associatedPartsStock.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        associatedPartsPrice.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        // Update window title and form label based on selected screen function
        if(productScreenFunction == Function.ADD_PRODUCT) {
            MainScreen.setChildScreenTitle("Add Product");
            actionPlaceholder.setText("Add Product");
        }
        else {
            MainScreen.setChildScreenTitle("Modify Product");
            actionPlaceholder.setText("Modify Product");

            // Fill Associated Parts table with all Part objects associated with the current Product object
            // The ObservableList returned from the Product object is a copy of the object's internal list, therefore
            // any changes in the view will not immediately change the object's data until save is clicked.
            associatedPartsTable.setItems(productScreenProduct.getAllAssociatedParts());
            fillProductDetails();
        }
    }

    /**
     * Fills the text fields with the appropriate Product object properties.
     */
    private void fillProductDetails() {
        idField.setText(Integer.toString(productScreenProduct.getId()));
        nameField.setText(productScreenProduct.getName());
        stockField.setText(Integer.toString(productScreenProduct.getStock()));
        priceField.setText(Double.toString(productScreenProduct.getPrice()));
        maxField.setText(Integer.toString(productScreenProduct.getMax()));
        minField.setText(Integer.toString(productScreenProduct.getMin()));
    }

    /**
     * Sets the function of the screen (ADD_PRODUCT or MODIFY_PRODUCT).
     *
     * @param function A Function enum describing the function of the screen. The enum constant can be either
     *                 ADD_PRODUCT or MODIFY_PART.
     */
    public static void setProductScreenFunction(Function function) {
        productScreenFunction = function;
    }

    /**
     * Updates the product screen controller's private static Product variable to reference the Product object to be
     * modified.
     *
     * @param product The Product object reference passed to the controller.
     */
    public static void setProduct(Product product) {
        productScreenProduct = product;
    }

    /**
     * Handles when the user clicks the Add part button. It will display a warning if the user has not selected a
     * part from the list, or if the selected part is already associated with the Product.
     */
    @FXML
    private void onAddPartButtonClick() {
        Part partToAdd = allPartsTable.getSelectionModel().getSelectedItem();

        // Validate a part has been selected from the All Parts list
        if(partToAdd == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add Part");
            alert.setHeaderText("Select a part from the list to add.");
            alert.showAndWait();
            return;
        }

        // Check if the selected Part is already associated with the Product
        for(Part partToCheck : associatedPartsTable.getItems()) {
            if(partToCheck == partToAdd) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Add Part");
                alert.setHeaderText("Selected part is already associated with this product.");
                alert.showAndWait();
                return;
            }
        }

        // Add the selected Part to the Associated Parts list
        associatedPartsTable.getItems().add(partToAdd);
    }

    /**
     * Handles when the user clicks the Remove associated part button. It will display a warning if the user has not
     * selected a part from the list.
     */
    @FXML
    private void onRemovePartButtonClick() {
        if(associatedPartsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Remove Part");
            alert.setHeaderText("Select a part from the list to remove.");
            alert.showAndWait();
            return;
        }

        // Remove the selected Part from the Associated Parts list
        associatedPartsTable.getItems().remove(associatedPartsTable.getSelectionModel().getSelectedItem());
    }

    /**
     * Validates all fields against the constraints defined in the checkAllFields() method. If they pass, then the part
     * will be either added to inventory or updated depending on the screen function.
     */
    @FXML
    private void onSaveButtonClick() {
    // Validate all fields against their desired constraints
        if(checkAllFields()) {
            if (productScreenFunction == Function.ADD_PRODUCT) {
                Product productToAdd = new Product(
                        Inventory.generateProductID(),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText()),
                        Integer.parseInt(minField.getText()),
                        Integer.parseInt(maxField.getText()));

                for (Part item : associatedPartsTable.getItems())
                    productToAdd.addAssociatedPart(item);

                Inventory.addProduct(productToAdd);
            }
            else {
                Product updatedProduct = new Product(Integer.parseInt(idField.getText()),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText()),
                        Integer.parseInt(minField.getText()),
                        Integer.parseInt(maxField.getText()));

                for (Part item : associatedPartsTable.getItems())
                    updatedProduct.addAssociatedPart(item);

                Inventory.updateProduct(Inventory.getAllProducts().indexOf(productScreenProduct), updatedProduct);
            }
            MainScreen.closeChildScreen();
        }
    }

    /**
     * Handles the user clicking the CANCEL button. Ask for confirmation and if the user clicks YES then close the
     * screen without changing any Product object or the inventory.
     */
    @FXML
    private void onCancelButtonClick() {
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
     * This method is called any time the user changes the text in the product search text field. It will call the
     * searchProducts method in the InventorySearch class which returns an ObservableList containing any products
     * whose ID or name matches the text in the text field. If no matches are returned then the table placeholder
     * text will be set to inform the user.
     */
    @FXML
    private void onPartSearchFieldChange() {
        allPartsTable.setPlaceholder(new Label("No parts in inventory."));

        if(partSearchField.getText().isEmpty())
            allPartsTable.setItems(Inventory.getAllParts());
        else {
            allPartsTable.setItems(InventorySearch.searchParts(partSearchField.getText()));
            if(allPartsTable.getItems().size() == 0)
                allPartsTable.setPlaceholder(new Label("No results."));
        }
    }

    /**
     * Performs a validation of all text fields by passing the text field and the desired constraints as defined in
     * the FieldConstraint enum in the FieldValidation class. Returns true if all validation tests are successful. Will
     * display an error message and return false if any validation fails.
     *
     * @return Returns true if all validations pass.
     */
    private boolean checkAllFields() {
        // Check name field
        if (!FieldValidation.CheckFieldConstraint(nameField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Name", "Value cannot be empty or blank.");
            return false;
        }

        // Check price field
        if (!FieldValidation.CheckFieldConstraint(priceField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Price", "Value cannot be empty or blank.");
            return false;
        }

        if (!FieldValidation.CheckFieldConstraint(priceField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM)) {
            FieldValidation.ValidationAlert("Price", "Invalid input.\nValue must be a positive number.");
            return false;
        }

        // Check inv field
        if (!FieldValidation.CheckFieldConstraint(stockField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Inv", "Value cannot be empty or blank.");
            return false;
        }

        if (!FieldValidation.CheckFieldConstraint(stockField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Inv", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Check max field
        if (!FieldValidation.CheckFieldConstraint(maxField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Max", "Value cannot be empty or blank.");
            return false;
        }

        if (!FieldValidation.CheckFieldConstraint(maxField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Max", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Check min field
        if (!FieldValidation.CheckFieldConstraint(minField, FieldValidation.FieldConstraint.NOT_NULL_OR_EMPTY)) {
            FieldValidation.ValidationAlert("Min", "Value cannot be empty or blank.");
            return false;
        }

        if (!FieldValidation.CheckFieldConstraint(minField,
                FieldValidation.FieldConstraint.IS_NUMBER,
                FieldValidation.FieldConstraint.POSITIVE_NUM,
                FieldValidation.FieldConstraint.IS_INT)) {
            FieldValidation.ValidationAlert("Min", "Invalid input.\nValue must be a positive number without a decimal.");
            return false;
        }

        // Is Max greater than Min?
        if (!FieldValidation.CheckFieldConstraint(maxField, minField, FieldValidation.FieldConstraint.X_GREATER_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Max", "Max must be greater than Min.");
            return false;
        }

        // Does Inv fall between Min and Max?
        if (!FieldValidation.CheckFieldConstraint(stockField, minField, FieldValidation.FieldConstraint.X_GREATER_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Inv", "Inv must be greater than or equal to Min.");
            return false;
        }

        if (!FieldValidation.CheckFieldConstraint(stockField, maxField, FieldValidation.FieldConstraint.X_LESS_THAN_EQUAL_Y)) {
            FieldValidation.ValidationAlert("Inv", "Inv must be less than or equal to Max.");
            return false;
        }

        return true;
    }
}
