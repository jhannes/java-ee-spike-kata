package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.fest.assertions.StringAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersonServletTest {

    private PersonServlet servlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private StringWriter htmlSource = new StringWriter();
    private PersonDao personDao = mock(PersonDao.class);

    @Test
    public void shouldDisplayCreatePage() throws Exception {
        getRequest("/createPerson.html");
        servlet.service(req, resp);

        assertThat(htmlSource.toString())
            .contains("<form method='post' action='createPerson.html'")
            .contains("<input type='text' name='first_name' value=''")
            .contains("<input type='text' name='last_name' value=''")
            .contains("<input type='submit' name='createPerson' value='Create person'");
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("first_name")).thenReturn("Anakin");
        when(req.getParameter("last_name")).thenReturn("Vader");
        servlet.service(req, resp);

        verify(personDao).storePerson(Person.withName("Anakin", "Vader"));
    }

    @Test
    public void shouldRejectFirstNameWithHtmlCharacters() throws Exception {
        when(req.getParameter("first_name")).thenReturn("<&>");
        assertValidationError("First name contains illegal characters")
            .contains("name='first_name' value='&lt;&amp;&gt;'");
    }

    @Test
    public void shouldValidateFirstNameIsGiven() throws Exception {
        when(req.getParameter("first_name")).thenReturn("");
        assertValidationError("First name must be given");
    }

    @Test
    public void shouldRejectLastNameWithHtmlCharacters() throws Exception {
        when(req.getParameter("last_name")).thenReturn("<&>");
        assertValidationError("Last name contains illegal characters")
        .contains("name='last_name' value='&lt;&amp;&gt;'");
    }
    
    @Test
    public void shouldValidateLastNameIsGiven() throws Exception {
        when(req.getParameter("last_name")).thenReturn("");
        assertValidationError("Last name must be given");
    }
    
    private StringAssert assertValidationError(String expectedError) throws ServletException, IOException {
        when(req.getMethod()).thenReturn("POST");

        servlet.service(req, resp);
        verify(personDao, never()).storePerson(any(Person.class));

        return assertThat(htmlSource.toString())
            .contains("<form ")
            .contains("<div id='error'>" + expectedError + "</div>");
    }

    @Test
    public void shouldRollbackOnError() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("first_name")).thenReturn("Darth");
        when(req.getParameter("last_name")).thenReturn("Vader");
        RuntimeException thrown = new RuntimeException();
        doThrow(thrown)
            .when(personDao).storePerson(any(Person.class));

        try {
            servlet.service(req, resp);
        } catch (RuntimeException caught) {
            assertThat(caught).isEqualTo(thrown);
        }

        verify(personDao).endTransaction(false);
    }

    @Test
    public void shouldDisplayFindPage() throws Exception {
        getRequest("/findPeople.html");
        servlet.service(req, resp);

        assertThat(htmlSource.toString())
            .contains("<form method='get' action='findPeople.html'")
            .contains("<input type='text' name='name_query' value=''")
            .contains("<input type='submit' name='findPeople' value='Find people'");
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("vader");

        servlet.service(req, resp);

        verify(personDao).findPeople("vader");
    }

    @Test
    public void shouldDisplaySearchResult() throws Exception {
        List<Person> people = Arrays.asList(Person.withName("Darth", "Darth Vader"), Person.withName("Darth", "Luke Skywalker"));
        when(personDao.findPeople(anyString())).thenReturn(people);
        getRequest("/findPeople.html");

        servlet.service(req, resp);

        assertThat(htmlSource.toString())
            .contains("Darth Vader")
            .contains("Luke Skywalker");
    }

    @Test
    public void shouldEchoNameQuery() throws Exception {
        getRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("vader");

        servlet.service(req, resp);

        assertThat(htmlSource.toString())
            .contains("name='name_query' value='vader'");
    }

    private void getRequest(String pathInfo) {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn(pathInfo);
    }

    @Before
    public void setupServlet() throws IOException {
        when(resp.getWriter()).thenReturn(new PrintWriter(htmlSource));
        servlet.setPersonDao(personDao);
    }

    @After
    public void shouldBeValidHtml() throws DocumentException {
        if (htmlSource.toString().length() > 0) {
            verify(resp).setContentType("text/html");
            DocumentHelper.parseText(htmlSource.toString());
        }
    }
}
