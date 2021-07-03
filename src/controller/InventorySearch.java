package controller;

// JavaFX imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Project imports
import model.Inventory;
import model.Part;
import model.Product;

/**
 * This InventorySearch class provides methods supporting the part/product search functionality of the main screen
 * and the product add/modify screen.
 */
public class InventorySearch{
    /**
     * <p>Returns an ObservableList containing references to all Part objects whose ID equals or name contains a
     * substring of the search string passed to the method.</p>
     *
     * <p><b>RUNTIME ERROR:</b> The original method implementation used Integer.parseInt to parse the input string
     * into an Integer to compare against the part's ID. However, this would cause a runtime error any time the
     * user entered anything that couldn't be parsed into an Integer in the search text field. This could have been
     * resolved with a try-catch block, but it seemed simpler to instead parse the part's ID to a string to compare
     * against the search string.</p>
     *
     * @param searchString The search string to be used to look up a part by ID or name.
     * @return An ObservableList of Part object references whose ID equals or name contains a substring of the search
     * string passed to the method.
     */
    public static ObservableList<Part> searchParts(String searchString) {
        ObservableList<Part> returnList = FXCollections.observableArrayList();

        // Iterate through each Part object in inventory and test if its parsed ID equals or name contains a substring
        // of the search string
        for(Part item : Inventory.getAllParts()) {
            if(Integer.toString(item.getId()).contentEquals(searchString) ||
                    item.getName().toLowerCase().contains(searchString.toLowerCase()))
                returnList.add(item);
        }

        return returnList;
    }

    /**
     * Returns an ObservableList containing references to all Product objects whose ID equals or name contains a
     * substring of the search string passed to the method.
     *
     * @param searchString The search string to be used to look up a part by ID or name.
     * @return An ObservableList of Product object references whose ID equals or name contains a substring of the search
     * string passed to the method.
     */
    public static ObservableList<Product> searchProducts(String searchString) {
        ObservableList<Product> returnList = FXCollections.observableArrayList();

        // Iterate through each Product object in inventory and test if its parsed ID equals or name contains a
        // substring of the search string
        for(Product item : Inventory.getAllProducts()) {
            if(Integer.toString(item.getId()).contentEquals(searchString) ||
                    item.getName().toLowerCase().contains(searchString.toLowerCase()))
                returnList.add(item);
        }

        return returnList;
    }
}
