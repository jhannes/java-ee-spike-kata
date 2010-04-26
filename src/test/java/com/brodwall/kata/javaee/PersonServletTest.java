package com.brodwall.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

public class PersonServletTest {

	private PersonServlet personServlet = new PersonServlet();
	private HttpServletRequest req = mock(HttpServletRequest.class);
	private HttpServletResponse resp = mock(HttpServletResponse.class);
	private StringWriter pageSource = new StringWriter();
	private PersonDao personDao = mock(PersonDao.class);

	@Before
	public void setupServlet() throws IOException {
		when(resp.getWriter()).thenReturn(new PrintWriter(pageSource));
		personServlet.setPersonDao(personDao);
	}

	@Test
	public void shouldDisplayCreateForm() throws Exception {
		httpRequest("GET", "/create.html");
		personServlet.service(req, resp);

		assertThat(pageSource.toString())
			.contains("<form method='post' action='create.html'")
			.contains("<input type='text' name='full_name' value=''")
			.contains("<input type='submit' name='create' value='Create person'");
		verify(resp).setContentType("text/html");
		assertValidHtml();
	}

	@Test
	public void shouldCreatePerson() throws Exception {
		httpRequest("POST", "/create.html");
		when(req.getParameter("full_name")).thenReturn("Johannes Brodwall");

		personServlet.service(req, resp);

		verify(personDao).createPerson(Person.withName("Johannes Brodwall"));
		verify(resp).sendRedirect(anyString());
	}

	@Test
	public void shouldValidateThatNameIsPresent() throws Exception {
		httpRequest("POST", "/create.html");
		when(req.getParameter("full_name")).thenReturn("");

		personServlet.service(req, resp);

		verify(personDao, never()).createPerson(any(Person.class));
		assertThat(pageSource.toString())
			.contains("<form")
			.contains("<div id='errorMessage'>Name must be present</div>");
		assertValidHtml();
	}

	@Test
	public void shouldValidateNameIsShortEnough() throws Exception {
		httpRequest("POST", "/create.html");
		String tooLongName = "1234567890123456789012345678901";
		when(req.getParameter("full_name")).thenReturn(tooLongName);

		personServlet.service(req, resp);

		verify(personDao, never()).createPerson(any(Person.class));
		assertThat(pageSource.toString())
			.contains("<form")
			.contains("<div id='errorMessage'>Name must be 30 characters or less</div>")
			.contains("<input type='text' name='full_name' value='" + tooLongName + "'");
		assertValidHtml();
	}

	@Test
	public void shouldDisplaySearchPage() throws Exception {
		httpRequest("GET", "/find.html");

		personServlet.service(req, resp);

		assertThat(pageSource.toString())
			.contains("<form method='get' action='find.html'")
			.contains("<input type='text' name='name_query' value=''")
			.contains("<input type='submit' name='find' value='Find people'");
		assertValidHtml();
	}

	@Test
	public void shouldDisplaySearchResult() throws Exception {
		httpRequest("GET", "/find.html");

		List<Person> people = Arrays.asList(Person.withName("Johannes Brodwall"), Person.withName("Ada Brodwall"));
		when(personDao.findPeople(anyString())).thenReturn(people);

		personServlet.service(req, resp);

		assertThat(pageSource.toString())
			.contains("<li>Johannes Brodwall</li>")
			.contains("<li>Ada Brodwall</li>");
		assertValidHtml();
	}

	@Test
	public void shouldEchoSearchQuery() throws Exception {
		httpRequest("GET", "/find.html");
		when(req.getParameter("name_query")).thenReturn("brodwa");

		personServlet.service(req, resp);

		assertThat(pageSource.toString())
			.contains("<form")
			.contains("<input type='text' name='name_query' value='brodwa'");
	}

	@Test
	public void shouldSearchForParameter() throws Exception {
		httpRequest("GET", "/find.html");
		when(req.getParameter("name_query")).thenReturn("brodwa");

		personServlet.service(req, resp);

		verify(personDao).findPeople("brodwa");
	}

	private Document assertValidHtml() throws DocumentException {
		return DocumentHelper.parseText(pageSource.toString());
	}

	private void httpRequest(String method, String pathInfo) {
		when(req.getMethod()).thenReturn(method);
		when(req.getPathInfo()).thenReturn(pathInfo);
	}

}
