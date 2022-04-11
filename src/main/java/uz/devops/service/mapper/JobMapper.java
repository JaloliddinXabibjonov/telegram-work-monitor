package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Job;
import uz.devops.service.dto.JobDTO;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JobMapper extends EntityMapper<JobDTO, Job> {
    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    JobDTO toDtoName(Job job);
}
