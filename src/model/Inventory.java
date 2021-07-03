package model;

// JavaFX imports
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

// Java imports
import java.util.Iterator;

/**
 * The Inventory class stores Part and Product objects for the application and provides methods for adding,
 * modifying, deleting, looking up, and tracking Part and Product objects.
 *
 * @author Billy Daniel
 */
public class Inventory {
    // Part and Product object reference arrays
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    // Part and Product indexes to assist with assigning unique IDs to all Part and Product objects in Inventory
    private static int partIndex = 1;
    private static int productIndex = 1;

    /**
     * Returns the next available Part object index and then increments the index by one.
     * @return The next available Part object index.
     */
    public static int generatePartID() {
        return partIndex++;
    }

    /**
     * Returns the next available Product object index and then increments the index by one.
     * @return The next available Product object index.
     */
    public static int generateProductID() {
        return productIndex++;
    }

    /**
     * Adds a Part object to the inventory.
     * @param newPart The Part object to be added to the inventory.
     */
    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    /**
     * Updates an existing Part object in inventory. This method will seek out any Products that are associated with
     * the Part and ensure their object references are updated so the Part object details are consistent between
     * the Inventory Parts list and any associated Product Parts list.
     *
     * @param index The location of the Part object in the allParts ObservableList that is to be updated.
     * @param selectedPart The updated Part object which will replace the existing Part object.
     */
    public static void updatePart(int index, Part selectedPart) {
        // Throw an IndexOutOfBoundsException if index supplied is negative or larger than size of allParts list
        if(index < 0 || index >= allParts.size()) {
            throw new IndexOutOfBoundsException();
        }

        // Store a reference to the existing Part which is going to be replaced by the Part supplied to the method
        Part partToDelete = Inventory.lookupPart(selectedPart.getId());

        // Get an array of all Product objects that are associated with the Part object to be updated
        ObservableList<Product> productsWithDependency = Inventory.getAllPartAssociations(partToDelete);

        // Remove dependencies from Product(s)
        for(Product productToUpdate : productsWithDependency)
            productToUpdate.deleteAssociatedPart(partToDelete);

        // Update the Part in Inventory
        Inventory.deletePart(partToDelete);
        Inventory.getAllParts().add(index, selectedPart);

        // Re-establish dependencies in Product(s) with updated Part
        for(Product productToUpdate : productsWithDependency)
            productToUpdate.addAssociatedPart(selectedPart);
    }

    /**
     * <p>Deletes a Part object from inventory if it is not associated with any Product in inventory.</p>
     *
     * <p><b>RUNTIME ERROR:</b> My first version of this method iterated through the allParts list using an
     * enhanced for-loop. When I attempted to remove a Part object from the list during iteration I encountered a
     * <i>ConcurrentModificationException</i>. I learned this was because you cannot modify a collection while
     * iterating through it using a for-loop because it can produce undefined behavior. However, an Iterator may be
     * utilized which allows the list to be updated as you are iterating through it using the Iterator.</p>
     *
     * @param selectedPart The Part to be deleted from inventory.
     * @return Returns true if the deletion was completed successfully.
     */
    public static boolean deletePart(Part selectedPart) {
        Iterator<Part> iterator = allParts.iterator();

        // Iterate through the Part objects in allParts using an Iterator to avoid a ConcurrentModificationException
        // if a Part is removed from the collection. Check any matches for a Product -> Part dependency for all Products
        // in Inventory. If no dependencies are found then delete the Part and return true. Otherwise, return false.
        while(iterator.hasNext()) {
            Part lookupPart = iterator.next();
            if(lookupPart == selectedPart && !isPartAssociated(lookupPart)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if a Product in inventory is associated with the specified Part object.
     *
     * @param partToCheck The Part object which will be checked for in all Products' AssociatedParts lists.
     * @return True if the specified Part is associated with a Product in inventory.
     */
    public static boolean isPartAssociated(Part partToCheck) {
        // Iterate through all Product objects in Inventory and check if a reference to the specified object
        // exists in the AssociatedParts list of the Products. If an association is found then return true;
        for(Product productToCheck : Inventory.getAllProducts())
            for(Part associatedPart : productToCheck.getAllAssociatedParts()) {
                if (associatedPart == partToCheck)
                    return true;
            }

        return false;
    }

    /**
     * Returns an ObservableList containing references to all Product objects that are associated with the
     * specified Part object.
     *
     * @param partToCheck The Part object which will be checked for in all Products' AssociatedPart lists.
     * @return An ObservableList containing references to all Product objects that are associated with the
     * specified Part object.
     */
    public static ObservableList<Product> getAllPartAssociations(Part partToCheck) {
        ObservableList<Product> productsWithPart = FXCollections.observableArrayList();

        // Iterate through all Product objects in Inventory and check if a reference to the specified object
        // exists in the AssociatedParts list of the Products.
        for(Product productToCheck : Inventory.getAllProducts())
            for(Part associatedPart : productToCheck.getAllAssociatedParts()) {
                if (associatedPart == partToCheck)
                    productsWithPart.add(productToCheck);
            }

        return productsWithPart;
    }

    /**
     * Searches for a Part in inventory whose ID matches the supplied ID. If a matching Part is found then a reference
     * to the object is returned. Otherwise, null is returned.
     *
     * @param partId The Part ID to look up.
     * @return A reference to a Part object if a Part is found. Otherwise, null.
     */
    public static Part lookupPart(int partId) {
        // Iterate through the Parts objects in allParts using the enhanced for-loop and test if the object's id
        // member variable is equal to the supplied argument. If a match is found, then return a reference to the
        // object via foundPart. Otherwise, return a null reference.
        for(Part lookupPart : allParts) {
            if(lookupPart.getId() == partId) {
                return lookupPart;
            }
        }

        return null;
    }

    /**
     * Searches for a Part in inventory whose name matches the supplied name. If a matching Part is found then a
     * reference to the object is returned. Otherwise, null is returned.
     *
     * @param partName The Part name to look up.
     * @return A reference to a Part object if a Part is found. Otherwise, null.
     */
    public static Part lookupPart(String partName) {
        // Iterate through the Parts objects in allParts using the enhanced for-loop and test if the object's name
        // member variable is equal to the supplied argument. If a match is found, then return a reference to the
        // object via foundPart. Otherwise, return a null reference.
        for(Part lookupPart : allParts) {
            if(lookupPart.getName().equals(partName)) {
                return lookupPart;
            }
        }

        return null;
    }

    /**
     * Returns an ObservableList containing references to all Part objects in inventory.
     * @return An ObservableList containing references to all Part objects in inventory.
     */
    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     * Adds a Product object to inventory.
     *
     * @param newProduct The Product object to be added to inventory.
     */
    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * Updates an existing Product in inventory.
     *
     * @param index The location of the Product object in the allProducts ObservableList that is to be updated.
     * @param selectedProduct The updated Product object which will replace the existing Product object.
     */
    public static void updateProduct(int index, Product selectedProduct) {
        // Throw an IndexOutOfBoundsException if index supplied is negative or larger than size of allParts list
        if(index < 0 || index >= allProducts.size()) {
            throw new IndexOutOfBoundsException();
        }

        Inventory.getAllProducts().set(index, selectedProduct);
    }

    /**
     * Deletes an existing Product from inventory if it does not have any associated Part objects.
     *
     * @param selectedProduct The Product to be deleted from inventory.
     * @return Returns true if the Product was successfully deleted. Otherwise, false.
     */
    public static boolean deleteProduct(Product selectedProduct) {
        Iterator<Product> iterator = allProducts.iterator();

        // Iterate through the Products objects in allProducts using an Iterator to avoid a
        // ConcurrentModificationException if a match is removed from the collection. If the Product object is the same
        // object referenced in the supplied argument and it has no associated Part objects then remove it using
        // the iterator and return true. Otherwise, return false.
        while(iterator.hasNext()) {
            Product lookupProduct = iterator.next();
            if(lookupProduct == selectedProduct && lookupProduct.getAllAssociatedParts().size() == 0) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Searches for a Product in inventory whose ID matches the supplied ID. If a matching Product is found then a
     * reference to the object is returned. Otherwise, null is returned.
     *
     * @param productId The Product ID to look up.
     * @return A reference to a Product object if it is found. Otherwise, null.
     */
    public static Product lookupProduct(int productId) {
        Product foundProduct = null;

        // Iterate through the Product objects in allProducts using the enhanced for-loop and test if the object's id
        // member variable is equal to the supplied argument. If a match is found, then return a reference to the
        // object. Otherwise, return a null reference.
        for(Product lookupProduct : allProducts) {
            if(lookupProduct.getId() == productId) {
                return lookupProduct;
            }
        }

        return null;
    }

    /**
     * Searches for a Product in inventory whose name matches the supplied name. If a matching Product is found then a
     * reference to the object is returned. Otherwise, null is returned.
     *
     * @param productName The Product name to look up.
     * @return A reference to a Product object if it is found. Otherwise, null.
     */
    public static Product lookupProduct(String productName) {
        // Iterate through the Product objects in allProducts using the enhanced for-loop and test if the object's name
        // member variable is equal to the supplied argument. If a match is found, then return a reference to the
        // object via foundProduct. Otherwise, return a null reference.
        for(Product lookupProduct : allProducts) {
            if (lookupProduct.getName().equals(productName)) {
                return lookupProduct;
            }
        }

        return null;
    }

    /**
     * Returns an ObservableList containing references to all Product objects in inventory.
     *
     * @return An ObservableList containing references to all Product objects in inventory.
     */
    public static ObservableList<Product> getAllProducts () {
        return allProducts;
    }
}