package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.service.dto.JobDTO;
import uz.devops.service.dto.OrderDTO;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "job", source = "job", qualifiedByName = "jobName")
    OrderDTO toDto(Order s);

    @Named("jobName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    JobDTO toDtoJobName(Job job);
}
