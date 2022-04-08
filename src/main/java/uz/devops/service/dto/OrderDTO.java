package uz.devops.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;
import uz.devops.domain.enumeration.OrderStatus;

/**
 * A DTO for the {@link uz.devops.domain.Order} entity.
 */
@Schema(description = "Заказы на работу")
public class OrderDTO implements Serializable {

    private Long id;

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @Schema(description = "Наименование", required = true)
    private String name;

    /**
     * Цена заказа
     */
    @Schema(description = "Цена заказа")
    private Long price;

    private String chatId;

    private String employee;

    private OrderStatus status;

    /**
     * Описание заказа
     */
    @Schema(description = "Описание заказа")
    @Lob
    private String description;

    private Instant startedDate;

    private Instant endDate;

    private JobDTO job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", chatId='" + getChatId() + "'" +
            ", employee='" + getEmployee() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", startedDate='" + getStartedDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", job=" + getJob() +
            "}";
    }
}
