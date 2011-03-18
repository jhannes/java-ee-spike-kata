package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 7744195856599544243L;
    private PersonDao personDao;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.beginTransaction();
        boolean commit = false;
        try {
            super.service(req, resp);
            commit = true;
        } finally {
            personDao.endTransaction(commit);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if (req.getPathInfo().equals("/findPeople.html")) {
            String nameQuery = req.getParameter("name_query");
            List<Person> people = personDao.findPeople(nameQuery);
            showSearchPage(writer, nameQuery, people);
        } else {
            showCreatePage(writer, "", null, "", null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lastName = req.getParameter("last_name");
        String lastNameError = validateName(lastName, "Last name");
        String firstName = req.getParameter("first_name");
        String firstNameError = validateName(firstName, "First name");

        if (lastNameError == null && firstNameError == null) {
            personDao.createPerson(Person.withName(firstName, lastName));
            resp.sendRedirect("/");
        } else {
            resp.setContentType("text/html");
            showCreatePage(resp.getWriter(), lastName, lastNameError, firstName, firstNameError);
        }
    }

    private String validateName(String lastName, String fieldName) {
        String errorMessage = null;
        if (lastName.equals("")) {
            errorMessage = fieldName + " must be given";
        } else if (containsIllegalCharacters(lastName)) {
            errorMessage = fieldName + " contains illegal characters";
        }
        return errorMessage;
    }

    private void showCreatePage(PrintWriter writer, String lastName, String validationError, String firstName, String firstNameError) {
        writer.append("<html>");
        writer.append("<head><style>#error { color: red; }</style></head>");

        if (validationError != null) {
            writer.append("<div id='error'>").append(validationError).append("</div>");
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

    private String htmlEscape(String lastName) {
        return lastName.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private void showSearchPage(PrintWriter writer, String nameQuery, List<Person> people) {
        if (nameQuery == null) nameQuery = "";
        writer //
            .append("<html>") //
            .append("<form method='get' action='findPeople.html'>") //
            .append("<input type='text' name='name_query' value='" + nameQuery + "'/>") //
            .append("<input type='submit' name='findPeople' value='Find people'/>") //
            .append("</form>");

        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>").append(person.getFullName()).append("</li>");
        }
        writer //
            .append("</ul>") //
            .append("</html>") //
            ;
    }

    private boolean containsIllegalCharacters(String lastName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (lastName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }
}
