import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AdvisorComponent } from './list/advisor.component';
import { AdvisorDetailComponent } from './detail/advisor-detail.component';
import { AdvisorUpdateComponent } from './update/advisor-update.component';
import { AdvisorDeleteDialogComponent } from './delete/advisor-delete-dialog.component';
import { AdvisorRoutingModule } from './route/advisor-routing.module';

@NgModule({
  imports: [SharedModule, AdvisorRoutingModule],
  declarations: [AdvisorComponent, AdvisorDetailComponent, AdvisorUpdateComponent, AdvisorDeleteDialogComponent],
  entryComponents: [AdvisorDeleteDialogComponent],
})
export class AdvisorModule {}
