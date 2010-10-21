package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private PersonDao personDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        if (req.getPathInfo().equals("/createPerson.html")) {
            showCreateForm(resp, false);
        } else {
            List<Person> people = personDao.findPeople(req.getParameter("name_query"));
            showFindForm(resp, people, req.getParameter("name_query"));
        }
    }

    protected void showFindForm(HttpServletResponse resp, List<Person> people, String searchString) throws IOException {
        if (searchString == null) searchString="";
        PrintWriter writer = resp.getWriter();
        writer //
                .append("<html><body>")
                .append("<form action='findPeople.html' method='get'>") //
                .append("<input type='text' name='name_query' value='" + searchString +"'/>") //
                .append("<input type='submit' name='findPeople' value='Find people' /> ") //
                .append("</form>") //
        ;
        writer.append("<ul>");

        for (Person person : people) {
            writer.append("<li>" + person.getName() + "</li>");
        }
        writer.append("</ul></body></html>");
    }

    protected void showCreateForm(HttpServletResponse resp, boolean hasError) throws IOException {
        resp.getWriter() //
                .append("<html><body>")
                .append(hasError ? "<div class='error'> Invalid input</div>" : "")
                .append("<form action='createPerson.html' method='post'>") //
                .append("<input type='text' name='full_name' value=''/>") //
                .append("<input type='submit' name='createPerson' value='Create person' /> ") //
                .append("</form>") //
                .append("</body></html>")
        ;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("full_name");
        if(!fullName.isEmpty()){
            personDao.createPerson(Person.withName(fullName));
            resp.sendRedirect("/");
        } else {
            showCreateForm(resp, true);
        }

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        personDao.beginTransaction();
        super.service(req, resp);
        personDao.endTransaction(true);
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
