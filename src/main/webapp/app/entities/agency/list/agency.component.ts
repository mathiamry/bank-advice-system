import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAgency } from '../agency.model';
import { AgencyService } from '../service/agency.service';
import { AgencyDeleteDialogComponent } from '../delete/agency-delete-dialog.component';

@Component({
  selector: 'jhi-agency',
  templateUrl: './agency.component.html',
})
export class AgencyComponent implements OnInit {
  agencies?: IAgency[];
  isLoading = false;

  constructor(protected agencyService: AgencyService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.agencyService.query().subscribe(
      (res: HttpResponse<IAgency[]>) => {
        this.isLoading = false;
        this.agencies = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IAgency): number {
    return item.id!;
  }

  delete(agency: IAgency): void {
    const modalRef = this.modalService.open(AgencyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.agency = agency;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
