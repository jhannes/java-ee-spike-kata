package com.brodwall.kata.javaee;

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

    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse res = mock(HttpServletResponse.class);
    private PersonServlet personServlet = new PersonServlet();
    private StringWriter html = new StringWriter();
    private PersonDao personDao = mock(PersonDao.class);

    @Test
    public void shouldDisplayCreateForm() throws Exception {
        when(req.getMethod()).thenReturn("GET");

        personServlet.service(req, res);

        verify(res).setContentType("text/html");

        assertThat(html.toString()).contains("<form method='POST' action='create.html'").contains("<input type='text' name='full_name' value=''")
                .contains("<input type='submit' name='create' value='Create her/him'");

        verifyHtml();
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("Ali Baba");


        personServlet.service(req, res);

        verify(res).sendRedirect("/");

        InOrder order = inOrder(personDao);

        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("Ali Baba"));
        order.verify(personDao).endTransaction(true);
    }

    @Test
    public void shouldDisplaySearhForm() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("/find.html");

        personServlet.service(req, res);

        assertThat(html.toString()).contains("<form method='GET' action='find.html'").contains("<input type='text' name='name_query' value=''")
                .contains("<input type='submit' name='find' value='Search Now!'");

        verifyHtml();
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("/find.html");
        when(req.getParameter("name_query")).thenReturn("bab");

        personServlet.service(req, res);

        verify(personDao).findPeople("bab");
    }

    @Test
    public void shouldDisplaySearchResults() throws Exception {
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("/find.html");

        List<Person> people = Arrays.asList(Person.withName("Ali Baba"), Person.withName("Aladdin"));
        when(personDao.findPeople(anyString())).thenReturn(people);

        personServlet.service(req, res);

        assertThat(html.toString())
            .contains("<ul>")
            .contains("<li>Ali Baba</li>")
            .contains("<li>Aladdin</li>")
            .contains("</ul>");
    }


    @Before
    public void setupServlet() throws IOException {
        personServlet.setPersonDao(personDao);
        when(res.getWriter()).thenReturn(new PrintWriter(html));
    }

    private void verifyHtml() throws DocumentException {
        DocumentHelper.parseText(html.toString());
    }
}
