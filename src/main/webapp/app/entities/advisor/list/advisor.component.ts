import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdvisor } from '../advisor.model';
import { AdvisorService } from '../service/advisor.service';
import { AdvisorDeleteDialogComponent } from '../delete/advisor-delete-dialog.component';

@Component({
  selector: 'jhi-advisor',
  templateUrl: './advisor.component.html',
})
export class AdvisorComponent implements OnInit {
  advisors?: IAdvisor[];
  isLoading = false;

  constructor(protected advisorService: AdvisorService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.advisorService.query().subscribe(
      (res: HttpResponse<IAdvisor[]>) => {
        this.isLoading = false;
        this.advisors = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IAdvisor): number {
    return item.id!;
  }

  delete(advisor: IAdvisor): void {
    const modalRef = this.modalService.open(AdvisorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.advisor = advisor;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
