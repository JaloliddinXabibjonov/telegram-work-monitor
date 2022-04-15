package uz.devops.service.mapper;

import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.devops.domain.Profession;
import uz.devops.service.dto.ProfessionDTO;

/**
 * Mapper for the entity {@link Profession} and its DTO {@link ProfessionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProfessionMapper extends EntityMapper<ProfessionDTO, Profession> {
    @Named("nameSet")
    @Mapping(target = "name", source = "name")
    Set<ProfessionDTO> toDtoIdSet(Set<Profession> profession);
}
