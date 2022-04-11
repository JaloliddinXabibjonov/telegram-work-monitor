import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import { ITaskInfo, TaskInfo } from '../task-info.model';
import { TaskInfoService } from '../service/task-info.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

import { Status } from 'app/entities/enumerations/status.model';

@Component({
  templateUrl: './task-info-update.component.html',
})
export class TaskInfoUpdateComponent extends BaseComponent implements OnInit {
  isSaving = false;
  readonly taskInfo!: ITaskInfo;
  statusValues = Object.keys(Status);

  StatusConstants = Object.keys(Status);

  editForm = this.fb.group({
    id: [],
    price: [],
    status: [],
    description: [],
  });

  constructor(
    private readonly dataUtils: DataUtils,
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly taskInfoService: TaskInfoService,
    public readonly activeModal: NgbActiveModal,
    private readonly fb: FormBuilder
  ) {
    super();
  }

  ngOnDestroy(): void {
    this.unsubscribe();
  }

  ngOnInit(): void {
    this.updateForm(this.taskInfo);
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
        this.eventManager.broadcast(new EventWithContent<AlertError>('workmonitorApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  save(): void {
    this.loading = true;
    const taskInfo = this.createFromForm();
    if (taskInfo.id) {
      this.update(taskInfo);
    } else {
      this.create(taskInfo);
    }
  }

  private update(taskInfo: ITaskInfo): void {
    this.taskInfoService
      .update(taskInfo)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', taskInfo.id));
  }

  private create(taskInfo: ITaskInfo): void {
    this.taskInfoService
      .create(taskInfo)
      .pipe(
        pluck('id'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(id => this.onSuccess('created', id));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('TaskInfoListModified');
    this.alertService.success('workmonitorApp.taskInfo.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(taskInfo: ITaskInfo): void {
    this.editForm.patchValue({
      id: taskInfo.id,
      price: taskInfo.price,
      status: taskInfo.status,
      description: taskInfo.description,
    });
  }

  protected createFromForm(): ITaskInfo {
    return {
      ...new TaskInfo(),
      id: this.editForm.get(['id'])!.value,
      price: this.editForm.get(['price'])!.value,
      status: this.editForm.get(['status'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
