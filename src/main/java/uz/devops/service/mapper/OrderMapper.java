package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Order;
import uz.devops.service.dto.OrderDTO;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { JobMapper.class, TaskInfoMapper.class })
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "job", source = "job", qualifiedByName = "name")
    @Mapping(target = "taskInfo", source = "taskInfo", qualifiedByName = "id")
    OrderDTO toDto(Order s);
}
