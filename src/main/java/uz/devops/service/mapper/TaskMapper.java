package uz.devops.service.mapper;

import java.util.Set;
import org.mapstruct.*;
import uz.devops.domain.Task;
import uz.devops.service.dto.TaskDTO;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProfessionMapper.class, JobMapper.class, TaskInfoMapper.class })
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "professions", source = "professions", qualifiedByName = "nameSet")
    @Mapping(target = "job", source = "job", qualifiedByName = "name")
    @Mapping(target = "taskInfo", source = "taskInfo", qualifiedByName = "id")
    TaskDTO toDto(Task s);

    @Mapping(target = "removeProfession", ignore = true)
    Task toEntity(TaskDTO taskDTO);
}
