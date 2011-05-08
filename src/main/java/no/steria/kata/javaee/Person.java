package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    @SuppressWarnings("unused")
    @Id @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    public static Person withName(String firstName, String lastName) {
        Person person = new Person();
        person.firstName = firstName;
        person.lastName = lastName;
        return person;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return nullSafeEquals(firstName, ((Person)obj).firstName)
                && nullSafeEquals(lastName, ((Person)obj).lastName);
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
