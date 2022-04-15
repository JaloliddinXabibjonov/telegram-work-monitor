package uz.devops.service.impl;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.JobRepository;
import uz.devops.repository.OrderRepository;
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

    private final JobRepository jobRepository;

    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, JobRepository jobRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.jobRepository = jobRepository;
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

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    public SimpleResultData<Order> findOrderById(Long orderId) {
        log.debug("Request to find order with id: {}", orderId);
        var optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.debug("Order not found with id: " + orderId);
            return new SimpleResultData<>("Order not found with id: " + orderId, false);
        }

        return new SimpleResultData<>("Order found", true, optionalOrder.get());
    }

    @Override
    public SimpleResultData<Order> createOrder(Long jobId, String description) {
        log.debug("Request to find Job with id: {}", jobId);

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            log.debug("Job not found with id: {}", jobId);
            return new SimpleResultData<>("Job not found with id: " + jobId, false);
        }

        Job job = optionalJob.get();

        Order order = new Order();
        order.setStatus(Status.TO_DO);
        order.setCreatedDate(Instant.now());
        order.setDescription(description);
        order.setJob(job);
        orderRepository.save(order);

        return new SimpleResultData<>("Order saved !", true, order);
    }
}
