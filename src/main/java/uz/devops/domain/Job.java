package uz.devops.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Справочник для описания вида этапа работы
 */
@Entity
@Table(name = "job")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Job implements Serializable {

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

    @OneToMany(mappedBy = "job")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "professions", "job" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "job")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "job" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Job id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Job name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setJob(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setJob(this));
        }
        this.tasks = tasks;
    }

    public Job tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Job addTask(Task task) {
        this.tasks.add(task);
        task.setJob(this);
        return this;
    }

    public Job removeTask(Task task) {
        this.tasks.remove(task);
        task.setJob(null);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setJob(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setJob(this));
        }
        this.orders = orders;
    }

    public Job orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Job addOrder(Order order) {
        this.orders.add(order);
        order.setJob(this);
        return this;
    }

    public Job removeOrder(Order order) {
        this.orders.remove(order);
        order.setJob(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        return id != null && id.equals(((Job) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
