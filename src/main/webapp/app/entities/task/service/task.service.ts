import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntityConfig } from '../../../shared/entity/entity-config.model';
import { EntityService } from '../../../shared/entity/entity.service';
import { EntityUpdateOptions } from '../../../shared/entity/entity-update/entity-update-options.model';
import { EntityViewOptions } from '../../../shared/entity/entity-view/entity-view-options.model';
import { TaskUpdateComponent } from '../update/task-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { ITask, getTaskIdentifier } from '../task.model';
import { ProfessionService } from '../../profession/service/profession.service';
import { JobService } from '../../job/service/job.service';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<ITask>;
export type EntityArrayResponseType = HttpResponse<ITask[]>;

@Injectable({ providedIn: 'root' })
export class TaskService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/tasks');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService,
    private professionService: ProfessionService,
    private jobService: JobService
  ) {}

  create(task: ITask): Observable<EntityResponseType> {
    return this.http.post<ITask>(this.resourceUrl, task, { observe: 'response' });
  }

  update(task: ITask): Observable<EntityResponseType> {
    return this.http.put<ITask>(`${this.resourceUrl}/${getTaskIdentifier(task) as number}`, task, { observe: 'response' });
  }

  partialUpdate(task: ITask): Observable<EntityResponseType> {
    return this.http.patch<ITask>(`${this.resourceUrl}/${getTaskIdentifier(task) as number}`, task, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITask>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITask[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTaskToCollectionIfMissing(taskCollection: ITask[], ...tasksToCheck: (ITask | null | undefined)[]): ITask[] {
    const tasks: ITask[] = tasksToCheck.filter(isPresent);
    if (tasks.length > 0) {
      const taskCollectionIdentifiers = taskCollection.map(taskItem => getTaskIdentifier(taskItem)!);
      const tasksToAdd = tasks.filter(taskItem => {
        const taskIdentifier = getTaskIdentifier(taskItem);
        if (taskIdentifier == null || taskCollectionIdentifiers.includes(taskIdentifier)) {
          return false;
        }
        taskCollectionIdentifiers.push(taskIdentifier);
        return true;
      });
      return [...tasksToAdd, ...taskCollection];
    }
    return taskCollection;
  }

  getFilterFields(): NglFilterField[] {
    return [
      { name: 'id.equals', type: NglFilterFieldType.NUMBER, translation: 'global.field.id' },
      {
        name: 'id.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workMonitorApp.task.id',
      },
      {
        name: 'name.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.task.name',
      },
      {
        name: 'status.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.task.status',
      },
      {
        name: 'professionName.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.task.profession',
        options: {
          resourceUrl: this.professionService.resourceUrl,
          labelField: 'name',
          valueField: 'name',
        },
      },
      {
        name: 'jobName.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.task.job',
        options: {
          resourceUrl: this.jobService.resourceUrl,
          labelField: 'name',
          valueField: 'name',
        },
      },
    ];
  }

  openUpdate(task?: ITask): void {
    const options: EntityUpdateOptions<TaskUpdateComponent> = {
      component: TaskUpdateComponent,
      componentInstances: {
        task: task || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: ITask): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.id', value: entity.id },
      {
        title: 'workMonitorApp.task.id',
        value: entity.id,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.task.name',
        value: entity.name,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.task.status',
        value: entity.status,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.task.profession',
        value: entity.professions?.map(item => item.name),
        type: 'link',
        link: (value: any) => this.professionService.openView(entity.professions?.find(item => item.name === value)!),
      },
      {
        title: 'workMonitorApp.task.job',
        value: entity.job?.name,
        type: 'link',
        link: () => this.jobService.openView(entity.job!),
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'taskListModification',
      alertTranslation: 'workMonitorApp.task.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'TASK',
      entityId: id,
      i18nPrefix: 'workMonitorApp.task',
    };

    this.entityService.history(options);
  }
}
