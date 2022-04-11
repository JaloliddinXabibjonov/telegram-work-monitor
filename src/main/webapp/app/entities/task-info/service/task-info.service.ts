import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntityConfig } from '../../../shared/entity/entity-config.model';
import { EntityService } from '../../../shared/entity/entity.service';
import { EntityUpdateOptions } from '../../../shared/entity/entity-update/entity-update-options.model';
import { EntityViewOptions } from '../../../shared/entity/entity-view/entity-view-options.model';
import { TaskInfoUpdateComponent } from '../update/task-info-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskInfo, getTaskInfoIdentifier } from '../task-info.model';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<ITaskInfo>;
export type EntityArrayResponseType = HttpResponse<ITaskInfo[]>;

@Injectable({ providedIn: 'root' })
export class TaskInfoService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/task-infos');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService
  ) {}

  create(taskInfo: ITaskInfo): Observable<EntityResponseType> {
    return this.http.post<ITaskInfo>(this.resourceUrl, taskInfo, { observe: 'response' });
  }

  update(taskInfo: ITaskInfo): Observable<EntityResponseType> {
    return this.http.put<ITaskInfo>(`${this.resourceUrl}/${getTaskInfoIdentifier(taskInfo) as number}`, taskInfo, { observe: 'response' });
  }

  partialUpdate(taskInfo: ITaskInfo): Observable<EntityResponseType> {
    return this.http.patch<ITaskInfo>(`${this.resourceUrl}/${getTaskInfoIdentifier(taskInfo) as number}`, taskInfo, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITaskInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITaskInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTaskInfoToCollectionIfMissing(taskInfoCollection: ITaskInfo[], ...taskInfosToCheck: (ITaskInfo | null | undefined)[]): ITaskInfo[] {
    const taskInfos: ITaskInfo[] = taskInfosToCheck.filter(isPresent);
    if (taskInfos.length > 0) {
      const taskInfoCollectionIdentifiers = taskInfoCollection.map(taskInfoItem => getTaskInfoIdentifier(taskInfoItem)!);
      const taskInfosToAdd = taskInfos.filter(taskInfoItem => {
        const taskInfoIdentifier = getTaskInfoIdentifier(taskInfoItem);
        if (taskInfoIdentifier == null || taskInfoCollectionIdentifiers.includes(taskInfoIdentifier)) {
          return false;
        }
        taskInfoCollectionIdentifiers.push(taskInfoIdentifier);
        return true;
      });
      return [...taskInfosToAdd, ...taskInfoCollection];
    }
    return taskInfoCollection;
  }

  getFilterFields(): NglFilterField[] {
    return [
      { name: 'id.equals', type: NglFilterFieldType.NUMBER, translation: 'global.field.id' },
      {
        name: 'id.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workmonitorApp.taskInfo.id',
      },
      {
        name: 'price.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workmonitorApp.taskInfo.price',
      },
      {
        name: 'status.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workmonitorApp.taskInfo.status',
      },
    ];
  }

  openUpdate(taskInfo?: ITaskInfo): void {
    const options: EntityUpdateOptions<TaskInfoUpdateComponent> = {
      component: TaskInfoUpdateComponent,
      componentInstances: {
        taskInfo: taskInfo || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: ITaskInfo): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.id', value: entity.id },
      {
        title: 'workmonitorApp.taskInfo.id',
        value: entity.id,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.taskInfo.price',
        value: entity.price,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.taskInfo.status',
        value: entity.status,
        type: 'translation',
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'taskInfoListModification',
      alertTranslation: 'workmonitorApp.taskInfo.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'TASK_INFO',
      entityId: id,
      i18nPrefix: 'workmonitorApp.taskInfo',
    };

    this.entityService.history(options);
  }
}
