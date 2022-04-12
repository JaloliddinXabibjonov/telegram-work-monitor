package uz.devops.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.domain.Task} entity.
 */
@ApiModel(description = "Справочник для описания вида этапа задачи")
public class TaskDTO implements Serializable {

    private Long id;

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @ApiModelProperty(value = "Наименование", required = true)
    private String name;

    /**
     * Цена
     */
    @ApiModelProperty(value = "Цена")
    private String price;

    /**
     * Описания
     */
    @ApiModelProperty(value = "Описания")
    private String description;

    /**
     * Приоритет
     */
    @ApiModelProperty(value = "Приоритет")
    private Integer priority;

    private JobDTO job;

    private Set<ProfessionDTO> professions = new HashSet<>();

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    public Set<ProfessionDTO> getProfessions() {
        return professions;
    }

    public void setProfessions(Set<ProfessionDTO> professions) {
        this.professions = professions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskDTO)) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price='" + getPrice() + "'" +
            ", description='" + getDescription() + "'" +
            ", priority=" + getPriority() +
            ", job=" + getJob() +
            ", professions=" + getProfessions() +
            "}";
    }
}
