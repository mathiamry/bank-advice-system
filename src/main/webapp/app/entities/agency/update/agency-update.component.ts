import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAgency, Agency } from '../agency.model';
import { AgencyService } from '../service/agency.service';

@Component({
  selector: 'jhi-agency-update',
  templateUrl: './agency-update.component.html',
})
export class AgencyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    address: [null, [Validators.required]],
    contact: [],
    email: [null, [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$')]],
  });

  constructor(protected agencyService: AgencyService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agency }) => {
      this.updateForm(agency);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const agency = this.createFromForm();
    if (agency.id !== undefined) {
      this.subscribeToSaveResponse(this.agencyService.update(agency));
    } else {
      this.subscribeToSaveResponse(this.agencyService.create(agency));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAgency>>): void {
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

  protected updateForm(agency: IAgency): void {
    this.editForm.patchValue({
      id: agency.id,
      name: agency.name,
      address: agency.address,
      contact: agency.contact,
      email: agency.email,
    });
  }

  protected createFromForm(): IAgency {
    return {
      ...new Agency(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      address: this.editForm.get(['address'])!.value,
      contact: this.editForm.get(['contact'])!.value,
      email: this.editForm.get(['email'])!.value,
    };
  }
}
