package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

    @Test
    public void shouldGetName() throws Exception {
        assertThat(Person.withName("Darth", "Vader").getFullName()).isEqualTo("Darth Vader");
    }

    @Test
    public void shouldBeEqualWhenNameIsEqual() throws Exception {
        assertThat(Person.withName("Darth", "Vader"))
            .isEqualTo(Person.withName("Darth", "Vader"))
            .isNotEqualTo(Person.withName("Darth", "Anakin"))
            .isNotEqualTo(Person.withName("Darth", null))
            .isNotEqualTo(new Object())
            .isNotEqualTo(null);

        assertThat(Person.withName("Darth", null))
            .isEqualTo(Person.withName("Darth", null))
            .isNotEqualTo(Person.withName("Darth", "Vader"));
    }

    @Test
    public void shouldBaseHashcodeOnName() throws Exception {
        assertThat(Person.withName("Darth", "Vader").hashCode()).as("hashCode")
            .isEqualTo(Person.withName("Darth", "Vader").hashCode())
            .isNotEqualTo(Person.withName("Darth", "Anakin").hashCode())
            .isNotEqualTo(Person.withName("Darth", null).hashCode());
    }

}
