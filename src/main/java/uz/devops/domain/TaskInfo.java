package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import uz.devops.domain.enumeration.Status;

/**
 * A TaskInfo.
 */
@Entity
@Table(name = "task_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Цена заказа
     */
    @Column(name = "price")
    private String price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    /**
     * Описание заказа
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "taskInfo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "professions", "job", "taskInfo" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "taskInfo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "job", "taskInfo" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaskInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrice() {
        return this.price;
    }

    public TaskInfo price(String price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Status getStatus() {
        return this.status;
    }

    public TaskInfo status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskInfo description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setTaskInfo(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setTaskInfo(this));
        }
        this.tasks = tasks;
    }

    public TaskInfo tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public TaskInfo addTask(Task task) {
        this.tasks.add(task);
        task.setTaskInfo(this);
        return this;
    }

    public TaskInfo removeTask(Task task) {
        this.tasks.remove(task);
        task.setTaskInfo(null);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setTaskInfo(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setTaskInfo(this));
        }
        this.orders = orders;
    }

    public TaskInfo orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public TaskInfo addOrder(Order order) {
        this.orders.add(order);
        order.setTaskInfo(this);
        return this;
    }

    public TaskInfo removeOrder(Order order) {
        this.orders.remove(order);
        order.setTaskInfo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskInfo)) {
            return false;
        }
        return id != null && id.equals(((TaskInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskInfo{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
