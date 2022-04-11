package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.service.TaskInfoService;
import uz.devops.service.dto.TaskInfoDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.TaskInfo}.
 */
@RestController
@RequestMapping("/api")
public class TaskInfoResource {

    private final Logger log = LoggerFactory.getLogger(TaskInfoResource.class);

    private static final String ENTITY_NAME = "taskInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskInfoService taskInfoService;

    private final TaskInfoRepository taskInfoRepository;

    public TaskInfoResource(TaskInfoService taskInfoService, TaskInfoRepository taskInfoRepository) {
        this.taskInfoService = taskInfoService;
        this.taskInfoRepository = taskInfoRepository;
    }

    /**
     * {@code POST  /task-infos} : Create a new taskInfo.
     *
     * @param taskInfoDTO the taskInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskInfoDTO, or with status {@code 400 (Bad Request)} if the taskInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/task-infos")
    public ResponseEntity<TaskInfoDTO> createTaskInfo(@RequestBody TaskInfoDTO taskInfoDTO) throws URISyntaxException {
        log.debug("REST request to save TaskInfo : {}", taskInfoDTO);
        if (taskInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskInfoDTO result = taskInfoService.save(taskInfoDTO);
        return ResponseEntity
            .created(new URI("/api/task-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /task-infos/:id} : Updates an existing taskInfo.
     *
     * @param id the id of the taskInfoDTO to save.
     * @param taskInfoDTO the taskInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskInfoDTO,
     * or with status {@code 400 (Bad Request)} if the taskInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/task-infos/{id}")
    public ResponseEntity<TaskInfoDTO> updateTaskInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskInfoDTO taskInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TaskInfo : {}, {}", id, taskInfoDTO);
        if (taskInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskInfoDTO result = taskInfoService.save(taskInfoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskInfoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /task-infos/:id} : Partial updates given fields of an existing taskInfo, field will ignore if it is null
     *
     * @param id the id of the taskInfoDTO to save.
     * @param taskInfoDTO the taskInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskInfoDTO,
     * or with status {@code 400 (Bad Request)} if the taskInfoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskInfoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/task-infos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskInfoDTO> partialUpdateTaskInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskInfoDTO taskInfoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaskInfo partially : {}, {}", id, taskInfoDTO);
        if (taskInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskInfoDTO> result = taskInfoService.partialUpdate(taskInfoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskInfoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /task-infos} : get all the taskInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskInfos in body.
     */
    @GetMapping("/task-infos")
    public ResponseEntity<List<TaskInfoDTO>> getAllTaskInfos(Pageable pageable) {
        log.debug("REST request to get a page of TaskInfos");
        Page<TaskInfoDTO> page = taskInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-infos/:id} : get the "id" taskInfo.
     *
     * @param id the id of the taskInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/task-infos/{id}")
    public ResponseEntity<TaskInfoDTO> getTaskInfo(@PathVariable Long id) {
        log.debug("REST request to get TaskInfo : {}", id);
        Optional<TaskInfoDTO> taskInfoDTO = taskInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskInfoDTO);
    }

    /**
     * {@code DELETE  /task-infos/:id} : delete the "id" taskInfo.
     *
     * @param id the id of the taskInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/task-infos/{id}")
    public ResponseEntity<Void> deleteTaskInfo(@PathVariable Long id) {
        log.debug("REST request to delete TaskInfo : {}", id);
        taskInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
