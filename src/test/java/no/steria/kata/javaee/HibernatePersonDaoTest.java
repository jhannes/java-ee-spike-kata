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
        try (Transaction transaction = personDao.beginTransaction()) {
            Person person = Person.withName("Vader");
            personDao.createPerson(person);
            assertThat(personDao.findPeople(null)).contains(person);
        }
    }

    @Test
    public void shouldLimitFindToQuery() throws Exception {
    }

    @Test
    public void shouldCommitOrRollback() throws Exception {
        Person commitedPerson = Person.withName("Vader");
        Person uncommitedPerson = Person.withName("Jar Jar Binks");

        try (Transaction transaction = personDao.beginTransaction()) {
            personDao.createPerson(commitedPerson);
            transaction.setCommit();
        }

        try (Transaction transaction = personDao.beginTransaction()) {
            personDao.createPerson(uncommitedPerson);
        }

        try (Transaction transaction = personDao.beginTransaction()) {
            assertThat(personDao.findPeople(null))
                .contains(commitedPerson)
                .excludes(uncommitedPerson);
        }
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
