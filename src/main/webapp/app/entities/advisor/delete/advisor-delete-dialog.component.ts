import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdvisor } from '../advisor.model';
import { AdvisorService } from '../service/advisor.service';

@Component({
  templateUrl: './advisor-delete-dialog.component.html',
})
export class AdvisorDeleteDialogComponent {
  advisor?: IAdvisor;

  constructor(protected advisorService: AdvisorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.advisorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
