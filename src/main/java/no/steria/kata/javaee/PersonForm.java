package no.steria.kata.javaee;

import java.io.PrintWriter;

public class PersonForm {

    private String lastName = "";
    private String lastNameError;
    private String firstName = "";
    private String firstNameError;

    private static String htmlEscape(String lastName) {
        return lastName.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
        this.firstNameError = validateName(firstName, "First name");
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
        this.lastNameError = validateName(lastName, "Last name");
    }

    void showForm(PrintWriter writer) {
        writer.append("<html>");
        writer.append("<head><style>#error { color: red; }</style></head>");
    
        if (lastNameError != null) {
            writer.append("<div id='error'>").append(lastNameError).append("</div>");
        }
        if (firstNameError != null) {
            writer.append("<div id='error'>").append(firstNameError).append("</div>");
        }
        writer //
            .append("<form method='post' action='createPerson.html'>") //
            .append("<p>")
            .append("<label for='first_name'><b>First name:</b></label>")
            .append("<input type='text' name='first_name' value='" + htmlEscape(firstName) + "'/>") //
            .append("</p>")
            .append("<p>")
            .append("<label for='last_name'><b>Last name:</b></label>")
            .append("<input type='text' name='last_name' value='" + htmlEscape(lastName) + "'/>") //
            .append("</p>")
            .append("<input type='submit' name='createPerson' value='Create person'/>") //
            .append("</form>"); 
        writer.append("</html>");
    }

    static String validateName(String lastName, String fieldName) {
        String errorMessage = null;
        if (lastName.equals("")) {
            errorMessage = fieldName + " must be given";
        } else if (PersonForm.containsIllegalCharacters(lastName)) {
            errorMessage = fieldName + " contains illegal characters";
        }
        return errorMessage;
    }

    static boolean containsIllegalCharacters(String lastName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (lastName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public boolean hasErrors() {
        return firstNameError != null || lastNameError != null;
    }

    public Person createPerson() {
        return Person.withName(firstName, lastName);
    }

}
