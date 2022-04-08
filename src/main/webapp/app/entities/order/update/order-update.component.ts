import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IOrder, Order } from '../order.model';
import { OrderService } from '../service/order.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

@Component({
  templateUrl: './order-update.component.html',
})
export class OrderUpdateComponent extends BaseComponent implements OnInit {
  isSaving = false;
  readonly order!: IOrder;
  orderStatusValues = Object.keys(OrderStatus);

  jobs: IJob[] = [];
  OrderStatusConstants = Object.keys(OrderStatus);

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
    price: [],
    chatId: [],
    employee: [],
    status: [],
    description: [],
    startedDate: [],
    endDate: [],
    job: [null, Validators.required],
  });

  constructor(
    private readonly dataUtils: DataUtils,
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

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('workMonitorApp.error', { ...err, key: 'error.file.' + err.key })),
    });
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
    this.eventManager.broadcast('OrderListModified');
    this.alertService.success('workMonitorApp.order.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      id: order.id,
      name: order.name,
      price: order.price,
      chatId: order.chatId,
      employee: order.employee,
      status: order.status,
      description: order.description,
      startedDate: order.startedDate ? order.startedDate.format(DATE_TIME_FORMAT) : null,
      endDate: order.endDate ? order.endDate.format(DATE_TIME_FORMAT) : null,
      job: order.job,
    });
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
      chatId: this.editForm.get(['chatId'])!.value,
      employee: this.editForm.get(['employee'])!.value,
      status: this.editForm.get(['status'])!.value,
      description: this.editForm.get(['description'])!.value,
      startedDate: this.editForm.get(['startedDate'])!.value
        ? dayjs(this.editForm.get(['startedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
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