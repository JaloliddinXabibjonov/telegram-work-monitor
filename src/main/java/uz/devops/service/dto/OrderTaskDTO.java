package uz.devops.service.dto;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import uz.devops.domain.enumeration.Status;

/**
 * A DTO for the {@link uz.devops.domain.OrderTask} entity.
 */
public class OrderTaskDTO implements Serializable {

    private Long id;

    private Status status;

    /**
     * Дата начала
     */
    @ApiModelProperty(value = "Дата начала")
    private Instant startedDate;

    /**
     * Дата окончания
     */
    @ApiModelProperty(value = "Дата окончания")
    private Instant endDate;

    /**
     * Имя сотрудника
     */
    @ApiModelProperty(value = "Имя сотрудника")
    private String employeeUsername;

    private TaskDTO task;

    private OrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Instant startedDate) {
        this.startedDate = startedDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderTaskDTO)) {
            return false;
        }

        OrderTaskDTO orderTaskDTO = (OrderTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderTaskDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startedDate='" + getStartedDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", employeeUsername='" + getEmployeeUsername() + "'" +
            ", task=" + getTask() +
            ", order=" + getOrder() +
            "}";
    }
}
