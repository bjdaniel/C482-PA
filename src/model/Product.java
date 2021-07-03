package model;

// JavaFx imports
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

// Java imports
import java.util.Iterator;

/**
 * The Product class provides methods for creating Product objects and getting and setting properties of the
 * Product object instance.
 */
public class Product {
    private ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    /** The constructor for the model.Product class.
     * @param id The unique ID for the product.
     * @param name The product name.
     * @param price The product price.
     * @param stock The product stock level.
     * @param min The minimum stock level for the product.
     * @param max The maximum stock level for the product.
     */
    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
        this.associatedParts = FXCollections.observableArrayList();
    }

    /** Sets the ID property of the Product object.
     * @param id The product ID to assign to the Product.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Returns the Product object's ID property.
     * @return The Product object's ID.
     */
    public int getId() {
        return this.id;
    }

    /** Sets the Product object's name property.
     * @param name The name to assign to the Product object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the Product object's name property.
     * @return The Product object's name.
     */
    public String getName() {
        return this.name;
    }

    /** Sets the Product object's price property.
     * @param price The price to set for the product.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /** Returns the Product object's price property.
     * @return The Product's price.
     */
    public double getPrice() {
        return this.price;
    }

    /** Sets the current stock level of the Product object.
     * @param stock The stock level to set for the product.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /** Returns the stock level of the Product object.
     * @return The product stock level.
     */
    public int getStock() {
        return this.stock;
    }

    /** Sets the minimum stock level for the Product object.
     * @param min The minimum stock level to set for the product.
     */
    public void setMin(int min) {
        this.min = min;
    }

    /** Returns the minimum stock level for the Product object.
     * @return The minimum stock level for the Product object.
     */
    public int getMin() {
        return this.min;
    }

    /** Sets the maximum stock level for the Product object.
     * @param max The maximum stock level to set for the Product object.
     */
    public void setMax(int max) {
        this.max = max;
    }

    /** Returns the maximum stock level for the Product object.
     * @return The maximum stock level for the Product object.
     */
    public int getMax() {
        return this.max;
    }

    /** Inserts a Part object into the associatedParts ObservableList.
     * @param part The Part object to insert into the associatedParts ObservableList.
     */
    public void addAssociatedPart(Part part) {
        associatedParts.add(part);
    }

    /** Removes a Part object from the associatedParts ObservableList.
     * @param selectedAssociatedPart The Part object to be removed from the associatedParts ObservableList.
     * @return True if the removal was successful; false if the Part supplied in argument was not found.
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        Iterator<Part> iterator = associatedParts.iterator();

        // Iterate through the Part objects in allParts using an Iterator to avoid a ConcurrentModificationException
        // if a match is removed from the collection. If the Part object is the same object referenced in the supplied
        // argument then remove it using the iterator and return true. Otherwise, if no matches
        // found then return false.
        while(iterator.hasNext()) {
            Part lookupPart = iterator.next();
            if(lookupPart == selectedAssociatedPart) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    /** <p>Returns an ObservableList containing references to all of the Part objects in the associatedParts
     * ObservableList.</p>
     *
     * <p><b>RUNTIME ERROR: </b>This method originally returned a reference to the Product object's AssociatedParts
     * observable list. This became a problem when I used that list in the Product screen controller because removing
     * parts in the TableView actually removed the Part object from the Product's AssociatedParts list. This was
     * undesirable because the action then became immediately permanent rather than requiring the user to click Save
     * to commit their changes (and to give them the option to undo/cancel their actions using Cancel). The method was
     * modified to return a <i>copy</i> of the Product object's AssociatedParts list to avoid this problem.</p>
     *
     * @return An ObservableList of all of the Part objects in the associatedParts ObservableList.
     */
    public ObservableList<Part> getAllAssociatedParts() {
        ObservableList<Part> associatedPartsToReturn = FXCollections.observableArrayList();

        // Copy each Part object reference in associatedParts into the associatedPartsToReturn ObservableList
        for(Part partToAdd : associatedParts)
            associatedPartsToReturn.add(partToAdd);

        return associatedPartsToReturn;
    }
}