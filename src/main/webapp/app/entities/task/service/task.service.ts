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
import { JobService } from '../../job/service/job.service';
import { ProfessionService } from '../../profession/service/profession.service';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';
import { find } from 'rxjs/operators';
import { IJob } from '../../job/job.model';

export type EntityResponseType = HttpResponse<ITask>;
export type EntityArrayResponseType = HttpResponse<ITask[]>;

@Injectable({ providedIn: 'root' })
export class TaskService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/tasks');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService,
    private jobService: JobService,
    private professionService: ProfessionService
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
        name: 'price.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.task.price',
      },
      {
        name: 'description.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.task.description',
      },
      {
        name: 'priority.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workMonitorApp.task.priority',
      },
      {
        name: 'jobId.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.task.job',
        options: {
          'select.resourceUrl': this.jobService.resourceUrl,
          'select.nzLabel': 'name',
          'select.nzValue': 'name',
        },
      },
      {
        name: 'professionName.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.task.profession',
        options: {
          'select.resourceUrl': this.professionService.resourceUrl,
          'select.nzLabel': 'name',
          'select.nzValue': 'name',
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
      {
        title: 'workMonitorApp.task.id',
        value: entity.id,
      },
      {
        title: 'workMonitorApp.task.name',
        value: entity.name,
      },
      {
        title: 'workMonitorApp.task.price',
        value: entity.price,
      },
      {
        title: 'workMonitorApp.task.description',
        value: entity.description,
      },
      {
        title: 'workMonitorApp.task.priority',
        value: entity.priority,
      },
      {
        title: 'workMonitorApp.task.job',
        value: entity.job?.id,
        type: 'link',
        link: () => this.jobService.openView(entity.job),
      },
      {
        title: 'workMonitorApp.task.profession',
        value: entity.professions?.map(item => item.name),
        type: 'link',
        link: (value: any) => this.professionService.openView(entity.professions?.find(item => item.name === value)!),
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'TaskListModified',
      // alertTranslation: 'workMonitorApp.task.deleted',
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
