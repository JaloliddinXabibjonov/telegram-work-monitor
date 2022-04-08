import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { IProfession } from 'app/entities/profession/profession.model';
import { ProfessionService } from 'app/entities/profession/service/profession.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { Status } from 'app/entities/enumerations/status.model';

@Component({
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent extends BaseComponent implements OnInit {
  isSaving = false;
  readonly task!: ITask;
  statusValues = Object.keys(Status);

  professions: IProfession[] = [];
  jobs: IJob[] = [];
  StatusConstants = Object.keys(Status);

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
    status: [],
    professions: [],
    job: [null, Validators.required],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly taskService: TaskService,
    public readonly professionService: ProfessionService,
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
    this.updateForm(this.task);
    this.loadProfessions();
    this.loadJobs();
  }

  private loadProfessions() {
    this.professionService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(professions => (this.professions = professions ?? []));
  }
  private loadJobs() {
    this.jobService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(jobs => (this.jobs = jobs ?? []));
  }

  save(): void {
    this.loading = true;
    const task = this.createFromForm();
    if (task.id) {
      this.update(task);
    } else {
      this.create(task);
    }
  }

  private update(task: ITask): void {
    this.taskService
      .update(task)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', task.id));
  }

  private create(task: ITask): void {
    this.taskService
      .create(task)
      .pipe(
        pluck('id'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(id => this.onSuccess('created', id));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('TaskListModified');
    this.alertService.success('workMonitorApp.task.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(task: ITask): void {
    this.editForm.patchValue({
      id: task.id,
      name: task.name,
      status: task.status,
      professions: task.professions,
      job: task.job,
    });
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      status: this.editForm.get(['status'])!.value,
      professions: this.editForm.get(['professions'])!.value,
      job: this.editForm.get(['job'])!.value,
    };
  }

  loadProfessionModification(value: any): void {
    this.eventManager.subscribe('ProfessionListModified', () => {
      const query = {
        'id.equals': value,
      };
      this.professionService
        .query(query)
        .pipe(takeUntil(this.destroy$), debounceTime(1000))
        .subscribe(res => (this.professions = res.body ?? []));
    });
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
