package uz.devops.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Job;
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
    public SimpleResultData<List<Job>> findAllJobs() {
        log.debug("Request to find all jobs");
        List<Job> jobs = jobRepository.findAll();

        if (jobs.isEmpty()) {
            log.debug("Jobs not found");
            return new SimpleResultData<>("Jobs not found", false);
        }

        log.debug("Available jobs found");
        return new SimpleResultData<>("Available jobs", true, jobs);
    }

    @Override
    public SimpleResultData<Job> findJobByOrderId(Long orderId) {
        log.debug("Request to find job with orderId: {}", orderId);
        Optional<Job> optionalJob = jobRepository.findJobByOrderId(orderId);

        if (optionalJob.isEmpty()) {
            log.debug("Job not found with orderId: {}", orderId);
            return new SimpleResultData<>("Job not found with orderId: " + orderId, false);
        }

        return new SimpleResultData<>("Job found !", true, optionalJob.get());
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
}
