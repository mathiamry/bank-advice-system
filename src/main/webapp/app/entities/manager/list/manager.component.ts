import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IManager } from '../manager.model';
import { ManagerService } from '../service/manager.service';
import { ManagerDeleteDialogComponent } from '../delete/manager-delete-dialog.component';

@Component({
  selector: 'jhi-manager',
  templateUrl: './manager.component.html',
})
export class ManagerComponent implements OnInit {
  managers?: IManager[];
  isLoading = false;

  constructor(protected managerService: ManagerService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.managerService.query().subscribe(
      (res: HttpResponse<IManager[]>) => {
        this.isLoading = false;
        this.managers = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IManager): number {
    return item.id!;
  }

  delete(manager: IManager): void {
    const modalRef = this.modalService.open(ManagerDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.manager = manager;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
