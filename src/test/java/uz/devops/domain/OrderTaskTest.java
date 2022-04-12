package uz.devops.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class OrderTaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderTask.class);
        OrderTask orderTask1 = new OrderTask();
        orderTask1.setId(1L);
        OrderTask orderTask2 = new OrderTask();
        orderTask2.setId(orderTask1.getId());
        assertThat(orderTask1).isEqualTo(orderTask2);
        orderTask2.setId(2L);
        assertThat(orderTask1).isNotEqualTo(orderTask2);
        orderTask1.setId(null);
        assertThat(orderTask1).isNotEqualTo(orderTask2);
    }
}
