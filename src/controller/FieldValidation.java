package controller;

// JavaFX imports
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

// Java imports
import java.security.InvalidParameterException;

/**
 * <p>The FieldValidation class provides methods to support validation testing of text field input against a set of
 * validations rules as classified in the FieldConstraint enum. The validation rules as of this version of the class
 * includes:</p>
 * <ul>
 *     <li><b>NOT_NULL_OR_EMPTY</b></li>
 *     <li><b>IS_NUMBER</b></li>
 *     <li><b>IS_INT</b></li>
 *     <li><b>POSITIVE_NUM</b></li>
 *     <li><b>X_GREATER_THAN_EQUAL_Y</b></li>
 *     <li><b>X_LESS_THAN_EQUAL_Y</b></li>
 * </ul>
 * <p>Whenever a constraint violation is found, the method will display an error message to the user and return
 * false to the callee to indicate the validation test failed.</p>
 */
public class FieldValidation {
    public static enum FieldConstraint {
        NOT_NULL_OR_EMPTY,
        IS_NUMBER,
        IS_INT,
        POSITIVE_NUM,
        X_GREATER_THAN_EQUAL_Y,
        X_LESS_THAN_EQUAL_Y
    };

    /**
     * Validates whether the integer value of the first text field (X) is greater than or equal to/less than or equal
     * to the integer value of the second text field (Y). Will throw an InvalidParameterException if a null, empty, or
     * non-number value is passed from either text field.
     *
     * @param textField1 The first text field (X).
     * @param textField2 The second text field (Y).
     * @param fieldConstraint The constraint rule to validate (either X_LESS_THAN_EQUAL_Y or X_GREATER_THAN_EQUAL_Y)
     * @return True if the validation test passed.
     */
    public static boolean CheckFieldConstraint(TextField textField1, TextField textField2, FieldConstraint fieldConstraint) {
        if (!(fieldConstraint == FieldConstraint.X_GREATER_THAN_EQUAL_Y || fieldConstraint == FieldConstraint.X_LESS_THAN_EQUAL_Y))
            throw new InvalidParameterException("Incorrect method form for this constraint type. Use CheckFieldConstraint(TextField textField1, FieldConstraint... fieldConstraints) instead.");

        if (!CheckFieldConstraint(textField1, FieldConstraint.NOT_NULL_OR_EMPTY, FieldConstraint.IS_NUMBER))
            throw new InvalidParameterException("Invalid input provided in textField1 parameter for constraint type.");

        if (!CheckFieldConstraint(textField2, FieldConstraint.NOT_NULL_OR_EMPTY, FieldConstraint.IS_NUMBER))
            throw new InvalidParameterException("Invalid input provided in textField2 parameter for constraint type.");

        if (fieldConstraint == FieldConstraint.X_GREATER_THAN_EQUAL_Y)
            return (Double.parseDouble(textField1.getText()) >= Double.parseDouble(textField2.getText()));

        if (fieldConstraint == FieldConstraint.X_LESS_THAN_EQUAL_Y)
            return (Double.parseDouble(textField1.getText()) <= Double.parseDouble(textField2.getText()));

        return false;
    }

    /**
     * Validates the text field against any of the FieldConstraint constants passed to the method except the
     * X_GREATER_THAN_EQUAL_Y or X_LESS_THAN_EQUAL_Y constraint checks. Will throw an InvalidParameterException if
     * either are passed to the method.
     *
     * @param textField1 The text field to be validated.
     * @param fieldConstraints The constraint(s) to be tested.
     * @return True if the validation test(s) pass.
     */
    public static boolean CheckFieldConstraint(TextField textField1, FieldConstraint... fieldConstraints) {

        for (FieldConstraint fieldConstraintToCheck : fieldConstraints) {
            // Cannot perform X greater than/less than Y constraint validation with single TextField
            if ((fieldConstraintToCheck == FieldConstraint.X_GREATER_THAN_EQUAL_Y || fieldConstraintToCheck == FieldConstraint.X_LESS_THAN_EQUAL_Y))
                throw new InvalidParameterException("Incorrect method form for constraint type. Use overloaded CheckFieldConstraint(TextField textField1, TextField textField2, FieldConstraint fieldConstraint) instead.");

            // Validate if the text field is null or empty
            if (fieldConstraintToCheck == FieldConstraint.NOT_NULL_OR_EMPTY)
                if (textField1 == null || textField1.getText().isEmpty() || textField1.getText().isBlank())
                    return false;

            // Validate if the text field is a number
            if (fieldConstraintToCheck == FieldConstraint.IS_NUMBER) {
                try {
                    double d = Double.parseDouble(textField1.getText());
                } catch (Exception e) {
                    return false;
                }
            }

            // Validate if the text field is an integer
            if (fieldConstraintToCheck == FieldConstraint.IS_INT) {
                try {
                    int i = Integer.parseInt(textField1.getText());
                }
                catch (NumberFormatException e) {
                    return false;
                }
                catch (Exception e) {
                    throw new InvalidParameterException("Incorrect input provided for constraint type.");
                }
            }

            // Validate if the text field is a positive number. Parse input into double because parsing -0.25 to an
            // integer would give us an int equal to 0, causing the constraint check to incorrectly return true
            // when should be false.
            if (fieldConstraintToCheck == FieldConstraint.POSITIVE_NUM) {
                try {
                    if (Double.parseDouble(textField1.getText()) < 0.00)
                        return false;
                } catch (Exception e) {
                    throw new InvalidParameterException("Incorrect input provided for constraint type.");
                }
            }
        }

        return true;
    }

    /**
     * Provides a convenient means for displaying an alert for a failed validation.
     *
     * @param textFieldName The name of the text field whose validation failed.
     * @param alertMessage The message to display in the alert.
     */
    public static void ValidationAlert(String textFieldName, String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MainScreen.getChildScreenTitle());
        alert.setHeaderText(textFieldName + ": " + alertMessage);

        alert.showAndWait();
    }
}
