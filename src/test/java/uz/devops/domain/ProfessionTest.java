package uz.devops.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class ProfessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profession.class);
        Profession profession1 = new Profession();
        profession1.setName("id1");
        Profession profession2 = new Profession();
        profession2.setName(profession1.getName());
        assertThat(profession1).isEqualTo(profession2);
        profession2.setName("id2");
        assertThat(profession1).isNotEqualTo(profession2);
        profession1.setName(null);
        assertThat(profession1).isNotEqualTo(profession2);
    }
}
