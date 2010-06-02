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
        try {
            super.service(req, resp);
        } finally {
            personDao.endTransaction(true);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        PrintWriter writer = resp.getWriter();
        if ("/find.html".equals(req.getPathInfo())) {
            String nameQuery = req.getParameter("name_query");
            showSearchPage(writer, nameQuery != null ? nameQuery : "",
                    personDao.findPeople(nameQuery));
        } else {
            showCreatePage(writer);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.createPerson(Person.withName(req.getParameter("full_name")));
        resp.sendRedirect("/");
    }

    private void showCreatePage(PrintWriter writer) {
        writer.println("<form method='post' action='create.html'>");
        writer.println("<input type='text' name='full_name' value='' />");
        writer.println("<input type='submit' name='create' value='Create Person' />");
        writer.println("</form>");
    }

    private void showSearchPage(PrintWriter writer, String nameQuery, List<Person> people) {
        writer.println("<html>");
        showSearchForm(writer, nameQuery);
        showSearchResults(writer, people);
        writer.println("</html>");
    }

    private void showSearchResults(PrintWriter writer, List<Person> people) {
        writer.println("<ul>");
        for (Person person : people) {
            writer.println("<li>" + person.getName() + "</li>");
        }
        writer.println("</ul>");
    }

    private void showSearchForm(PrintWriter writer, String nameQuery) {
        writer.println("<form method='get' action='find.html'>");
        writer.println("<input type='text' name='name_query' value='" + nameQuery + "' />");
        writer.println("<input type='submit' name='find' value='Find Person' />");
        writer.println("</form>");
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }
}
