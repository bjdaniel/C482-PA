package controller;

// JavaFX imports
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// Java imports
import java.io.IOException;
import java.util.Optional;

// Project imports
import model.*;

/**
 * This is the controller for the main application screen.
 */
public class MainScreen {
    // Class enums
    private enum ScreenFunction {ADD, MODIFY};
    private enum Entity {PART, PRODUCT};

    // JavaFX stage for the Parts and Products add/modification screens
    private static Stage childScreenStage;

    // MainScreen controls
    @FXML
    private TextField partSearchField;
    @FXML
    private TextField productSearchField;

    // Parts TableView components
    @FXML
    private TableView<Part> partsTable;
    @FXML
    private TableColumn<Part, Integer> partId;
    @FXML
    private TableColumn<Part, String> partName;
    @FXML
    private TableColumn<Part, Double> partPrice;
    @FXML
    private TableColumn<Part, Integer> partStock;

    // Products TableView components
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> productId;
    @FXML
    private TableColumn<Product, String> productName;
    @FXML
    private TableColumn<Product, Double> productPrice;
    @FXML
    private TableColumn<Product, Integer> productStock;

    /**
     * Initializes the scene components, including binding the Part and Product TableViews and their columns to the
     * respective objects' and their properties.
     */
    public void initialize() {
        partsTable.setPlaceholder(new Label("No parts in inventory.\nClick Add below to populate inventory."));
        productsTable.setPlaceholder(new Label("No products in inventory.\nClick Add below to populate inventory."));

        // Bind the Part TableView columns to the Part object members
        partId.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        partPrice.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
        partStock.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));

        // Associate the Part TableView with the allParts ObservableList in the Inventory object
        partsTable.setItems(Inventory.getAllParts());

        // Bind the Product TableView columns to the Product object members
        productId.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        productStock.setCellValueFactory(new PropertyValueFactory<Product, Integer>("stock"));

        // Associate the Product TableView with the allProducts ObservableList in the Inventory object
        productsTable.setItems(Inventory.getAllProducts());
    }

    /**
     * Launches a screen allowing the user to add a new part to inventory.
     */
    @FXML
    private void onPartAddButtonClick() {
        launchChildScreen(ScreenFunction.ADD, Entity.PART);
    }

    /**
     * <p>Launches a screen allowing the user to modify a part in inventory. Will display an error if the user has not
     * selected a Part in the list to modify.</p>
     * <p><b>RUNTIME ERROR: In the original implementation of this method, an exception could be generated if the user
     * searched for a part, selected a part, modified and saved it, and then immediately modified and saved the part
     * again before clearing or updating the search. The reason this caused an exception is because the list view
     * still contained a reference to the old Part object prior to the modification action. The method was updated
     * to automatically clear the search text field and repopulate the Parts table view to avoid the possibility
     * of this exception occurring.</b></p>
     */
    @FXML
    private void onPartModifyButtonClick() {
        // Display an error message if the user has not selected a part from the list
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modify part");
            alert.setHeaderText("Select a part from the list to modify.");
            alert.showAndWait();
            return;
        }

        launchChildScreen(ScreenFunction.MODIFY, Entity.PART);

        // Clear the search field and repopulate the Parts list
        partSearchField.clear();
        onPartSearchChange();
    }

    /**
     * <p>Handles the user clicking on the part Delete button. Will display an error if the user has not selected a Part
     * in the list to delete. Will also display an error if the part cannot be deleted (for example, if it associated
     * with a Product in inventory).</p>
     * <p><b>RUNTIME ERROR:</b> In the original implementation of this method, if the user searched for a part, selected
     * a part, and then deleted it the part would remain in the Part table view. This is because the part deletion
     * only affected the Inventory.allParts ObservableList whereas the table view reflects a separate
     * ObservableList that was returned as a result of the user's search. The part displayed no longer had a valid
     * reference and so if the user tried to act on the part in any way an exception would occur. The method was
     * modified to rerun the search so the object deletion would be reflected in the table view.</p>
     */
    @FXML
    private void onPartDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.NONE);

        // Check if the user has selected an item in the Parts list
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Delete part");
            alert.setHeaderText("Select a part from the list to delete.");
            alert.showAndWait();
            return;
        }

        // Get confirmation from the user if they really want to delete the part
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Delete part");
        alert.setHeaderText("This will permanently delete the part from inventory.\nAre you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        // Cancel request to delete the part
        if (result.isPresent() && result.get() == ButtonType.NO)
            return;

        // Attempt to delete the part - alert the user if it cannot be removed from inventory
        if (!Inventory.deletePart(partsTable.getSelectionModel().getSelectedItem())) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.setTitle("Delete part");
            alert.setHeaderText("Could not delete the selected part.\nIt may be associated with a product in inventory.");
            alert.showAndWait();
            return;
        }

        // Call the onPartSearchChange method to requery the search and update the Part table view to reflect the
        // production deletion
        onPartSearchChange();
    }

    /**
     * Launches a screen that allows the user to add a new product to inventory.
     */
    @FXML
    private void onProductAddButtonClick() {
        launchChildScreen(ScreenFunction.ADD, Entity.PRODUCT);
    }

    /**
     * Launches a screen that allows the user to modify an existing product in inventory. Will display an error message
     * if the user has not selected a product from the list to modify.
     */
    @FXML
    private void onProductModifyButtonClick() {
        // Display an error if the user has not selected a product from the list to modify.
        if (productsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modify product");
            alert.setHeaderText("Select a product from the list to modify.");
            alert.showAndWait();
            return;
        }

        launchChildScreen(ScreenFunction.MODIFY, Entity.PRODUCT);

        // Clear the Product search text field and repopulate the Products table view.
        productSearchField.clear();
        onProductSearchChange();
    }

    /**
     * Handles the user clicking on the product Delete button. Will display an error if the user has not selected a
     * Product in the list to delete. Will also display an error if the part cannot be deleted (for example, if it
     * contains associated parts).
     */
    @FXML
    private void onProductDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.NONE);

        // Check if an item has been selected in the Products list
        if (productsTable.getSelectionModel().getSelectedItem() == null) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setTitle("Delete product");
            alert.setHeaderText("Select a product from the list to delete.");
            alert.showAndWait();
            return;
        }

        // Get confirmation from the user if they really want to delete the product
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.setTitle("Delete product");
        alert.setHeaderText("This will permanently delete the product from inventory.\nAre you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        // Cancel request to delete the product
        if (result.isPresent() && result.get() == ButtonType.NO)
            return;

        // Attempt to delete the product - alert the user if an error occurred during deletion
        if (!Inventory.deleteProduct(productsTable.getSelectionModel().getSelectedItem())) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.setTitle("Delete product");
            alert.setHeaderText("Could not delete the selected product.\nIt may have associated parts which must be removed first.");
            alert.showAndWait();
            return;
        }

        // Call the onProductSearchChange method to requery the search and update the Product table view to reflect the
        // deletion
        onProductSearchChange();
    }

    /**
     * This method is called any time the user changes the text in the product search text field. It will call the
     * searchProducts method in the InventorySearch class which returns an ObservableList containing any products
     * whose ID or name matches the text in the text field. If no matches are returned then the table placeholder
     * text will be set to inform the user.
     */
    @FXML
    private void onProductSearchChange() {
        productsTable.setPlaceholder(new Label("No products in inventory.\nClick Add below to populate inventory."));

        if(productSearchField.getText().isEmpty()) {
            productsTable.setItems(Inventory.getAllProducts());
        }
        else {
            productsTable.setItems(InventorySearch.searchProducts(productSearchField.getText()));
            if(productsTable.getItems().size() == 0)
                productsTable.setPlaceholder(new Label("No results."));
        }
    }

    /**
     * This method is called any time the user changes the text in the part search text field. It will call the
     * searchParts method in the InventorySearch class which returns an ObservableList containing any part
     * whose ID or name matches the text in the text field. If no matches are returned then the table placeholder
     * text will be set to inform the user.
     */
    @FXML
    private void onPartSearchChange() {
        partsTable.setPlaceholder(new Label("No parts in inventory.\nClick Add below to populate inventory."));

        if(partSearchField.getText().isEmpty())
            partsTable.setItems(Inventory.getAllParts());
        else {
            partsTable.setItems(InventorySearch.searchParts(partSearchField.getText()));
            if(partsTable.getItems().size() == 0)
                partsTable.setPlaceholder(new Label("No results."));
        }
    }

    /**
     * Handles the user clicking on the Exit button. A dialog box will appear asking for the user's confirmation and
     * their exit request will be cancelled if they click NO.
     */
    @FXML
    private void onExitButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Inventory Management System");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Shut down the application if the user confirms they want to exit
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.YES)
            Platform.exit();
    }

    /**
     * <p>Launches a child screen based on the entity type (PART or PRODUCT) and the screen function type (ADD or
     * MODIFY) passed to the method. A new Stage will be instantiated and the fxml file for the respective
     * scene will be loaded. The Stage object is referenced with a public static class member variable so it
     * may be accessed by the part screen or product screen controller. This important for the some of the
     * controllers' functionality.</p>
     *
     * <p>The method will set the Function enum of the respective part or product controller and, if the function
     * is a modification, then it will pass the selected part or product to the controller as well.</p>
     *
     * <p>The new screen will be launched as APPLICATION_MODAL to prevent user action on the
     * main screen while the child screen is still open. A handler is set up to catch if the user requests to close
     * the child screen and asks for confirmation before either allowing the child window to be closed or cancelling
     * the close request.</p>
     *
     * @param screenFunction A ScreenFunction enum constant describing the function of the child window to be launched.
     *                       The enum constant is either ADD or MODIFY.
     * @param entity An Entity enum constant describing whether the child screen will affect a part or product. The
     *               enum constant is either PART or PRODUCT.
     */
    @FXML
    private void launchChildScreen(ScreenFunction screenFunction, Entity entity) {
        // A String containing the path to the fxml file to be used for the new screen
        String fxmlResource;

        // Instantiate a new Stage object which will contain the scene for the new screen
        childScreenStage = new Stage();

        // A part add/modify screen is being launched
        if (entity == Entity.PART) {
            fxmlResource = "..\\view\\PartScreen.fxml";

            // If user clicked Modify then ensure a Part from the list has been selected
            if (screenFunction == ScreenFunction.MODIFY) {
                PartScreen.setPart(partsTable.getSelectionModel().getSelectedItem());
            }

            // Tell the Part screen what it is going to be doing so it can update labels appropriately
            PartScreen.setPartScreenFunction(screenFunction == ScreenFunction.ADD ?
                                                PartScreen.Function.ADD_PART :
                                                PartScreen.Function.MODIFY_PART);
        }
        // A product add/modify screen is being launched
        else {
            fxmlResource = "..\\view\\ProductScreen.fxml";

            // If user selected Modify Product then ensure a Product from the list has been selected
            if (screenFunction == ScreenFunction.MODIFY) {
                // Pass Product object selected to the Product add/modify screen
                ProductScreen.setProduct(productsTable.getSelectionModel().getSelectedItem());
            }

            // Tell the Product screen what it's going to be doing so it can update labels appropriately
            ProductScreen.setProductScreenFunction(screenFunction == ScreenFunction.ADD ?
                                                    ProductScreen.Function.ADD_PRODUCT :
                                                    ProductScreen.Function.MODIFY_PRODUCT);
        }

        // Set up a close window request event handler
        childScreenStage.setOnCloseRequest(event -> {
            if(event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(childScreenStage.getTitle());
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setHeaderText("Any changes will be lost.\nAre you sure?");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.NO) {
                    // Cancel the close window request if the user answers NO to the prompt
                    event.consume();
                }
            }
        });

        // Open new screen as an application modal screen
        try {
            Parent screenRoot = FXMLLoader.load(getClass().getResource(fxmlResource));
            childScreenStage.setScene(new Scene(screenRoot));
            childScreenStage.initModality(Modality.APPLICATION_MODAL);
            childScreenStage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not launch screen.");
            alert.showAndWait();
        }
    }

    /**
     * Returns a String containing the child screen's title.
     *
     * @return A string containing the child screen's title.
     */
    public static String getChildScreenTitle() {
        return childScreenStage.getTitle();
    }

    /**
     * Sets the child screen's title.
     * @param title A String containing the desired title for the child screen.
     */
    public static void setChildScreenTitle(String title) {
        childScreenStage.setTitle(title);
    }

    /**
     * Closes the child screen and sets the public static class variable referencing the screen's stage to null.
     */
    public static void closeChildScreen() {
        // Close child screen and dereference the public static childScreenStage class variable
        childScreenStage.close();
        childScreenStage = null;
    }
}