package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PersonDao personDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if ("/find.html".equals(req.getPathInfo())) {
            String nameQuery = req.getParameter("name_query");
            showFindView(writer, nameQuery != null ? nameQuery : "",
                    personDao.findPeople(nameQuery));
        } else {
            showCreateView(writer);
        }
    }

    private void showCreateView(PrintWriter writer) {
        writer.append("<form method='post' action='create.html'>")//
        .append("<input type='text' name='the_name' value=''/>")
        .append("<input type='submit' name='create' value='Create Person'/>")
        .append("</form>");
    }

    private void showFindView(PrintWriter writer, String nameQuery, List<Person> people) {
        writer.append("<html>");
        showFindForm(writer, nameQuery);
        showSearchResults(writer, people);
        writer.append("</html>");
    }

    private void showSearchResults(PrintWriter writer, List<Person> people) {
        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>" + person.getName() + "</li>");
        }
        writer.append("</ul>");
    }

    private void showFindForm(PrintWriter writer, String nameQuery) {
        writer.append("<form method='GET' action='find.html'>")
            .append("<input type='text' name='name_query' value='" + nameQuery + "'/>")
            .append("<input type='submit' name='find' value='Find People'/>")
            .append("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.createPerson(Person.withName(req.getParameter("the_name")));
        resp.sendRedirect("/");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.beginTransaction();
        try {
            super.service(req, resp);
        } finally {
            personDao.endTransaction(true);
        }
    }
    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
