import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAgency, Agency } from '../agency.model';
import { AgencyService } from '../service/agency.service';

@Injectable({ providedIn: 'root' })
export class AgencyRoutingResolveService implements Resolve<IAgency> {
  constructor(protected service: AgencyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAgency> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((agency: HttpResponse<Agency>) => {
          if (agency.body) {
            return of(agency.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Agency());
  }
}
