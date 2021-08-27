import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAgency } from '../agency.model';
import { AgencyService } from '../service/agency.service';

@Component({
  templateUrl: './agency-delete-dialog.component.html',
})
export class AgencyDeleteDialogComponent {
  agency?: IAgency;

  constructor(protected agencyService: AgencyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.agencyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
