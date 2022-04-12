import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderTask, OrderTask } from '../order-task.model';
import { OrderTaskService } from '../service/order-task.service';

@Injectable({ providedIn: 'root' })
export class OrderTaskRoutingResolveService implements Resolve<IOrderTask> {
  constructor(protected service: OrderTaskService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderTask> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderTask: HttpResponse<OrderTask>) => {
          if (orderTask.body) {
            return of(orderTask.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrderTask());
  }
}
