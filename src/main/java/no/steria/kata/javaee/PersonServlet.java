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
            new PersonForm().showForm(writer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonForm personForm = new PersonForm();
        personForm.setFirstName(req.getParameter("first_name"));
        personForm.setLastName(req.getParameter("last_name"));
        if (!personForm.hasErrors()) {
            personDao.createPerson(personForm.createPerson());
            resp.sendRedirect("/");
        } else {
            resp.setContentType("text/html");
            personForm.showForm(resp.getWriter());
        }
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

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }
}
