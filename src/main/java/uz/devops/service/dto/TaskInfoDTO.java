package uz.devops.service.dto;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import uz.devops.domain.enumeration.Status;

/**
 * A DTO for the {@link uz.devops.domain.TaskInfo} entity.
 */
public class TaskInfoDTO implements Serializable {

    private Long id;

    /**
     * Цена заказа
     */
    @ApiModelProperty(value = "Цена заказа")
    private String price;

    private Status status;

    /**
     * Описание заказа
     */
    @ApiModelProperty(value = "Описание заказа")
    @Lob
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskInfoDTO)) {
            return false;
        }

        TaskInfoDTO taskInfoDTO = (TaskInfoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskInfoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskInfoDTO{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
