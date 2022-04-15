import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IOrder, Order } from '../order.model';
import { OrderService } from '../service/order.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  templateUrl: './order-update.component.html',
})
export class OrderUpdateComponent extends BaseComponent implements OnInit, OnDestroy {
  isSaving = false;
  readonly order!: IOrder;
  statusValues = Object.keys(Status);

  jobs: IJob[] = [];
  StatusConstants = Object.keys(Status);

  editForm = this.fb.group({
    id: [],
    startedDate: [],
    endDate: [],
    status: [],
    job: [],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly orderService: OrderService,
    public readonly jobService: JobService,
    public readonly activeModal: NgbActiveModal,
    private readonly fb: FormBuilder
  ) {
    super();
  }

  ngOnDestroy(): void {
    this.unsubscribe();
  }

  ngOnInit(): void {
    this.updateForm(this.order);
    this.loadJobs();
  }

  private loadJobs() {
    this.jobService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(jobs => (this.jobs = jobs ?? []));
  }

  save(): void {
    this.loading = true;
    const order = this.createFromForm();
    if (order.id) {
      this.update(order);
    } else {
      this.create(order);
    }
  }

  private update(order: IOrder): void {
    this.orderService
      .update(order)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', order.id));
  }

  private create(order: IOrder): void {
    this.orderService
      .create(order)
      .pipe(
        pluck('id'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(id => this.onSuccess('created', id));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('orderListModification');
    this.activeModal.dismiss();
  }

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      startedDate: order.startedDate.format(DATE_TIME_FORMAT),
      endDate: order.endDate.format(DATE_TIME_FORMAT),
      status: order.status,
      job: order.job,
    });
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      id: this.editForm.get(['id'])!.value,
      startedDate: this.editForm.get(['startedDate'])!.value
        ? dayjs(this.editForm.get(['startedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      status: this.editForm.get(['status'])!.value,
      job: this.editForm.get(['job'])!.value,
    };
  }

  loadJobModification(value: any): void {
    this.eventManager.subscribe('JobListModified', () => {
      const query = {
        'id.equals': value,
      };
      this.jobService
        .query(query)
        .pipe(takeUntil(this.destroy$), debounceTime(1000))
        .subscribe(res => (this.jobs = res.body ?? []));
    });
  }
}
