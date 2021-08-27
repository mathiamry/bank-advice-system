import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEnterprise, Enterprise } from '../enterprise.model';
import { EnterpriseService } from '../service/enterprise.service';
import { IManager } from 'app/entities/manager/manager.model';
import { ManagerService } from 'app/entities/manager/service/manager.service';

@Component({
  selector: 'jhi-enterprise-update',
  templateUrl: './enterprise-update.component.html',
})
export class EnterpriseUpdateComponent implements OnInit {
  isSaving = false;

  managersCollection: IManager[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    address: [null, [Validators.required]],
    ninea: [],
    email: [null, [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')]],
    manager: [],
  });

  constructor(
    protected enterpriseService: EnterpriseService,
    protected managerService: ManagerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enterprise }) => {
      this.updateForm(enterprise);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const enterprise = this.createFromForm();
    if (enterprise.id !== undefined) {
      this.subscribeToSaveResponse(this.enterpriseService.update(enterprise));
    } else {
      this.subscribeToSaveResponse(this.enterpriseService.create(enterprise));
    }
  }

  trackManagerById(index: number, item: IManager): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnterprise>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(enterprise: IEnterprise): void {
    this.editForm.patchValue({
      id: enterprise.id,
      name: enterprise.name,
      address: enterprise.address,
      ninea: enterprise.ninea,
      email: enterprise.email,
      manager: enterprise.manager,
    });

    this.managersCollection = this.managerService.addManagerToCollectionIfMissing(this.managersCollection, enterprise.manager);
  }

  protected loadRelationshipsOptions(): void {
    this.managerService
      .query({ filter: 'enterprise-is-null' })
      .pipe(map((res: HttpResponse<IManager[]>) => res.body ?? []))
      .pipe(
        map((managers: IManager[]) => this.managerService.addManagerToCollectionIfMissing(managers, this.editForm.get('manager')!.value))
      )
      .subscribe((managers: IManager[]) => (this.managersCollection = managers));
  }

  protected createFromForm(): IEnterprise {
    return {
      ...new Enterprise(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      address: this.editForm.get(['address'])!.value,
      ninea: this.editForm.get(['ninea'])!.value,
      email: this.editForm.get(['email'])!.value,
      manager: this.editForm.get(['manager'])!.value,
    };
  }
}
