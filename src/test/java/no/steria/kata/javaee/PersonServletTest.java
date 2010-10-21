package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PersonServletTest {

    private static final boolean COMMIT = true;
    private PersonServlet personServlet = new PersonServlet();
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private PersonDao personDao = mock(PersonDao.class);
    private StringWriter htmlSource = new StringWriter();

    @Test
    public void shouldDisplayCreateForm() throws Exception {
        getRequest("/createPerson.html");
        personServlet.service(req, resp);

        verify(resp).setContentType("text/html");

        assertThat(htmlSource.toString()) //
                .contains("<form action='createPerson.html' method='post'") //
                .contains("<input type='text' name='full_name' value=''") //
                .contains("<input type='submit' name='createPerson' value='Create person'") //
                .excludes("<div class='error'")
        ;
    }

    @Test
    public void shouldDisplaySearchPage() throws Exception {
        getRequest("/findPeople.html");
        personServlet.service(req, resp);

        verify(resp).setContentType("text/html");

        assertThat(htmlSource.toString()) //
                .contains("<form action='findPeople.html' method='get'") //
                .contains("<input type='text' name='name_query' value=''") //
                .contains("<input type='submit' name='findPeople' value='Find people'") //
        ;
    }


    @After
    public void verifyCorrectHtml() throws DocumentException {
        if (htmlSource.toString().isEmpty()) return;
        DocumentHelper.parseText(htmlSource.toString());
    }

    protected void getRequest(String pathInfo) {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn(pathInfo);
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("Darth Vader");

        personServlet.service(req, resp);

        InOrder order = inOrder(personDao);
        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("Darth Vader"));
        order.verify(personDao).endTransaction(COMMIT);

        verify(resp).sendRedirect("/");
    }

    @Test
    public void shouldValidateEmptyPerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("");


        personServlet.service(req, resp);

        verify(personDao, never()).createPerson(Person.withName(""));

        assertThat(htmlSource.toString()) //
        .contains("<form action='createPerson.html' method='post'") //
        .contains("<input type='text' name='full_name' value=''") //
        .contains("<input type='submit' name='createPerson' value='Create person'") //
        .contains("<div class='error'> Invalid input</div>") //
;


    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("darth");

        personServlet.service(req, resp);

        verify(personDao).findPeople("darth");
    }

    @Test
    public void shouldEchoSearchString() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("darth");

        personServlet.service(req, resp);

        assertThat(htmlSource.toString()) //
            .contains("<input type='text' name='name_query' value='darth'");
    }

    @Test
    public void shouldDisplaySearchResult() throws Exception {
        getRequest("/findPeople.html");

        List<Person> people = Arrays.asList(Person.withName("Anakin"), Person.withName("Darth"));
        when(personDao.findPeople(anyString())).thenReturn(people);

        personServlet.service(req, resp);

        assertThat(htmlSource.toString()) //
                .contains("<ul") //
                .contains("<li>Anakin") //
                .contains("<li>Darth") //
        ;
    }

    @Before
    public void setUpServlet() throws IOException {
        personServlet.setPersonDao(personDao);
        when(resp.getWriter()).thenReturn(new PrintWriter(htmlSource));
    }

}
