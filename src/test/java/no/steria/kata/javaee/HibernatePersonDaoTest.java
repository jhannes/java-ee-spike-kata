package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Before;
import org.junit.Test;

public class HibernatePersonDaoTest {

    private PersonDao personDao;

    @Test
    public void shouldFindCreatedPeople() throws Exception {
        personDao.beginTransaction();
        Person person = Person.withName("Darth", "Vader");
        personDao.storePerson(person);
        assertThat(personDao.findPeople(null)).contains(person);
    }

    @Test
    public void shouldLimitFindToQuery() throws Exception {
        personDao.beginTransaction();
        Person matchingPerson = Person.withName("Darth", "Darth Vader");
        Person nonMatchingPerson = Person.withName("Darth", "Anakin");
        personDao.storePerson(matchingPerson);
        personDao.storePerson(nonMatchingPerson);

        assertThat(personDao.findPeople("vader"))
            .contains(matchingPerson)
            .excludes(nonMatchingPerson);
    }

    @Test
    public void shouldCommitOrRollback() throws Exception {
        personDao.beginTransaction();
        Person commitedPerson = Person.withName("Darth", "Vader");
        personDao.storePerson(commitedPerson);
        personDao.endTransaction(true);

        personDao.beginTransaction();
        Person uncommitedPerson = Person.withName("Darth", "Jar Jar Binks");
        personDao.storePerson(uncommitedPerson);
        personDao.endTransaction(false);

        personDao.beginTransaction();
        assertThat(personDao.findPeople(null))
            .contains(commitedPerson)
            .excludes(uncommitedPerson);
    }

    @Before
    public void setupPersonDao() throws NamingException {
        personDao = createPersonDao();
    }

    private PersonDao createPersonDao() throws NamingException {
        String jndiDataSource = "jdbc/testDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:test");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        return new HibernatePersonDao(jndiDataSource);
    }

}
