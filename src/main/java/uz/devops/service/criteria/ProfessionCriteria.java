package uz.devops.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link uz.devops.domain.Profession} entity. This class is used
 * in {@link uz.devops.web.rest.ProfessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /professions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProfessionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private StringFilter name;

    private StringFilter description;

    private LongFilter taskId;

    private Boolean distinct;

    public ProfessionCriteria() {}

    public ProfessionCriteria(ProfessionCriteria other) {
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProfessionCriteria copy() {
        return new ProfessionCriteria(this);
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getTaskId() {
        return taskId;
    }

    public LongFilter taskId() {
        if (taskId == null) {
            taskId = new LongFilter();
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProfessionCriteria that = (ProfessionCriteria) o;
        return (
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfessionCriteria{" +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
