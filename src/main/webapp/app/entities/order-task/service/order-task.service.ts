import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntityConfig } from '../../../shared/entity/entity-config.model';
import { EntityService } from '../../../shared/entity/entity.service';
import { EntityUpdateOptions } from '../../../shared/entity/entity-update/entity-update-options.model';
import { EntityViewOptions } from '../../../shared/entity/entity-view/entity-view-options.model';
import { OrderTaskUpdateComponent } from '../update/order-task-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrderTask, getOrderTaskIdentifier } from '../order-task.model';
import { TaskService } from '../../task/service/task.service';
import { OrderService } from '../../order/service/order.service';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<IOrderTask>;
export type EntityArrayResponseType = HttpResponse<IOrderTask[]>;

@Injectable({ providedIn: 'root' })
export class OrderTaskService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/order-tasks');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService,
    private taskService: TaskService,
    private orderService: OrderService
  ) {}

  create(orderTask: IOrderTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderTask);
    return this.http
      .post<IOrderTask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(orderTask: IOrderTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderTask);
    return this.http
      .put<IOrderTask>(`${this.resourceUrl}/${getOrderTaskIdentifier(orderTask) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(orderTask: IOrderTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderTask);
    return this.http
      .patch<IOrderTask>(`${this.resourceUrl}/${getOrderTaskIdentifier(orderTask) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOrderTask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOrderTask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOrderTaskToCollectionIfMissing(
    orderTaskCollection: IOrderTask[],
    ...orderTasksToCheck: (IOrderTask | null | undefined)[]
  ): IOrderTask[] {
    const orderTasks: IOrderTask[] = orderTasksToCheck.filter(isPresent);
    if (orderTasks.length > 0) {
      const orderTaskCollectionIdentifiers = orderTaskCollection.map(orderTaskItem => getOrderTaskIdentifier(orderTaskItem)!);
      const orderTasksToAdd = orderTasks.filter(orderTaskItem => {
        const orderTaskIdentifier = getOrderTaskIdentifier(orderTaskItem);
        if (orderTaskIdentifier == null || orderTaskCollectionIdentifiers.includes(orderTaskIdentifier)) {
          return false;
        }
        orderTaskCollectionIdentifiers.push(orderTaskIdentifier);
        return true;
      });
      return [...orderTasksToAdd, ...orderTaskCollection];
    }
    return orderTaskCollection;
  }

  protected convertDateFromClient(orderTask: IOrderTask): IOrderTask {
    return Object.assign({}, orderTask, {
      startedDate: orderTask.startedDate?.isValid() ? orderTask.startedDate.toJSON() : undefined,
      endDate: orderTask.endDate?.isValid() ? orderTask.endDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startedDate = res.body.startedDate ? dayjs(res.body.startedDate) : undefined;
      res.body.endDate = res.body.endDate ? dayjs(res.body.endDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((orderTask: IOrderTask) => {
        orderTask.startedDate = orderTask.startedDate ? dayjs(orderTask.startedDate) : undefined;
        orderTask.endDate = orderTask.endDate ? dayjs(orderTask.endDate) : undefined;
      });
    }
    return res;
  }

  getFilterFields(): NglFilterField[] {
    return [
      { name: 'id.equals', type: NglFilterFieldType.NUMBER, translation: 'global.field.id' },
      {
        name: 'id.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workMonitorApp.orderTask.id',
      },
      {
        name: 'status.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.orderTask.status',
      },
      {
        name: 'startedDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workMonitorApp.orderTask.startedDate',
      },
      {
        name: 'startedDate.lessThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'global.field.toDate',
      },
      {
        name: 'endDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workMonitorApp.orderTask.endDate',
      },
      {
        name: 'endDate.lessThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'global.field.toDate',
      },
      {
        name: 'employeeUsername.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.orderTask.employeeUsername',
      },
      {
        name: 'taskId.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.orderTask.task',
        options: {
          resourceUrl: this.taskService.resourceUrl,
          labelField: 'id',
          valueField: 'id',
        },
      },
      {
        name: 'orderId.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.orderTask.order',
        options: {
          resourceUrl: this.orderService.resourceUrl,
          labelField: 'id',
          valueField: 'id',
        },
      },
    ];
  }

  openUpdate(orderTask?: IOrderTask): void {
    const options: EntityUpdateOptions<OrderTaskUpdateComponent> = {
      component: OrderTaskUpdateComponent,
      componentInstances: {
        orderTask: orderTask || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: IOrderTask): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.id', value: entity.id },
      {
        title: 'workMonitorApp.orderTask.id',
        value: entity.id,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.orderTask.status',
        value: entity.status,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.orderTask.startedDate',
        value: entity.startedDate,
        type: 'date',
      },
      {
        title: 'workMonitorApp.orderTask.endDate',
        value: entity.endDate,
        type: 'date',
      },
      {
        title: 'workMonitorApp.orderTask.employeeUsername',
        value: entity.employeeUsername,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.orderTask.task',
        value: entity.task?.id,
        type: 'link',
        link: () => this.taskService.openView(entity.task!),
      },
      {
        title: 'workMonitorApp.orderTask.order',
        value: entity.order?.id,
        type: 'link',
        link: () => this.orderService.openView(entity.order!),
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'orderTaskListModification',
      alertTranslation: 'workMonitorApp.orderTask.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'ORDER_TASK',
      entityId: id,
      i18nPrefix: 'workMonitorApp.orderTask',
    };

    this.entityService.history(options);
  }
}
