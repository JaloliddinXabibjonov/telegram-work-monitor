package uz.devops.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskInfoMapperTest {

    private TaskInfoMapper taskInfoMapper;

    @BeforeEach
    public void setUp() {
        taskInfoMapper = new TaskInfoMapperImpl();
    }
}
