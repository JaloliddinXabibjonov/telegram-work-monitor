package uz.devops.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import uz.devops.domain.enumeration.Status;

/**
 * Criteria class for the {@link uz.devops.domain.OrderTask} entity. This class is used
 * in {@link uz.devops.web.rest.OrderTaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderTaskCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StatusFilter status;

    private InstantFilter startedDate;

    private InstantFilter endDate;

    private StringFilter employeeUsername;

    private LongFilter taskId;

    private LongFilter orderId;

    private Boolean distinct;

    public OrderTaskCriteria() {}

    public OrderTaskCriteria(OrderTaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.startedDate = other.startedDate == null ? null : other.startedDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.employeeUsername = other.employeeUsername == null ? null : other.employeeUsername.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderTaskCriteria copy() {
        return new OrderTaskCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public StatusFilter status() {
        if (status == null) {
            status = new StatusFilter();
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public InstantFilter getStartedDate() {
        return startedDate;
    }

    public InstantFilter startedDate() {
        if (startedDate == null) {
            startedDate = new InstantFilter();
        }
        return startedDate;
    }

    public void setStartedDate(InstantFilter startedDate) {
        this.startedDate = startedDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            endDate = new InstantFilter();
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public StringFilter getEmployeeUsername() {
        return employeeUsername;
    }

    public StringFilter employeeUsername() {
        if (employeeUsername == null) {
            employeeUsername = new StringFilter();
        }
        return employeeUsername;
    }

    public void setEmployeeUsername(StringFilter employeeUsername) {
        this.employeeUsername = employeeUsername;
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

    public LongFilter getOrderId() {
        return orderId;
    }

    public LongFilter orderId() {
        if (orderId == null) {
            orderId = new LongFilter();
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
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
        final OrderTaskCriteria that = (OrderTaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(startedDate, that.startedDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(employeeUsername, that.employeeUsername) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, startedDate, endDate, employeeUsername, taskId, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderTaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (startedDate != null ? "startedDate=" + startedDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (employeeUsername != null ? "employeeUsername=" + employeeUsername + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
