package com.brodwall.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 6628439558603357450L;
    private PersonDao personDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        PrintWriter writer = resp.getWriter();
        writer.println("<html>");
        if ("/find.html".equals(req.getPathInfo())) {
            List<Person> people = personDao.findPeople(req.getParameter("name_query"));
            showSearchView(writer, people);
        } else {
            showCreateView(writer);
        }
        writer.println("</html>");
    }

    private void showCreateView(PrintWriter writer) {
        writer.println("<form method='POST' action='create.html'>");
        writer.println("<input type='text' name='full_name' value='' />");
        writer.println("<input type='submit' name='create' value='Create her/him' />");
        writer.println("</form>");
    }

    private void showSearchView(PrintWriter writer, List<Person> people) {
        showSearchForm(writer);

        showSearchResulrt(writer, people);
    }

    private void showSearchResulrt(PrintWriter writer, List<Person> people) {
        writer.println("<ul>");
        for (Person person : people) {
            writer.println("<li>" + person.getName() + "</li>");
        }
        writer.println("</ul>");
    }

    private void showSearchForm(PrintWriter writer) {
        writer.println("<form method='GET' action='find.html'>");
        writer.println("<input type='text' name='name_query' value='' />");
        writer.println("<input type='submit' name='find' value='Search Now!' />");
        writer.println("</form>");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.beginTransaction();
        super.service(req, resp);
        personDao.endTransaction(true);
    }

    @Override
    public void init() throws ServletException {
        personDao = new HibernatePersonDao("jdbc/personDs");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.createPerson(Person.withName(req.getParameter("full_name")));
        resp.sendRedirect("/");
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
