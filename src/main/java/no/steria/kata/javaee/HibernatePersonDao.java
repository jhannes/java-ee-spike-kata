package no.steria.kata.javaee;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.context.ThreadLocalSessionContext;

public class HibernatePersonDao implements PersonDao {

    public class HibernateTransactionResource implements Transaction {

        private boolean commit = false;

        @Override
        public void setCommit() {
            this.commit = true;
        }

        @Override
        public void close() {
            if (commit) {
                getSession().getTransaction().commit();
            } else {
                getSession().getTransaction().rollback();
            }
        }
    }

    private SessionFactory sessionFactory;

    public HibernatePersonDao(String jndiDataSource) {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg.setProperty(Environment.DATASOURCE, jndiDataSource);
        cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, ThreadLocalSessionContext.class.getName());
        cfg.addAnnotatedClass(Person.class);
        this.sessionFactory = cfg.buildSessionFactory();
    }

    @Override
    public Transaction beginTransaction() {
        getSession().beginTransaction();
        return new HibernateTransactionResource();
    }

    @Override
    public void createPerson(Person person) {
        getSession().save(person);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Person> findPeople(String nameQuery) {
        Criteria criteria = getSession().createCriteria(Person.class);
        return criteria.list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
