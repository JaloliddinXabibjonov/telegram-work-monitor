package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.devops.domain.enumeration.Status;

/**
 * Заказы на работу
 */
@Entity
@Table(name = "jhi_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "employee")
    private String employee;

    @Column(name = "started_date")
    private Instant startedDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties(value = { "tasks", "orders" }, allowSetters = true)
    private Job job;

    @ManyToOne
    @JsonIgnoreProperties(value = { "tasks", "orders" }, allowSetters = true)
    private TaskInfo taskInfo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Order name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatId() {
        return this.chatId;
    }

    public Order chatId(String chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getEmployee() {
        return this.employee;
    }

    public Order employee(String employee) {
        this.setEmployee(employee);
        return this;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Instant getStartedDate() {
        return this.startedDate;
    }

    public Order startedDate(Instant startedDate) {
        this.setStartedDate(startedDate);
        return this;
    }

    public void setStartedDate(Instant startedDate) {
        this.startedDate = startedDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Order endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Order job(Job job) {
        this.setJob(job);
        return this;
    }

    public TaskInfo getTaskInfo() {
        return this.taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public Order taskInfo(TaskInfo taskInfo) {
        this.setTaskInfo(taskInfo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", chatId='" + getChatId() + "'" +
            ", employee='" + getEmployee() + "'" +
            ", startedDate='" + getStartedDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
