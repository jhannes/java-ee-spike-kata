package no.steria.kata.javaee;

import java.io.PrintWriter;

public class PersonCreateForm {

    private String firstNameError;
    private String lastNameError;
    private String firstName = "";
    private String lastName = "";
    
    

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.firstNameError = validateName(firstName, "First name");
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.lastNameError = validateName(lastName, "Last name");
    }

    public void show(PrintWriter writer) {
        writer.append("<html>");
        writer.append("<head><style>#error { color: red; }</style></head>");
    
        if (firstNameError != null) {
            writer.append("<div id='error'>").append(firstNameError).append("</div>");
        }
        if (lastNameError != null) {
            writer.append("<div id='error'>").append(lastNameError).append("</div>");
        }
        writer
            .append("<form method='post' action='createPerson.html'>")
            .append("<p>")
            .append("<label for='first_name'><b>First name:</b></label>")
            .append("<input type='text' name='first_name' value='" + PersonCreateForm.htmlEscape(firstName) + "'/>")
            .append("</p>")
            .append("<p>")
            .append("<label for='last_name'><b>Last name:</b></label>")
            .append("<input type='text' name='last_name' value='" + PersonCreateForm.htmlEscape(lastName) + "'/>")
            .append("</p>")
            .append("<input type='submit' name='createPerson' value='Create person'/>")
            .append("</form>");
        writer.append("</html>");
    }

    public boolean isValid() {
        return firstNameError == null && lastNameError == null;
    }

    public Person createPerson() {
        return Person.withName(firstName, lastName);
    }

    static boolean containsIllegalCharacters(String value) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (value.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    static String validateName(String name, String fieldName) {
        String errorMessage = null;
        if (name == null || name.equals("")) {
            errorMessage = fieldName + " must be given";
        } else if (PersonCreateForm.containsIllegalCharacters(name)) {
            errorMessage = fieldName + " contains illegal characters";
        }
        return errorMessage;
    }

    static String htmlEscape(String value) {
        if (value == null) return null;
        return value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

}
