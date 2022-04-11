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
 * Роли
 */
@Entity
@Table(name = "profession")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Profession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @Id
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    /**
     * Описание профессии
     */
    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "professions")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "professions", "job", "taskInfo" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getName() {
        return this.name;
    }

    public Profession name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Profession description(String description) {
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
            this.tasks.forEach(i -> i.removeProfession(this));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.addProfession(this));
        }
        this.tasks = tasks;
    }

    public Profession tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Profession addTask(Task task) {
        this.tasks.add(task);
        task.getProfessions().add(this);
        return this;
    }

    public Profession removeTask(Task task) {
        this.tasks.remove(task);
        task.getProfessions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profession)) {
            return false;
        }
        return name != null && name.equals(((Profession) o).name);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profession{" +
            "name=" + getName() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
