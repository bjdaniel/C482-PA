package view;

// JavaFX imports
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// Project imports
import model.Product;
import model.InHouse;
import model.Outsourced;
import model.Inventory;

// Java imports
import java.util.Optional;

/**
 *  <p>The main application class.</p>
 *
 *  <p><b>FUTURE ENHANCEMENTS:</b></p>
 *  <ul>
 *      <li>Implement a database to store Part, Products, and Inventory data. This would allow data persistence, provide
 *          better control of data constraints, better track updates, enhance security and data access, and allow multiple
 *          concurrent users to view and modify data.</li>
 *      <li>Add more reponsive elements to the user interface to enhance user experience. For example, changing the
 *          background or border color of a text field in real time and providing a popup tooltip if the user enters an
 *          invalid value into the text field.</li>
 *      <li>Allow users to customize their view to suit their specific needs. For example, allow columns in the Part and
 *          Product lists to be added or hidden. Add the means for a user to save their current view configuration so
 *          it can persist between sessions, or be later recalled if they change their view and want to revert it.</li>
 *      <li>Build an application programming interface to extend application functionality beyond the desktop. This
 *          could allow data to be created, retrieved, updated, and deleted from approved outside sources. For example,
 *          an order fulfillment system could update the inventory in real time as parts are picked for customer
 *          orders. This provides greater value to the organization by allowing data sharing across organizational
 *          functions without requiring platform replacements or potentially costly and complicated customizations.</li>
 *  </ul>
 *
 * @author Billy Daniel
 */
public class Main extends Application {

    /**
     * <p>JavaDoc located in <b>\Billy Daniel C482 PA\javadoc\</b></p>
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        //populateTestData();
        launch(args);
    }

    /**
     * The required implementation of the start() method inherited from the Application class.
     * @param primaryStage The primary application stage.
     * @throws Exception Required by the Application class
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("..\\view\\MainScreen.fxml"))));
        primaryStage.setTitle("Inventory Management System");

        // Set up handler to allow confirmation or cancellation of a request to close to primary application window
        primaryStage.setOnCloseRequest(windowEvent -> {
            if(windowEvent.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Inventory Management System");
                alert.setHeaderText("Are you sure you want to close the application?");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.NO)
                    windowEvent.consume();
            }
        });

        primaryStage.show();
    }

    /**
     * Populates data into the inventory for testing purposes.
     */
    public static void populateTestData() {
        // Populate parts into Inventory
        Inventory.addPart(new InHouse(Inventory.generatePartID(), "Prefabulated amulite base plate", 1125.23, 5, 1, 5, 100));
        Inventory.addPart(new InHouse(Inventory.generatePartID(), "Maleable logarithmic casing", 500.15, 3, 1, 5, 150));
        Inventory.addPart(new Outsourced(Inventory.generatePartID(), "Spurving bearings", 64.55, 20, 15, 50, "North Bearing Co."));
        Inventory.addPart(new Outsourced(Inventory.generatePartID(), "Stator with pandermic semi-boloid slots", 747.11, 7, 5, 10, "Stator the Union LLC"));
        Inventory.addPart(new InHouse(Inventory.generatePartID(), "Differential girdle springs", 55.13, 34, 15, 75, 205));
        Inventory.addPart(new InHouse(Inventory.generatePartID(), "Grammeters", 357.45, 9, 5, 10, 502));
        Inventory.addPart(new Outsourced(Inventory.generatePartID(), "Lotus-o-deltoid winding", 867.34, 3, 2, 6, "Wound Windings Winders Co."));
        Inventory.addPart(new InHouse(Inventory.generatePartID(), "Non-reversible tremie pipe", 14.01, 15, 5, 15, 125));

        // Populate products into Inventory
        Inventory.addProduct(new Product(Inventory.generateProductID(), "Turboencabulator", 5325.13, 2, 1, 2));
        Inventory.addProduct(new Product (Inventory.generateProductID(), "Microencabulator", 2425.99, 3, 1, 4));

        // Associate some Parts with the Products in Inventory
        Inventory.getAllProducts().get(0).addAssociatedPart(Inventory.lookupPart(1));
        Inventory.getAllProducts().get(0).addAssociatedPart(Inventory.lookupPart(2));
        Inventory.getAllProducts().get(0).addAssociatedPart(Inventory.lookupPart(4));
        Inventory.getAllProducts().get(0).addAssociatedPart(Inventory.lookupPart(5));
        Inventory.getAllProducts().get(0).addAssociatedPart(Inventory.lookupPart(6));
        Inventory.getAllProducts().get(1).addAssociatedPart(Inventory.lookupPart(1));
        Inventory.getAllProducts().get(1).addAssociatedPart(Inventory.lookupPart(2));
        Inventory.getAllProducts().get(1).addAssociatedPart(Inventory.lookupPart(4));
    }
}
