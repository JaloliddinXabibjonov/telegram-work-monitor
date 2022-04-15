import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize, pluck, takeUntil } from 'rxjs/operators';

import { IJob, Job } from '../job.model';
import { JobService } from '../service/job.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';

@Component({
  templateUrl: './job-update.component.html',
})
export class JobUpdateComponent extends BaseComponent implements OnInit, OnDestroy {
  isSaving = false;
  readonly job!: IJob;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
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
    this.updateForm(this.job);
  }

  save(): void {
    this.loading = true;
    const job = this.createFromForm();
    if (job.id) {
      this.update(job);
    } else {
      this.create(job);
    }
  }

  private update(job: IJob): void {
    this.jobService
      .update(job)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', job.id));
  }

  private create(job: IJob): void {
    this.jobService
      .create(job)
      .pipe(
        pluck('id'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(id => this.onSuccess('created', id));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('JobListModified');
    this.alertService.success('workMonitorApp.job.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(job: IJob): void {
    this.editForm.patchValue({
      id: job.id,
      name: job.name,
    });
  }

  protected createFromForm(): IJob {
    return {
      ...new Job(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }
}
