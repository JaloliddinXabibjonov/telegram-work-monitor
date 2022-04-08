package uz.devops.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.devops.domain.Task;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TaskRepositoryWithBagRelationshipsImpl implements TaskRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Task> fetchBagRelationships(Optional<Task> task) {
        return task.map(this::fetchProfessions);
    }

    @Override
    public Page<Task> fetchBagRelationships(Page<Task> tasks) {
        return new PageImpl<>(fetchBagRelationships(tasks.getContent()), tasks.getPageable(), tasks.getTotalElements());
    }

    @Override
    public List<Task> fetchBagRelationships(List<Task> tasks) {
        return Optional.of(tasks).map(this::fetchProfessions).orElse(Collections.emptyList());
    }

    Task fetchProfessions(Task result) {
        return entityManager
            .createQuery("select task from Task task left join fetch task.professions where task is :task", Task.class)
            .setParameter("task", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Task> fetchProfessions(List<Task> tasks) {
        return entityManager
            .createQuery("select distinct task from Task task left join fetch task.professions where task in :tasks", Task.class)
            .setParameter("tasks", tasks)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
