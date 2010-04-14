package com.brodwall.kata.javaee;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernatePersonDao implements PersonDao {

	private SessionFactory sessionFactory;

	public HibernatePersonDao(String jndiDataSource) {
		AnnotationConfiguration cfg = new AnnotationConfiguration()
			.setProperty(Environment.DATASOURCE, jndiDataSource)
			.setProperty(Environment.HBM2DDL_AUTO, "update")
			.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread")
			;
		cfg.addAnnotatedClass(Person.class);
		sessionFactory = cfg.buildSessionFactory();
	}

	@Override
	public void beginTransaction() {
		getSession().beginTransaction();
	}
	
	@Override
	public void endTransaction(boolean commit) {
		if (commit) {
			getSession().getTransaction().commit();
		} else {
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public void createPerson(Person person) {
		getSession().save(person);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> findPeople(String nameQuery) {
		Criteria criteria = getSession().createCriteria(Person.class);
		if (nameQuery != null) criteria.add(Restrictions.ilike("name", nameQuery, MatchMode.ANYWHERE));
		return criteria.list();
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
