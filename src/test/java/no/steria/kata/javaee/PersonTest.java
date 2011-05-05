package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {

    @Test
    public void shouldGetName() throws Exception {
        assertThat(Person.withName("Vader").getFullName()).isEqualTo("Vader");
    }

    @Test
    public void shouldBeEqualWhenNameIsEqual() throws Exception {
        assertThat(Person.withName("Vader"))
            .isEqualTo(Person.withName("Vader"))
            .isNotEqualTo(Person.withName("Anakin"))
            .isNotEqualTo(Person.withName(null))
            .isNotEqualTo(new Object())
            .isNotEqualTo(null);

        assertThat(Person.withName(null))
            .isEqualTo(Person.withName(null))
            .isNotEqualTo(Person.withName("Vader"));
    }

    @Test
    public void shouldBaseHashcodeOnName() throws Exception {
        assertThat(Person.withName("Vader").hashCode()).as("hashCode")
            .isEqualTo(Person.withName("Vader").hashCode())
            .isNotEqualTo(Person.withName("Anakin").hashCode())
            .isNotEqualTo(Person.withName(null).hashCode());
    }

}
