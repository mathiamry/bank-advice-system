import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AgencyComponent } from './list/agency.component';
import { AgencyDetailComponent } from './detail/agency-detail.component';
import { AgencyUpdateComponent } from './update/agency-update.component';
import { AgencyDeleteDialogComponent } from './delete/agency-delete-dialog.component';
import { AgencyRoutingModule } from './route/agency-routing.module';

@NgModule({
  imports: [SharedModule, AgencyRoutingModule],
  declarations: [AgencyComponent, AgencyDetailComponent, AgencyUpdateComponent, AgencyDeleteDialogComponent],
  entryComponents: [AgencyDeleteDialogComponent],
})
export class AgencyModule {}
