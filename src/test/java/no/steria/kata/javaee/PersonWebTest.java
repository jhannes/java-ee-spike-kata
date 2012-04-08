package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PersonWebTest {

    private static HibernatePersonDao personDao;
    private static int localPort;
    private WebDriver browser = createWebDriver("http://localhost:" + localPort + "/");

    @Test
    public void shouldFindSavedPerson() throws Exception {
        browser.findElement(By.linkText("Create person")).click();
        browser.findElement(By.name("full_name")).sendKeys("Darth Vader");
        browser.findElement(By.name("createPerson")).click();

        try (Transaction tx = personDao.beginTransaction())  {
            assertThat(personDao.findPeople(null)).contains(Person.withName("Darth Vader"));
        }
    }

    @Test
    public void shouldOnlyFindMatchingPeople() throws Exception {
    }

    @BeforeClass
    public static void startWebserver() throws NamingException, Exception {
        String jndiDataSource = "jdbc/personDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:webtest");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        personDao = new HibernatePersonDao(jndiDataSource);

        Server server = new Server(0);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        localPort = server.getConnectors()[0].getLocalPort();
    }

    private HtmlUnitDriver createWebDriver(String url) {
        HtmlUnitDriver browser = new HtmlUnitDriver();
        browser.get(url);
        return browser;
    }

}
