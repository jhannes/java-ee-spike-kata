package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    @SuppressWarnings("unused")
    @Id @GeneratedValue
    private Long id;

    private String fullName;

    public static Person withName(String fullName) {
        Person person = new Person();
        person.fullName = fullName;
        return person;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return nullSafeEquals(fullName, ((Person)obj).fullName);
    }

    @Override
    public int hashCode() {
        return getFullName() != null ? getFullName().hashCode() : -1;
    }

    private boolean nullSafeEquals(String a, String b) {
        return a != null ? a.equals(b) : b == null;
    }

    @Override
    public String toString() {
        return "Person<" + getFullName() + ">";
    }

}
