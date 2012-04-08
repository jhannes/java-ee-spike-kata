package no.steria.kata.javaee;

import java.util.List;

public interface PersonDao {

    void createPerson(Person person);

    List<Person> findPeople(String nameQuery);

    Transaction beginTransaction();

}
