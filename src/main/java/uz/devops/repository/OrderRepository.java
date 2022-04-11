package uz.devops.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;

/**
 * Spring Data SQL repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStatus(@Param("status") Status status);

    List<Order> findAllByChatIdAndStatusIsNotLike(@Param("chatId") String chatId, @Param("status") Status status);
}
