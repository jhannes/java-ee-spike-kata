package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;

public class HibernatePersonDaoTest {

    @Test
    public void shouldFindCreatedPerson() throws Exception {
        PersonDao personDao = createPersonDao();
        personDao.beginTransaction();
        Person darth = Person.withName("Darth");
        personDao.createPerson(darth);
        assertThat(personDao.findPeople(null)).containsOnly(darth);
    }

    @Test
    public void testSearch() throws Exception {
        PersonDao personDao = createPersonDao();
        personDao.beginTransaction();
        Person darth = Person.withName("Darth Vader");
        Person yoda = Person.withName("Yoda");

        personDao.createPerson(darth);
        personDao.createPerson(yoda);
        assertThat(personDao.findPeople("arth vader")).containsOnly(darth);
    }

    private PersonDao createPersonDao() throws NamingException {
        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:test");
        dataSource.setUser("sa");
        new EnvEntry("jdbc/testDao", dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        return new HibernatePersonDao("jdbc/testDao");
    }
}
