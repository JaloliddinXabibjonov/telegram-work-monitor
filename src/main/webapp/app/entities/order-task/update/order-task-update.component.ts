import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IOrderTask, OrderTask } from '../order-task.model';
import { OrderTaskService } from '../service/order-task.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  templateUrl: './order-task-update.component.html',
})
export class OrderTaskUpdateComponent extends BaseComponent implements OnInit {
  isSaving = false;
  readonly orderTask!: IOrderTask;
  statusValues = Object.keys(Status);

  tasks: ITask[] = [];
  orders: IOrder[] = [];
  StatusConstants = Object.keys(Status);

  editForm = this.fb.group({
    id: [],
    status: [],
    startedDate: [],
    endDate: [],
    employeeUsername: [],
    task: [],
    order: [],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly orderTaskService: OrderTaskService,
    public readonly taskService: TaskService,
    public readonly orderService: OrderService,
    public readonly activeModal: NgbActiveModal,
    private readonly fb: FormBuilder
  ) {
    super();
  }

  ngOnDestroy(): void {
    this.unsubscribe();
  }

  ngOnInit(): void {
    this.updateForm(this.orderTask);
    this.loadTasks();
    this.loadOrders();
  }

  private loadTasks() {
    this.taskService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(tasks => (this.tasks = tasks ?? []));
  }
  private loadOrders() {
    this.orderService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(orders => (this.orders = orders ?? []));
  }

  save(): void {
    this.loading = true;
    const orderTask = this.createFromForm();
    if (orderTask.id) {
      this.update(orderTask);
    } else {
      this.create(orderTask);
    }
  }

  private update(orderTask: IOrderTask): void {
    this.orderTaskService
      .update(orderTask)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', orderTask.id));
  }

  private create(orderTask: IOrderTask): void {
    this.orderTaskService
      .create(orderTask)
      .pipe(
        pluck('id'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(id => this.onSuccess('created', id));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('OrderTaskListModified');
    this.alertService.success('workMonitorApp.orderTask.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(orderTask: IOrderTask): void {
    this.editForm.patchValue({
      id: orderTask.id,
      status: orderTask.status,
      startedDate: orderTask.startedDate ? orderTask.startedDate.format(DATE_TIME_FORMAT) : null,
      endDate: orderTask.endDate ? orderTask.endDate.format(DATE_TIME_FORMAT) : null,
      employeeUsername: orderTask.employeeUsername,
      task: orderTask.task,
      order: orderTask.order,
    });
  }

  protected createFromForm(): IOrderTask {
    return {
      ...new OrderTask(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      startedDate: this.editForm.get(['startedDate'])!.value
        ? dayjs(this.editForm.get(['startedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      employeeUsername: this.editForm.get(['employeeUsername'])!.value,
      task: this.editForm.get(['task'])!.value,
      order: this.editForm.get(['order'])!.value,
    };
  }

  loadTaskModification(value: any): void {
    this.eventManager.subscribe('TaskListModified', () => {
      const query = {
        'id.equals': value,
      };
      this.taskService
        .query(query)
        .pipe(takeUntil(this.destroy$), debounceTime(1000))
        .subscribe(res => (this.tasks = res.body ?? []));
    });
  }

  loadOrderModification(value: any): void {
    this.eventManager.subscribe('OrderListModified', () => {
      const query = {
        'id.equals': value,
      };
      this.orderService
        .query(query)
        .pipe(takeUntil(this.destroy$), debounceTime(1000))
        .subscribe(res => (this.orders = res.body ?? []));
    });
  }
}
