package uz.devops.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import uz.devops.domain.enumeration.Status;

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

    private Status status;

    private Set<ProfessionDTO> professions = new HashSet<>();

    private JobDTO job;

    private TaskInfoDTO taskInfo;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<ProfessionDTO> getProfessions() {
        return professions;
    }

    public void setProfessions(Set<ProfessionDTO> professions) {
        this.professions = professions;
    }

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    public TaskInfoDTO getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfoDTO taskInfo) {
        this.taskInfo = taskInfo;
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
            ", status='" + getStatus() + "'" +
            ", professions=" + getProfessions() +
            ", job=" + getJob() +
            ", taskInfo=" + getTaskInfo() +
            "}";
    }
}
