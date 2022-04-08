package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Job;
import uz.devops.service.dto.JobDTO;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring")
public interface JobMapper extends EntityMapper<JobDTO, Job> {}