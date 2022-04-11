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
import { OrderUpdateComponent } from '../update/order-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrder, getOrderIdentifier } from '../order.model';
import { JobService } from '../../job/service/job.service';
import { TaskInfoService } from '../../task-info/service/task-info.service';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<IOrder>;
export type EntityArrayResponseType = HttpResponse<IOrder[]>;

@Injectable({ providedIn: 'root' })
export class OrderService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/orders');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService,
    private jobService: JobService,
    private taskInfoService: TaskInfoService
  ) {}

  create(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .post<IOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .put<IOrder>(`${this.resourceUrl}/${getOrderIdentifier(order) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .patch<IOrder>(`${this.resourceUrl}/${getOrderIdentifier(order) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOrderToCollectionIfMissing(orderCollection: IOrder[], ...ordersToCheck: (IOrder | null | undefined)[]): IOrder[] {
    const orders: IOrder[] = ordersToCheck.filter(isPresent);
    if (orders.length > 0) {
      const orderCollectionIdentifiers = orderCollection.map(orderItem => getOrderIdentifier(orderItem)!);
      const ordersToAdd = orders.filter(orderItem => {
        const orderIdentifier = getOrderIdentifier(orderItem);
        if (orderIdentifier == null || orderCollectionIdentifiers.includes(orderIdentifier)) {
          return false;
        }
        orderCollectionIdentifiers.push(orderIdentifier);
        return true;
      });
      return [...ordersToAdd, ...orderCollection];
    }
    return orderCollection;
  }

  protected convertDateFromClient(order: IOrder): IOrder {
    return Object.assign({}, order, {
      startedDate: order.startedDate?.isValid() ? order.startedDate.toJSON() : undefined,
      endDate: order.endDate?.isValid() ? order.endDate.toJSON() : undefined,
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
      res.body.forEach((order: IOrder) => {
        order.startedDate = order.startedDate ? dayjs(order.startedDate) : undefined;
        order.endDate = order.endDate ? dayjs(order.endDate) : undefined;
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
        translation: 'workmonitorApp.order.id',
      },
      {
        name: 'name.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workmonitorApp.order.name',
      },
      {
        name: 'chatId.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workmonitorApp.order.chatId',
      },
      {
        name: 'employee.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workmonitorApp.order.employee',
      },
      {
        name: 'startedDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workmonitorApp.order.startedDate',
      },
      {
        name: 'startedDate.lessThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'global.field.toDate',
      },
      {
        name: 'endDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workmonitorApp.order.endDate',
      },
      {
        name: 'endDate.lessThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'global.field.toDate',
      },
      {
        name: 'jobName.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workmonitorApp.order.job',
        options: {
          resourceUrl: this.jobService.resourceUrl,
          labelField: 'name',
          valueField: 'name',
        },
      },
      {
        name: 'taskInfoId.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workmonitorApp.order.taskInfo',
        options: {
          resourceUrl: this.taskInfoService.resourceUrl,
          labelField: 'id',
          valueField: 'id',
        },
      },
    ];
  }

  openUpdate(order?: IOrder): void {
    const options: EntityUpdateOptions<OrderUpdateComponent> = {
      component: OrderUpdateComponent,
      componentInstances: {
        order: order || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: IOrder): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.id', value: entity.id },
      {
        title: 'workmonitorApp.order.id',
        value: entity.id,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.order.name',
        value: entity.name,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.order.chatId',
        value: entity.chatId,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.order.employee',
        value: entity.employee,
        type: 'translation',
      },
      {
        title: 'workmonitorApp.order.startedDate',
        value: entity.startedDate,
        type: 'date',
      },
      {
        title: 'workmonitorApp.order.endDate',
        value: entity.endDate,
        type: 'date',
      },
      {
        title: 'workmonitorApp.order.job',
        value: entity.job?.name,
        type: 'link',
        link: () => this.jobService.openView(entity.job!),
      },
      {
        title: 'workmonitorApp.order.taskInfo',
        value: entity.taskInfo?.id,
        type: 'link',
        link: () => this.taskInfoService.openView(entity.taskInfo!),
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'orderListModification',
      alertTranslation: 'workmonitorApp.order.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'ORDER',
      entityId: id,
      i18nPrefix: 'workmonitorApp.order',
    };

    this.entityService.history(options);
  }
}
