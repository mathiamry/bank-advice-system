import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AgencyComponent } from '../list/agency.component';
import { AgencyDetailComponent } from '../detail/agency-detail.component';
import { AgencyUpdateComponent } from '../update/agency-update.component';
import { AgencyRoutingResolveService } from './agency-routing-resolve.service';

const agencyRoute: Routes = [
  {
    path: '',
    component: AgencyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AgencyDetailComponent,
    resolve: {
      agency: AgencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AgencyUpdateComponent,
    resolve: {
      agency: AgencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AgencyUpdateComponent,
    resolve: {
      agency: AgencyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(agencyRoute)],
  exports: [RouterModule],
})
export class AgencyRoutingModule {}
