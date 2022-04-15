package uz.devops.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.devops.domain.Order;
import uz.devops.service.dto.OrderDTO;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { JobMapper.class })
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "job", source = "job", qualifiedByName = "id")
    OrderDTO toDto(Order s);

    @Named("id")
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoId(Order order);
}
