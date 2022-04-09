package uz.devops.service.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.JobRepository;
import uz.devops.service.JobService;
import uz.devops.service.dto.JobDTO;
import uz.devops.service.mapper.JobMapper;

/**
 * Service Implementation for managing {@link Job}.
 */
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public JobDTO save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    @Override
    public JobDTO update(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    @Override
    public Optional<JobDTO> partialUpdate(JobDTO jobDTO) {
        log.debug("Request to partially update Job : {}", jobDTO);

        return jobRepository
            .findById(jobDTO.getId())
            .map(existingJob -> {
                jobMapper.partialUpdate(existingJob, jobDTO);

                return existingJob;
            })
            .map(jobRepository::save)
            .map(jobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobDTO> findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findById(id).map(jobMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.deleteById(id);
    }

    @Override
    public Task getAvailableTask(Long jobId) {
        log.debug("Request to find job with id: {}", jobId);
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        Job job = optionalJob.get();

        return job.getTasks().stream().filter(task -> task.getStatus().equals(Status.NEW)).min(Comparator.comparing(Task::getId)).get();
    }

    @Override
    public Job createNewJob(Message message) {
        log.info("Request to create Job with name: {}", message.getText());

        Job job = new Job();
        job.setName(message.getText());
        job.setCreatedDate(Instant.now());
        jobRepository.save(job);

        log.info("Job created: {}", job);
        return job;
    }

    @Override
    public void addOrderToJob(Long jobId, Order order) {
        log.debug("Request to find Job with id: {}", jobId);

        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            log.debug("Job not found with id: {}", jobId);
            return;
        }

        Job job = optionalJob.get();
        if (job.getOrders() != null) {
            job.getOrders().add(order);
            jobRepository.save(job);
        } else {
            job.setOrders(Set.of(order));
            jobRepository.save(job);
        }
    }
}
