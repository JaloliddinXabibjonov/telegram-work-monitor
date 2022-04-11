package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.TaskInfo;
import uz.devops.service.dto.TaskInfoDTO;

/**
 * Mapper for the entity {@link TaskInfo} and its DTO {@link TaskInfoDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TaskInfoMapper extends EntityMapper<TaskInfoDTO, TaskInfo> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskInfoDTO toDtoId(TaskInfo taskInfo);
}
