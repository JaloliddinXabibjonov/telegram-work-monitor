import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProfession, Profession } from '../profession.model';
import { ProfessionService } from '../service/profession.service';

@Injectable({ providedIn: 'root' })
export class ProfessionRoutingResolveService implements Resolve<IProfession> {
  constructor(protected service: ProfessionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProfession> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((profession: HttpResponse<Profession>) => {
          if (profession.body) {
            return of(profession.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Profession());
  }
}
