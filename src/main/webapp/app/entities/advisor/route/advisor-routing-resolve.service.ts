import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAdvisor, Advisor } from '../advisor.model';
import { AdvisorService } from '../service/advisor.service';

@Injectable({ providedIn: 'root' })
export class AdvisorRoutingResolveService implements Resolve<IAdvisor> {
  constructor(protected service: AdvisorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAdvisor> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((advisor: HttpResponse<Advisor>) => {
          if (advisor.body) {
            return of(advisor.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Advisor());
  }
}
