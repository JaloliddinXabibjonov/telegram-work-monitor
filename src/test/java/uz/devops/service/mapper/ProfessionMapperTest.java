package uz.devops.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfessionMapperTest {

    private ProfessionMapper professionMapper;

    @BeforeEach
    public void setUp() {
        professionMapper = new ProfessionMapperImpl();
    }
}
