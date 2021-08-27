import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAdvisor, Advisor } from '../advisor.model';
import { AdvisorService } from '../service/advisor.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IAgency } from 'app/entities/agency/agency.model';
import { AgencyService } from 'app/entities/agency/service/agency.service';

@Component({
  selector: 'jhi-advisor-update',
  templateUrl: './advisor-update.component.html',
})
export class AdvisorUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  agenciesSharedCollection: IAgency[] = [];

  editForm = this.fb.group({
    id: [],
    gender: [],
    telephone: [null, [Validators.required, Validators.maxLength(20)]],
    user: [null, Validators.required],
    agency: [],
  });

  constructor(
    protected advisorService: AdvisorService,
    protected userService: UserService,
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ advisor }) => {
      this.updateForm(advisor);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const advisor = this.createFromForm();
    if (advisor.id !== undefined) {
      this.subscribeToSaveResponse(this.advisorService.update(advisor));
    } else {
      this.subscribeToSaveResponse(this.advisorService.create(advisor));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackAgencyById(index: number, item: IAgency): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAdvisor>>): void {
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

  protected updateForm(advisor: IAdvisor): void {
    this.editForm.patchValue({
      id: advisor.id,
      gender: advisor.gender,
      telephone: advisor.telephone,
      user: advisor.user,
      agency: advisor.agency,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, advisor.user);
    this.agenciesSharedCollection = this.agencyService.addAgencyToCollectionIfMissing(this.agenciesSharedCollection, advisor.agency);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.agencyService
      .query()
      .pipe(map((res: HttpResponse<IAgency[]>) => res.body ?? []))
      .pipe(map((agencies: IAgency[]) => this.agencyService.addAgencyToCollectionIfMissing(agencies, this.editForm.get('agency')!.value)))
      .subscribe((agencies: IAgency[]) => (this.agenciesSharedCollection = agencies));
  }

  protected createFromForm(): IAdvisor {
    return {
      ...new Advisor(),
      id: this.editForm.get(['id'])!.value,
      gender: this.editForm.get(['gender'])!.value,
      telephone: this.editForm.get(['telephone'])!.value,
      user: this.editForm.get(['user'])!.value,
      agency: this.editForm.get(['agency'])!.value,
    };
  }
}
