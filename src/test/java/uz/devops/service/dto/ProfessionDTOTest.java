package uz.devops.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class ProfessionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfessionDTO.class);
        ProfessionDTO professionDTO1 = new ProfessionDTO();
        professionDTO1.setName("id1");
        ProfessionDTO professionDTO2 = new ProfessionDTO();
        assertThat(professionDTO1).isNotEqualTo(professionDTO2);
        professionDTO2.setName(professionDTO1.getName());
        assertThat(professionDTO1).isEqualTo(professionDTO2);
        professionDTO2.setName("id2");
        assertThat(professionDTO1).isNotEqualTo(professionDTO2);
        professionDTO1.setName(null);
        assertThat(professionDTO1).isNotEqualTo(professionDTO2);
    }
}
