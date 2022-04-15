package uz.devops.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.OrderService;
import uz.devops.service.OrderTaskService;
import uz.devops.service.TaskService;
import uz.devops.service.UserService;
import uz.devops.service.dto.OrderTaskDTO;
import uz.devops.service.mapper.OrderTaskMapper;

/**
 * Service Implementation for managing {@link OrderTask}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderTaskServiceImpl implements OrderTaskService {

    private final Logger log = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    private final OrderTaskRepository orderTaskRepository;

    private final OrderRepository orderRepository;

    private final OrderTaskMapper orderTaskMapper;

    private final OrderService orderService;

    private final TaskService taskService;

    private final UserService userService;

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

    @Override
    public SimpleResultData<OrderTask> checkOrderStatus(Long orderId, Long taskId) {
        var optionalOrderTask = orderTaskRepository.findByOrderAndTaskId(orderId, taskId);

        if (optionalOrderTask.isPresent()) {
            return new SimpleResultData<>("Order already get", false);
        }

        return new SimpleResultData<>("Success", true);
    }

    @Override
    public SimpleResultData<OrderTask> startedOrderTask(Long orderTaskId, String username, Long orderId, Long taskId) {
        log.debug("Request to create orderTask with tgUsername: {} orderId: {} and taskId: {}", username, orderId, taskId);

        var orderById = orderService.findOrderById(orderId);
        var order = orderById.getData();

        order.setStartedDate(Instant.now());
        order.setStatus(Status.DOING);
        orderRepository.save(order);

        var orderTaskResultData = findById(orderTaskId);
        var orderTask = orderTaskResultData.getData();
        orderTask.setStatus(Status.DOING);
        orderTask.setStartedDate(Instant.now());
        orderTask.setEmployeeUsername(username);
        orderTaskRepository.save(orderTask);

        return new SimpleResultData<>("Order Task saved !", true, orderTask);
    }

    @Override
    public SimpleResultData<OrderTask> createOrderTask(Long orderId, Long taskId) {
        log.debug("Request to create orderTask with orderId: {} and taskId: {}", orderId, taskId);

        var orderById = orderService.findOrderById(orderId);
        var taskById = taskService.findTaskById(taskId);

        var task = taskById.getData();
        var order = orderById.getData();

        OrderTask orderTask = new OrderTask();
        orderTask.setStatus(Status.TO_DO);
        orderTask.setOrder(order);
        orderTask.setTask(task);
        orderTaskRepository.save(orderTask);

        return new SimpleResultData<>("Order Task saved !", true, orderTask);
    }

    @Override
    public SimpleResultData<OrderTask> completedOrderTask(Long orderTaskId) {
        log.debug("Request to change status orderTask with id: {}", orderTaskId);

        var optionalOrderTask = orderTaskRepository.findById(orderTaskId);
        if (optionalOrderTask.isEmpty()) {
            log.debug("OrderTask not found with id: {}", orderTaskId);
            return new SimpleResultData<>("OrderTask not found with id: " + orderTaskId, false);
        }

        var orderTask = optionalOrderTask.get();
        orderTask.setStatus(Status.DONE);
        orderTask.setEndDate(Instant.now());
        orderTaskRepository.save(orderTask);

        var order = orderTask.getOrder();
        order.setStatus(Status.DONE);
        order.setEndDate(Instant.now());
        orderRepository.save(order);

        return new SimpleResultData<>("OrderTask done !", true, orderTask);
    }

    @Override
    public SimpleResultData<OrderTask> rejectOrderTask(Long orderTaskId) {
        var orderTaskSimpleResultData = findById(orderTaskId);
        var orderTask = orderTaskSimpleResultData.getData();
        orderTask.setStatus(Status.TO_DO);
        orderTask.setEmployeeUsername(null);
        orderTask.setStartedDate(null);
        orderTaskRepository.save(orderTask);

        return new SimpleResultData<>("Order rejected !", true, orderTask);
    }

    @Override
    public SimpleResultData<OrderTask> findById(Long orderTaskId) {
        log.debug("Request to find orderTask id: {}", orderTaskId);
        var optionalOrderTask = orderTaskRepository.findById(orderTaskId);

        if (optionalOrderTask.isEmpty()) {
            log.debug("OrderTask not found with id: {}", orderTaskId);
            return new SimpleResultData<>("OrderTask not found with id: " + orderTaskId, false);
        }
        return new SimpleResultData<>("OrderTask found !", true, optionalOrderTask.get());
    }

    @Override
    public SimpleResultData<List<OrderTask>> findAvailableOrderTask(Long chatId) {
        var userByChatId = userService.findUserByChatId(chatId);
        var user = userByChatId.getData();

        var orderTasks = orderTaskRepository
            .findAllByStatus(Status.TO_DO)
            .stream()
            .filter(orderTask ->
                orderTask
                    .getTask()
                    .getProfessions()
                    .stream()
                    .anyMatch(taskProfession -> user.getProfessions().stream().anyMatch(taskProfession::equals))
            )
            .collect(Collectors.toList());

        if (orderTasks.isEmpty()) {
            log.debug("Available orderTask not found");
            return new SimpleResultData<>("False", false);
        }

        return new SimpleResultData<>("Success", true, orderTasks);
    }
}
