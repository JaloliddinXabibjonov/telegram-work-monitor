package uz.devops.service.impl;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Task;
import uz.devops.repository.JobRepository;
import uz.devops.repository.ProfessionRepository;
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

    private final TaskRepository taskRepository;

    private final ProfessionRepository professionRepository;

    private final JobRepository jobRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(
        TaskRepository taskRepository,
        ProfessionRepository professionRepository,
        JobRepository jobRepository,
        TaskMapper taskMapper
    ) {
        this.taskRepository = taskRepository;
        this.professionRepository = professionRepository;
        this.jobRepository = jobRepository;
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
    public SimpleResultData<Task> findNextTask(Long jobId, Long taskId) {
        log.debug("Request to find next task with jobId: {}", jobId);

        var taskOptional = jobRepository.findNextTaskByJobId(jobId, taskId);
        if (taskOptional.isEmpty()) {
            log.debug("Available task not found with jobId: {} and taskId: {}", jobId, taskId);
            return new SimpleResultData<>("Available task not found with jobId: " + jobId + "and taskId: " + taskId, false);
        }

        var availableTask = taskOptional.stream().findFirst().get();
        log.debug("Available task: {}", availableTask);
        return new SimpleResultData<>("Available task: " + availableTask, true, availableTask);
    }

    @Override
    public SimpleResultData<Task> findFirstTaskByJobId(Long jobId) {
        log.debug("Request to find available task by jobId: {}", jobId);
        var optionalTask = jobRepository.findTasksByJobId(jobId).stream().findFirst();

        if (optionalTask.isEmpty()) {
            log.debug("Task not found with jobId: {}", jobId);
            return new SimpleResultData<>("Task not found with jobId: " + jobId, false);
        }

        log.debug("Available task: {}", optionalTask.get());
        return new SimpleResultData<>("Available task: " + optionalTask.get(), true, optionalTask.get());
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
    public SimpleResultData<Task> findTaskById(Long taskId) {
        var optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            log.debug("Task not found with id: {}", taskId);
            return new SimpleResultData<>("Task not found with id: " + taskId, false);
        }

        return new SimpleResultData<>("Task found !", true, optionalTask.get());
    }
}
