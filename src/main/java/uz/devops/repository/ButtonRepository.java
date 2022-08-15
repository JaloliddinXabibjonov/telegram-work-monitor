package uz.devops.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.devops.domain.Button;
import uz.devops.domain.enumeration.CommandStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ButtonRepository extends JpaRepository<Button, Long> {

    Set<Button> getAllByStatus(CommandStatus status1);
    List<Button> getAllByStatusAndRole(CommandStatus status, String role);
    Optional<Button> findByName(String name);
    Optional<Button> findByNameAndRole(String name, String role);
}
