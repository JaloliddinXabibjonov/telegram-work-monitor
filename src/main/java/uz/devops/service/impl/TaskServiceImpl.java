package uz.devops.service.impl;

import java.util.*;
import javax.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.Task;
import uz.devops.domain.TaskInfo;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ProfessionRepository;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.repository.TaskRepository;
import uz.devops.service.TaskService;
import uz.devops.service.dto.TaskDTO;
import uz.devops.service.mapper.TaskMapper;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final ProfessionRepository professionRepository;

    private final TaskInfoRepository taskInfoRepository;

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(
        TaskRepository taskRepository,
        ProfessionRepository professionRepository,
        TaskInfoRepository taskInfoRepository,
        TaskMapper taskMapper
    ) {
        this.taskRepository = taskRepository;
        this.professionRepository = professionRepository;
        this.taskInfoRepository = taskInfoRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDTO save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public Optional<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        log.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .map(taskRepository::save)
            .map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAll(pageable).map(taskMapper::toDto);
    }

    public Page<TaskDTO> findAllWithEagerRelationships(Pageable pageable) {
        return taskRepository.findAllWithEagerRelationships(pageable).map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findOneWithEagerRelationships(id).map(taskMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }

    @Override
    public void addProfessionToTask(String profName, Task task) {
        var professionOptional = professionRepository.findById(profName);
        if (professionOptional.isEmpty()) {
            return;
        }
        var list = new ArrayList<>(Collections.singletonList(professionOptional.get()));
        if (task.getProfessions() != null) {
            list.addAll(task.getProfessions());
        }
        task.setProfessions(new HashSet<>(list));
        taskRepository.save(task);
    }

    @Override
    public void checkTaskProfession(String data, Long taskId) {
        taskRepository
            .findById(taskId)
            .ifPresent(task -> {
                if (task.getProfessions().removeIf(profession -> Objects.equals(profession.getName(), data))) {
                    taskRepository.save(task);
                    return;
                }
                addProfessionToTask(data, task);
            });
    }

    @Override
    public Task findTaskByInfoId(Long taskInfoId) {
        TaskInfo taskInfo = taskInfoRepository
            .findById(taskInfoId)
            .orElseThrow(() -> new NotFoundException("Task Info not found with id: " + taskInfoId));

        return taskInfo
            .getTasks()
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Task not found with taskInfoId: " + taskInfoId));
    }

    @Override
    public TaskInfo getTaskInfo(Long orderId) {
        return taskInfoRepository
            .findAll()
            .stream()
            .filter(taskInfo -> taskInfo.getOrders().stream().anyMatch(order -> order.getId().equals(orderId)))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Task Info not found with orderId: " + orderId));
    }

    @Override
    public TaskInfo findTaskInfoByTaskId(Long taskId) {
        return taskInfoRepository
            .findAll()
            .stream()
            .filter(taskInfo -> taskInfo.getTasks().stream().anyMatch(task -> task.getId().equals(taskId)))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Task Info not found with taskId: " + taskId));
    }

    @Override
    public void changeTaskStatus(Long taskInfoId) {
        Optional<TaskInfo> optionalTaskInfo = taskInfoRepository.findById(taskInfoId);
        if (optionalTaskInfo.isEmpty()) {
            return;
        }
        TaskInfo taskInfo = optionalTaskInfo.get();
        Task task = taskInfo.getTasks().stream().findFirst().get();

        task.setStatus(Status.DOING);
        taskRepository.save(task);
    }

    @Override
    public TaskInfo findTaskInfoById(Long taskInfoId) {
        return taskInfoRepository.findById(taskInfoId).orElseThrow(() -> new NotFoundException("Task info not found with id" + taskInfoId));
    }

    @Override
    public void addTaskToTaskInfo(TaskInfo taskInfo, Long taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return;
        }
        Task task = optionalTask.get();
        task.setTaskInfo(taskInfo);
        taskRepository.save(task);
        //        if (taskInfo.getTasks() != null) {
        //            taskInfo.getTasks().add(task);
        //            taskInfoRepository.save(taskInfo);
        //        } else {
        //            taskInfo.setTasks(Set.of(task));
        //            taskInfoRepository.save(taskInfo);
        //        }
    }
}
