import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { debounceTime, finalize, pluck, takeUntil } from 'rxjs/operators';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { IProfession } from 'app/entities/profession/profession.model';
import { ProfessionService } from 'app/entities/profession/service/profession.service';

@Component({
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent extends BaseComponent implements OnInit {
  isSaving = false;
  readonly task!: ITask;

  jobs: IJob[] = [];
  professions: IProfession[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
    price: [],
    description: [],
    priority: [],
    job: [],
    professions: [],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly taskService: TaskService,
    public readonly jobService: JobService,
    public readonly professionService: ProfessionService,
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
    this.loadJobs();
    this.loadProfessions();
  }

  private loadJobs() {
    this.jobService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(jobs => (this.jobs = jobs ?? []));
  }
  private loadProfessions() {
    this.professionService
      .query()
      .pipe(pluck('body'), takeUntil(this.destroy$))
      .subscribe(professions => (this.professions = professions ?? []));
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
      price: task.price,
      description: task.description,
      priority: task.priority,
      job: task.job,
      professions: task.professions,
    });
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      price: this.editForm.get(['price'])!.value,
      description: this.editForm.get(['description'])!.value,
      priority: this.editForm.get(['priority'])!.value,
      job: this.editForm.get(['job'])!.value,
      professions: this.editForm.get(['professions'])!.value,
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
}
