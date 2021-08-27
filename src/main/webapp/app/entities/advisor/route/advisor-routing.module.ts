import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AdvisorComponent } from '../list/advisor.component';
import { AdvisorDetailComponent } from '../detail/advisor-detail.component';
import { AdvisorUpdateComponent } from '../update/advisor-update.component';
import { AdvisorRoutingResolveService } from './advisor-routing-resolve.service';

const advisorRoute: Routes = [
  {
    path: '',
    component: AdvisorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AdvisorDetailComponent,
    resolve: {
      advisor: AdvisorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AdvisorUpdateComponent,
    resolve: {
      advisor: AdvisorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AdvisorUpdateComponent,
    resolve: {
      advisor: AdvisorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(advisorRoute)],
  exports: [RouterModule],
})
export class AdvisorRoutingModule {}
