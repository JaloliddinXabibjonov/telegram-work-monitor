package uz.devops.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.domain.Order} entity.
 */
@ApiModel(description = "Заказы на работу")
public class OrderDTO implements Serializable {

    private Long id;

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @ApiModelProperty(value = "Наименование", required = true)
    private String name;

    private String chatId;

    private String employee;

    private Instant startedDate;

    private Instant endDate;

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
            ", chatId='" + getChatId() + "'" +
            ", employee='" + getEmployee() + "'" +
            ", startedDate='" + getStartedDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", job=" + getJob() +
            ", taskInfo=" + getTaskInfo() +
            "}";
    }
}
