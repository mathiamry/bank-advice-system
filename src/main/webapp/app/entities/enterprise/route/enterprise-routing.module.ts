import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnterpriseComponent } from '../list/enterprise.component';
import { EnterpriseDetailComponent } from '../detail/enterprise-detail.component';
import { EnterpriseUpdateComponent } from '../update/enterprise-update.component';
import { EnterpriseRoutingResolveService } from './enterprise-routing-resolve.service';

const enterpriseRoute: Routes = [
  {
    path: '',
    component: EnterpriseComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnterpriseDetailComponent,
    resolve: {
      enterprise: EnterpriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnterpriseUpdateComponent,
    resolve: {
      enterprise: EnterpriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnterpriseUpdateComponent,
    resolve: {
      enterprise: EnterpriseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(enterpriseRoute)],
  exports: [RouterModule],
})
export class EnterpriseRoutingModule {}
