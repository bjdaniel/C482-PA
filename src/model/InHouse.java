package model;

/**
 *  The InHouse class extends the abstract Part class to include additional class members and functions
 *  specific to in-house parts.
 *
 * @author Billy Daniel
 */
public class InHouse extends Part {
    private int machineId;

    /** The InHouse class constructor.
     * @param id The unique part ID.
     * @param name The part name.
     * @param price The part price.
     * @param stock The part stock level.
     * @param min The minimum stock level for the part.
     * @param max The maximum stock level for the part.
     * @param machineId The machine ID of the machine that creates the part.
     */
    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    /** Sets the machine ID for the part.
     * @param machineId The machine ID to be assigned to the part.
     */
    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    /** Returns the machine ID assigned to the part.
     * @return The machine ID assigned to the part.
     */
    public int getMachineId() {
        return machineId;
    }
}
