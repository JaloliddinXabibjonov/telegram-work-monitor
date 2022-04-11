package uz.devops.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class TaskInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskInfo.class);
        TaskInfo taskInfo1 = new TaskInfo();
        taskInfo1.setId(1L);
        TaskInfo taskInfo2 = new TaskInfo();
        taskInfo2.setId(taskInfo1.getId());
        assertThat(taskInfo1).isEqualTo(taskInfo2);
        taskInfo2.setId(2L);
        assertThat(taskInfo1).isNotEqualTo(taskInfo2);
        taskInfo1.setId(null);
        assertThat(taskInfo1).isNotEqualTo(taskInfo2);
    }
}
