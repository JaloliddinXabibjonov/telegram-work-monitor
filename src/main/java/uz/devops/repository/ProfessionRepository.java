package uz.devops.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Profession;

/**
 * Spring Data SQL repository for the Profession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfessionRepository extends JpaRepository<Profession, String>, JpaSpecificationExecutor<Profession> {}
