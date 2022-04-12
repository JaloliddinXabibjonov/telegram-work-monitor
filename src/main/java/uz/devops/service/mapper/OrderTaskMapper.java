package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.OrderTask;
import uz.devops.service.dto.OrderTaskDTO;

/**
 * Mapper for the entity {@link OrderTask} and its DTO {@link OrderTaskDTO}.
 */
@Mapper(componentModel = "spring", uses = { TaskMapper.class, OrderMapper.class })
public interface OrderTaskMapper extends EntityMapper<OrderTaskDTO, OrderTask> {
    @Mapping(target = "task", source = "task", qualifiedByName = "id")
    @Mapping(target = "order", source = "order", qualifiedByName = "id")
    OrderTaskDTO toDto(OrderTask s);
}
