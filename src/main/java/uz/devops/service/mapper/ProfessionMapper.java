package uz.devops.service.mapper;

import java.util.Set;
import org.mapstruct.*;
import uz.devops.domain.Profession;
import uz.devops.service.dto.ProfessionDTO;

/**
 * Mapper for the entity {@link Profession} and its DTO {@link ProfessionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProfessionMapper extends EntityMapper<ProfessionDTO, Profession> {
    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    Set<ProfessionDTO> toDtoIdSet(Set<Profession> profession);
}
