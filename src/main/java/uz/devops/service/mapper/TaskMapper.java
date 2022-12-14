package uz.devops.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.devops.domain.Task;
import uz.devops.service.dto.TaskDTO;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring", uses = { JobMapper.class, ProfessionMapper.class })
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "job", source = "job", qualifiedByName = "id")
    @Mapping(target = "professions", source = "professions", qualifiedByName = "nameSet")
    TaskDTO toDto(Task s);

    @Named("id")
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoId(Task task);

    @Mapping(target = "removeProfession", ignore = true)
    Task toEntity(TaskDTO taskDTO);
}
