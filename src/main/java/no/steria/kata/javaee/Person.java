package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public static Person withName(String fullName) {
        Person person = new Person();
        person.name = fullName;
        return person;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person<" + getName() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return nullSafeEquals(name, ((Person) obj).name);
    }

    protected <T> boolean nullSafeEquals(T a, T b) {
        return a != null ? a.equals(b) : b == null;
    }
}
