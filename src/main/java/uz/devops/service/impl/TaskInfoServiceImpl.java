package uz.devops.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.TaskInfo;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.service.TaskInfoService;
import uz.devops.service.dto.TaskInfoDTO;
import uz.devops.service.mapper.TaskInfoMapper;

/**
 * Service Implementation for managing {@link TaskInfo}.
 */
@Service
@Transactional
public class TaskInfoServiceImpl implements TaskInfoService {

    private final Logger log = LoggerFactory.getLogger(TaskInfoServiceImpl.class);

    private final TaskInfoRepository taskInfoRepository;

    private final TaskInfoMapper taskInfoMapper;

    public TaskInfoServiceImpl(TaskInfoRepository taskInfoRepository, TaskInfoMapper taskInfoMapper) {
        this.taskInfoRepository = taskInfoRepository;
        this.taskInfoMapper = taskInfoMapper;
    }

    @Override
    public TaskInfoDTO save(TaskInfoDTO taskInfoDTO) {
        log.debug("Request to save TaskInfo : {}", taskInfoDTO);
        TaskInfo taskInfo = taskInfoMapper.toEntity(taskInfoDTO);
        taskInfo = taskInfoRepository.save(taskInfo);
        return taskInfoMapper.toDto(taskInfo);
    }

    @Override
    public Optional<TaskInfoDTO> partialUpdate(TaskInfoDTO taskInfoDTO) {
        log.debug("Request to partially update TaskInfo : {}", taskInfoDTO);

        return taskInfoRepository
            .findById(taskInfoDTO.getId())
            .map(existingTaskInfo -> {
                taskInfoMapper.partialUpdate(existingTaskInfo, taskInfoDTO);

                return existingTaskInfo;
            })
            .map(taskInfoRepository::save)
            .map(taskInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskInfoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaskInfos");
        return taskInfoRepository.findAll(pageable).map(taskInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskInfoDTO> findOne(Long id) {
        log.debug("Request to get TaskInfo : {}", id);
        return taskInfoRepository.findById(id).map(taskInfoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TaskInfo : {}", id);
        taskInfoRepository.deleteById(id);
    }
}
