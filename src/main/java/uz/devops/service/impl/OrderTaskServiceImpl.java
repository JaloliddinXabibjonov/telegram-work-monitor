package uz.devops.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.OrderTask;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.OrderTaskService;
import uz.devops.service.dto.OrderTaskDTO;
import uz.devops.service.mapper.OrderTaskMapper;

/**
 * Service Implementation for managing {@link OrderTask}.
 */
@Service
@Transactional
public class OrderTaskServiceImpl implements OrderTaskService {

    private final Logger log = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    private final OrderTaskRepository orderTaskRepository;

    private final OrderTaskMapper orderTaskMapper;

    public OrderTaskServiceImpl(OrderTaskRepository orderTaskRepository, OrderTaskMapper orderTaskMapper) {
        this.orderTaskRepository = orderTaskRepository;
        this.orderTaskMapper = orderTaskMapper;
    }

    @Override
    public OrderTaskDTO save(OrderTaskDTO orderTaskDTO) {
        log.debug("Request to save OrderTask : {}", orderTaskDTO);
        OrderTask orderTask = orderTaskMapper.toEntity(orderTaskDTO);
        orderTask = orderTaskRepository.save(orderTask);
        return orderTaskMapper.toDto(orderTask);
    }

    @Override
    public Optional<OrderTaskDTO> partialUpdate(OrderTaskDTO orderTaskDTO) {
        log.debug("Request to partially update OrderTask : {}", orderTaskDTO);

        return orderTaskRepository
            .findById(orderTaskDTO.getId())
            .map(existingOrderTask -> {
                orderTaskMapper.partialUpdate(existingOrderTask, orderTaskDTO);

                return existingOrderTask;
            })
            .map(orderTaskRepository::save)
            .map(orderTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderTasks");
        return orderTaskRepository.findAll(pageable).map(orderTaskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderTaskDTO> findOne(Long id) {
        log.debug("Request to get OrderTask : {}", id);
        return orderTaskRepository.findById(id).map(orderTaskMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrderTask : {}", id);
        orderTaskRepository.deleteById(id);
    }
}
