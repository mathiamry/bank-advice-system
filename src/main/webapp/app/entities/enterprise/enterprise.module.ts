import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnterpriseComponent } from './list/enterprise.component';
import { EnterpriseDetailComponent } from './detail/enterprise-detail.component';
import { EnterpriseUpdateComponent } from './update/enterprise-update.component';
import { EnterpriseDeleteDialogComponent } from './delete/enterprise-delete-dialog.component';
import { EnterpriseRoutingModule } from './route/enterprise-routing.module';

@NgModule({
  imports: [SharedModule, EnterpriseRoutingModule],
  declarations: [EnterpriseComponent, EnterpriseDetailComponent, EnterpriseUpdateComponent, EnterpriseDeleteDialogComponent],
  entryComponents: [EnterpriseDeleteDialogComponent],
})
export class EnterpriseModule {}
