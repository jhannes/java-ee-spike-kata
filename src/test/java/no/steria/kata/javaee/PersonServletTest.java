package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PersonServletTest {

    private PersonServlet personServlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private PersonDao personDao = mock(PersonDao.class);
    private StringWriter html = new StringWriter();

    @Test
    public void shouldDisplayCreateForm() throws Exception {
        when(req.getMethod()).thenReturn("GET");

        personServlet.service(req, resp);

        verify(resp).setContentType("text/html");

        assertThat(html.toString())//
            .contains("<form method='post' action='create.html'")
            .contains("<input type='text' name='the_name' value=''")
            .contains("<input type='submit' name='create' value='Create Person'");

        assertValidHtml();
    }

    private void assertValidHtml() throws DocumentException {
        DocumentHelper.parseText(html.toString());
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("the_name")).thenReturn("John Doe");

        personServlet.service(req, resp);

        InOrder order = inOrder(personDao);
        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("John Doe"));
        order.verify(personDao).endTransaction(true);

        verify(resp).sendRedirect("/");
    }

    @Test
    public void shouldDisplaySearchForm() throws Exception {
        httpRequest("GET", "/find.html");

        personServlet.service(req, resp);

        assertThat(html.toString())//
            .contains("<form method='GET' action='find.html'")
            .contains("<input type='text' name='name_query' value=''")
            .contains("<input type='submit' name='find' value='Find People'")
            ;
        assertValidHtml();
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("Jack Daniels");

        personServlet.service(req, resp);
        verify(personDao).findPeople("Jack Daniels");
    }

    @Test
    public void shouldDisplayFoundPeople() throws Exception {
        httpRequest("GET", "/find.html");

        List<Person> people = Arrays.asList(Person.withName("Jack"), Person.withName("Jill"));
        when(personDao.findPeople(anyString())).thenReturn(people );

        personServlet.service(req, resp);

        assertThat(html.toString()) //
            .contains("<ul>")//
            .contains("<li>Jack</li>")
            .contains("<li>Jill</li>");
    }

    @Test
    public void shouldEchoSearchParameter() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("Jack Daniels");
        personServlet.service(req, resp);

        assertThat(html.toString())//
            .contains("value='Jack Daniels'");

    }

    private void httpRequest(String method, String path) {
        when(req.getMethod()).thenReturn(method);
        when(req.getPathInfo()).thenReturn(path);
    }

    @Before
    public void setUpServlet() throws IOException {
        personServlet.setPersonDao(personDao);
        when(resp.getWriter()).thenReturn(new PrintWriter(html));
    }
}
