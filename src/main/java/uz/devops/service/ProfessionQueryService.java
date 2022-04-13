package uz.devops.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uz.devops.domain.*; // for static metamodels
import uz.devops.domain.Profession;
import uz.devops.repository.ProfessionRepository;
import uz.devops.service.criteria.ProfessionCriteria;
import uz.devops.service.dto.ProfessionDTO;
import uz.devops.service.mapper.ProfessionMapper;

/**
 * Service for executing complex queries for {@link Profession} entities in the database.
 * The main input is a {@link ProfessionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProfessionDTO} or a {@link Page} of {@link ProfessionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProfessionQueryService extends QueryService<Profession> {

    private final Logger log = LoggerFactory.getLogger(ProfessionQueryService.class);

    private final ProfessionRepository professionRepository;

    private final ProfessionMapper professionMapper;

    public ProfessionQueryService(ProfessionRepository professionRepository, ProfessionMapper professionMapper) {
        this.professionRepository = professionRepository;
        this.professionMapper = professionMapper;
    }

    /**
     * Return a {@link List} of {@link ProfessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProfessionDTO> findByCriteria(ProfessionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Profession> specification = createSpecification(criteria);
        return professionMapper.toDto(professionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProfessionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProfessionDTO> findByCriteria(ProfessionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Profession> specification = createSpecification(criteria);
        return professionRepository.findAll(specification, page).map(professionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProfessionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Profession> specification = createSpecification(criteria);
        return professionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProfessionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Profession> createSpecification(ProfessionCriteria criteria) {
        Specification<Profession> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Profession_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Profession_.description));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(Profession_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
        }
        return specification;
    }
}
