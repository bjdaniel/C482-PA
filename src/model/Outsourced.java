package model;

import model.Part;

/**
 * The Outsourced class extends the abstract Part class to include additional class members and functions
 * specific to outsourced parts.
 *
 * @author Billy Daniel
 */
public class Outsourced extends Part {
    private String companyName;

    /** The Outsourced class constructor.
     * @param id The unique part ID.
     * @param name The part name.
     * @param price The part price.
     * @param stock The part stock level.
     * @param min The minimum stock level for the part.
     * @param max The maximum stock level for the part.
     * @param companyName The name of the company providing the outsourced part.
     */
    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName;
    }

    /** Sets the company name for the part.
     * @param companyName The company name to be assigned to the part.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /** Returns the company name assigned to the part.
     * @return The company name assigned to the part.
     */
    public String getCompanyName() {
        return this.companyName;
    }
}
