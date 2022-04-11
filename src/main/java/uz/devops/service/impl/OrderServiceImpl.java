package uz.devops.service.impl;

import java.util.Optional;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.Order;
import uz.devops.domain.TaskInfo;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.service.OrderService;
import uz.devops.service.dto.OrderDTO;
import uz.devops.service.mapper.OrderMapper;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final TaskInfoRepository taskInfoRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, TaskInfoRepository taskInfoRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.taskInfoRepository = taskInfoRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public Optional<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .map(orderRepository::save)
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    public Page<OrderDTO> findAllWithEagerRelationships(Pageable pageable) {
        return orderRepository.findAllWithEagerRelationships(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findOneWithEagerRelationships(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        log.debug("Request to find order with id: {}", orderId);
        return Optional
            .of(orderRepository.findById(orderId))
            .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public void addOrderToTaskInfo(Long orderId, Long taskInfoId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<TaskInfo> optionalTaskInfo = taskInfoRepository.findById(taskInfoId);

        if (optionalOrder.isEmpty() || optionalTaskInfo.isEmpty()) {
            return;
        }

        Order order = optionalOrder.get();
        TaskInfo taskInfo = optionalTaskInfo.get();

        //        order.setTaskInfo(taskInfo);
        //        orderRepository.save(order);
        if (taskInfo.getOrders() != null) {
            taskInfo.getOrders().add(order);
            taskInfoRepository.save(taskInfo);
        } else {
            taskInfo.setOrders(Set.of(order));
            taskInfoRepository.save(taskInfo);
        }
    }
}
