package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Profession;
import uz.devops.service.dto.ProfessionDTO;

/**
 * Mapper for the entity {@link Profession} and its DTO {@link ProfessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfessionMapper extends EntityMapper<ProfessionDTO, Profession> {}
