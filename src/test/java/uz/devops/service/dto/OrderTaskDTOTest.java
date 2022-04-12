package uz.devops.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class OrderTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderTaskDTO.class);
        OrderTaskDTO orderTaskDTO1 = new OrderTaskDTO();
        orderTaskDTO1.setId(1L);
        OrderTaskDTO orderTaskDTO2 = new OrderTaskDTO();
        assertThat(orderTaskDTO1).isNotEqualTo(orderTaskDTO2);
        orderTaskDTO2.setId(orderTaskDTO1.getId());
        assertThat(orderTaskDTO1).isEqualTo(orderTaskDTO2);
        orderTaskDTO2.setId(2L);
        assertThat(orderTaskDTO1).isNotEqualTo(orderTaskDTO2);
        orderTaskDTO1.setId(null);
        assertThat(orderTaskDTO1).isNotEqualTo(orderTaskDTO2);
    }
}
