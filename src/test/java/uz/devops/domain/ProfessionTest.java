package uz.devops.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class ProfessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profession.class);
        Profession profession1 = new Profession();
        profession1.setId(1L);
        Profession profession2 = new Profession();
        profession2.setId(profession1.getId());
        assertThat(profession1).isEqualTo(profession2);
        profession2.setId(2L);
        assertThat(profession1).isNotEqualTo(profession2);
        profession1.setId(null);
        assertThat(profession1).isNotEqualTo(profession2);
    }
}
