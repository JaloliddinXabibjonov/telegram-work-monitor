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
    private jobService: JobService
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
      {
        name: 'id.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workMonitorApp.order.id',
      },
      {
        name: 'startedDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workMonitorApp.order.startedDate',
      },
      {
        name: 'endDate.greaterThanOrEqual',
        type: NglFilterFieldType.DATE,
        translation: 'workMonitorApp.order.endDate',
      },
      {
        name: 'status.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.order.status',
        options: {
          'select.resourceUrl': this.resourceUrl,
          'select.nzLabel': 'status',
          'select.nzValue': 'status',
        },
      },
      {
        name: 'jobId.in',
        type: NglFilterFieldType.MULTI_SELECT,
        translation: 'workMonitorApp.order.job',
        options: {
          'select.resourceUrl': this.jobService.resourceUrl,
          'select.nzLabel': 'name',
          'select.nzValue': 'name',
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
        title: 'workMonitorApp.order.startedDate',
        value: entity.startedDate,
        type: 'date',
      },
      {
        title: 'workMonitorApp.order.endDate',
        value: entity.endDate,
        type: 'date',
      },
      {
        title: 'workMonitorApp.order.status',
        value: entity.status,
      },
      {
        title: 'workMonitorApp.order.job',
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
      event: 'orderListModification',
      alertTranslation: 'workMonitorApp.order.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'ORDER',
      entityId: id,
      i18nPrefix: 'workMonitorApp.order',
    };

    this.entityService.history(options);
  }
}
