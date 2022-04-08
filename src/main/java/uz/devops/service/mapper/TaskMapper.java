package uz.devops.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.domain.Job;
import uz.devops.domain.Profession;
import uz.devops.domain.Task;
import uz.devops.service.dto.JobDTO;
import uz.devops.service.dto.ProfessionDTO;
import uz.devops.service.dto.TaskDTO;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "professions", source = "professions", qualifiedByName = "professionNameSet")
    @Mapping(target = "job", source = "job", qualifiedByName = "jobName")
    TaskDTO toDto(Task s);

    @Mapping(target = "removeProfession", ignore = true)
    Task toEntity(TaskDTO taskDTO);

    @Named("professionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProfessionDTO toDtoProfessionName(Profession profession);

    @Named("professionNameSet")
    default Set<ProfessionDTO> toDtoProfessionNameSet(Set<Profession> profession) {
        return profession.stream().map(this::toDtoProfessionName).collect(Collectors.toSet());
    }

    @Named("jobName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    JobDTO toDtoJobName(Job job);
}
