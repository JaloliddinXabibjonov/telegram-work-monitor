package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.devops.domain.enumeration.Status;

/**
 * A OrderTask.
 */
@Entity
@Table(name = "order_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrderTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    /**
     * Дата начала
     */
    @Column(name = "started_date")
    private Instant startedDate;

    /**
     * Дата окончания
     */
    @Column(name = "end_date")
    private Instant endDate;

    /**
     * Имя сотрудника
     */
    @Column(name = "employee_username")
    private String employeeUsername;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties(value = { "job", "professions" }, allowSetters = true)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIgnoreProperties(value = { "job" }, allowSetters = true)
    private Order order;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return this.status;
    }

    public OrderTask status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getStartedDate() {
        return this.startedDate;
    }

    public OrderTask startedDate(Instant startedDate) {
        this.setStartedDate(startedDate);
        return this;
    }

    public void setStartedDate(Instant startedDate) {
        this.startedDate = startedDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public OrderTask endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getEmployeeUsername() {
        return this.employeeUsername;
    }

    public OrderTask employeeUsername(String employeeUsername) {
        this.setEmployeeUsername(employeeUsername);
        return this;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public OrderTask task(Task task) {
        this.setTask(task);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderTask order(Order order) {
        this.setOrder(order);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderTask)) {
            return false;
        }
        return id != null && id.equals(((OrderTask) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderTask{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", startedDate='" + getStartedDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", employeeUsername='" + getEmployeeUsername() + "'" +
            "}";
    }
}
