package com.brodwall.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class PersonServlet extends HttpServlet {

	private PersonDao personDao;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");

		PrintWriter out = resp.getWriter();
		if ("/find.html".equals(req.getPathInfo())) {
			String nameQuery = req.getParameter("name_query");
			showSearchPage(out, nameQuery != null ? nameQuery : "", personDao.findPeople(nameQuery));
		} else {
			showCreatePage(out, "", null);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String fullName = req.getParameter("full_name");
		String errorMessage = validateName(fullName);

		if (errorMessage != null) {
			showCreatePage(resp.getWriter(), fullName, errorMessage);
			return;
		}

		personDao.createPerson(Person.withName(fullName));
		resp.sendRedirect("/");
	}

	private String validateName(String fullName) {
		if (fullName.equals("")) 	return "Name must be present";
		if (fullName.length() > 30) return "Name must be 30 characters or less";

		return null;
	}

	private void showCreatePage(PrintWriter out, String fullName, String errorMessage) {
		out.println("<html>");
		if (errorMessage != null) {
			out.println("<div id='errorMessage'>" + errorMessage + "</div>");
		}
		out.println("<form method='post' action='create.html'>");
		out.println("<input type='text' name='full_name' value='" + fullName + "'/>");
		out.println("<input type='submit' name='create' value='Create person'/>");
		out.println("</form>");
		out.println("</html>");
	}

	private void showSearchPage(PrintWriter out, String nameQuery, List<Person> people) {
		out.println("<html>");
		showSearchForm(out, nameQuery);
		showSearchResults(out, people);
		out.println("</html>");
	}

	private void showSearchResults(PrintWriter out, List<Person> people) {
		out.println("<ul>");
		for (Person person : people) {
			out.println("<li>" + person.getName() + "</li>");
		}
		out.println("</ul>");
	}

	private void showSearchForm(PrintWriter out, String nameQuery) {
		out.println("<form method='get' action='find.html'>");
		out.println("<input type='text' name='name_query' value='" + nameQuery + "'/>");
		out.println("<input type='submit' name='find' value='Find people'/>");
		out.println("</form>");
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	@Override
	public void init() throws ServletException {
		setPersonDao(new HibernatePersonDao("jdbc/personDs"));
	}

}
