package uz.devops.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTaskMapperTest {

    private OrderTaskMapper orderTaskMapper;

    @BeforeEach
    public void setUp() {
        orderTaskMapper = new OrderTaskMapperImpl();
    }
}
