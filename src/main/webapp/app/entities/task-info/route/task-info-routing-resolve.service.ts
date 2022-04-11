import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskInfo, TaskInfo } from '../task-info.model';
import { TaskInfoService } from '../service/task-info.service';

@Injectable({ providedIn: 'root' })
export class TaskInfoRoutingResolveService implements Resolve<ITaskInfo> {
  constructor(protected service: TaskInfoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITaskInfo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((taskInfo: HttpResponse<TaskInfo>) => {
          if (taskInfo.body) {
            return of(taskInfo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TaskInfo());
  }
}
