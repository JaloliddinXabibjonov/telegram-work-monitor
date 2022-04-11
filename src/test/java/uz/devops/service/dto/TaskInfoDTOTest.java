package uz.devops.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class TaskInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskInfoDTO.class);
        TaskInfoDTO taskInfoDTO1 = new TaskInfoDTO();
        taskInfoDTO1.setId(1L);
        TaskInfoDTO taskInfoDTO2 = new TaskInfoDTO();
        assertThat(taskInfoDTO1).isNotEqualTo(taskInfoDTO2);
        taskInfoDTO2.setId(taskInfoDTO1.getId());
        assertThat(taskInfoDTO1).isEqualTo(taskInfoDTO2);
        taskInfoDTO2.setId(2L);
        assertThat(taskInfoDTO1).isNotEqualTo(taskInfoDTO2);
        taskInfoDTO1.setId(null);
        assertThat(taskInfoDTO1).isNotEqualTo(taskInfoDTO2);
    }
}
