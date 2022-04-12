package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import uz.devops.repository.ProfessionRepository;
import uz.devops.service.ProfessionService;
import uz.devops.service.dto.ProfessionDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.Profession}.
 */
@RestController
@RequestMapping("/api")
public class ProfessionResource {

    private final Logger log = LoggerFactory.getLogger(ProfessionResource.class);

    private static final String ENTITY_NAME = "profession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfessionService professionService;

    private final ProfessionRepository professionRepository;

    public ProfessionResource(ProfessionService professionService, ProfessionRepository professionRepository) {
        this.professionService = professionService;
        this.professionRepository = professionRepository;
    }

    /**
     * {@code POST  /professions} : Create a new profession.
     *
     * @param professionDTO the professionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new professionDTO, or with status {@code 400 (Bad Request)} if the profession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/professions")
    public ResponseEntity<ProfessionDTO> createProfession(@Valid @RequestBody ProfessionDTO professionDTO) throws URISyntaxException {
        log.debug("REST request to save Profession : {}", professionDTO);
        if (professionDTO.getName() != null) {
            throw new BadRequestAlertException("A new profession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProfessionDTO result = professionService.save(professionDTO);
        return ResponseEntity
            .created(new URI("/api/professions/" + result.getName()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code PUT  /professions/:name} : Updates an existing profession.
     *
     * @param name the id of the professionDTO to save.
     * @param professionDTO the professionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated professionDTO,
     * or with status {@code 400 (Bad Request)} if the professionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the professionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/professions/{name}")
    public ResponseEntity<ProfessionDTO> updateProfession(
        @PathVariable(value = "name", required = false) final String name,
        @Valid @RequestBody ProfessionDTO professionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Profession : {}, {}", name, professionDTO);
        if (professionDTO.getName() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(name, professionDTO.getName())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!professionRepository.existsById(name)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProfessionDTO result = professionService.save(professionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, professionDTO.getName()))
            .body(result);
    }

    /**
     * {@code PATCH  /professions/:name} : Partial updates given fields of an existing profession, field will ignore if it is null
     *
     * @param name the id of the professionDTO to save.
     * @param professionDTO the professionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated professionDTO,
     * or with status {@code 400 (Bad Request)} if the professionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the professionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the professionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/professions/{name}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfessionDTO> partialUpdateProfession(
        @PathVariable(value = "name", required = false) final String name,
        @NotNull @RequestBody ProfessionDTO professionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Profession partially : {}, {}", name, professionDTO);
        if (professionDTO.getName() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(name, professionDTO.getName())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!professionRepository.existsById(name)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfessionDTO> result = professionService.partialUpdate(professionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, professionDTO.getName())
        );
    }

    /**
     * {@code GET  /professions} : get all the professions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of professions in body.
     */
    @GetMapping("/professions")
    public ResponseEntity<List<ProfessionDTO>> getAllProfessions(Pageable pageable) {
        log.debug("REST request to get a page of Professions");
        Page<ProfessionDTO> page = professionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /professions/:id} : get the "id" profession.
     *
     * @param id the id of the professionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the professionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/professions/{id}")
    public ResponseEntity<ProfessionDTO> getProfession(@PathVariable String id) {
        log.debug("REST request to get Profession : {}", id);
        Optional<ProfessionDTO> professionDTO = professionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(professionDTO);
    }

    /**
     * {@code DELETE  /professions/:id} : delete the "id" profession.
     *
     * @param id the id of the professionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/professions/{id}")
    public ResponseEntity<Void> deleteProfession(@PathVariable String id) {
        log.debug("REST request to delete Profession : {}", id);
        professionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
