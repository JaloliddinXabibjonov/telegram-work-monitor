package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Order;

/**
 * Spring Data SQL repository for the Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    default Optional<Order> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Order> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Order> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct jhiOrder from Order jhiOrder left join fetch jhiOrder.job",
        countQuery = "select count(distinct jhiOrder) from Order jhiOrder"
    )
    Page<Order> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct jhiOrder from Order jhiOrder left join fetch jhiOrder.job")
    List<Order> findAllWithToOneRelationships();

    @Query("select jhiOrder from Order jhiOrder left join fetch jhiOrder.job where jhiOrder.id =:id")
    Optional<Order> findOneWithToOneRelationships(@Param("id") Long id);
}
