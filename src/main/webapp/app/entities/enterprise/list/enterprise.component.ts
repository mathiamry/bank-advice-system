import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEnterprise } from '../enterprise.model';
import { EnterpriseService } from '../service/enterprise.service';
import { EnterpriseDeleteDialogComponent } from '../delete/enterprise-delete-dialog.component';

@Component({
  selector: 'jhi-enterprise',
  templateUrl: './enterprise.component.html',
})
export class EnterpriseComponent implements OnInit {
  enterprises?: IEnterprise[];
  isLoading = false;

  constructor(protected enterpriseService: EnterpriseService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.enterpriseService.query().subscribe(
      (res: HttpResponse<IEnterprise[]>) => {
        this.isLoading = false;
        this.enterprises = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IEnterprise): number {
    return item.id!;
  }

  delete(enterprise: IEnterprise): void {
    const modalRef = this.modalService.open(EnterpriseDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.enterprise = enterprise;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
