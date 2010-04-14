package com.brodwall.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 6628439558603357450L;
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
		if (req.getPathInfo().equals("/create.html")) {
			showCreatePage(out, "", null);
		} else {
			String nameQuery = req.getParameter("name_query");
			List<Person> people = new ArrayList<Person>();
			if (nameQuery != null) {
				people = personDao.findPeople(nameQuery);
			}
			showFindPage(out, nameQuery != null ? nameQuery : "", people);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String fullName = req.getParameter("full_name");
		String errorMessage = validateName(fullName);
		
		if (errorMessage == null) {
			personDao.createPerson(Person.withName(fullName));
			resp.sendRedirect("/");
		} else {
			showCreatePage(resp.getWriter(), fullName, errorMessage);
		}
	}

	private String validateName(String fullName) {
		if (fullName.equals("")) {
			return "Name must be provided";
		} else if (fullName.length() > 30) {
			return "Name cannot be more than 30 characters";
		}
		return null;
	}

	private void showCreatePage(PrintWriter out, String name, String errorMessage) {
		out.println("<html>");
		if (errorMessage != null) {
			out.println("<div id='errorMessage'>" + errorMessage + "</div>");
		}
		
		out.println("<form method='post' action='create.html'>");
		out.println("<input type='text' name='full_name' value='" + name + "'/>");
		out.println("<input type='submit' name='create' value='Create'/>");
		out.println("</form>");
		out.println("</html>");
	}

	private void showFindPage(PrintWriter out, String nameQuery, List<Person> people) {
		out.println("<html>");
		showSearchForm(out, nameQuery);
		showSearchResults(out, people);
		out.println("</html>");
	}

	private void showSearchForm(PrintWriter out, String nameQuery) {
		out.println("<form method='get' action='find.html'>");
		out.println("<input type='text' name='name_query' value='" + nameQuery + "'/>");
		out.println("<input type='submit' name='find' value='Find'/>");
		out.println("</form>");
	}
	
	private void showSearchResults(PrintWriter out, List<Person> people) {
		out.write("<ul>");
		for (Person person : people) {
			out.write("<li>" + person.getName() + "</li>");
		}
		out.write("</ul>");
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	@Override
	public void init() throws ServletException {
		setPersonDao(new HibernatePersonDao("jdbc/personDs"));
	}

}
