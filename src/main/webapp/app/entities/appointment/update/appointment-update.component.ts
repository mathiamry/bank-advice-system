import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IAppointment, Appointment } from '../appointment.model';
import { AppointmentService } from '../service/appointment.service';
import { IManager } from 'app/entities/manager/manager.model';
import { ManagerService } from 'app/entities/manager/service/manager.service';
import { IAdvisor } from 'app/entities/advisor/advisor.model';
import { AdvisorService } from 'app/entities/advisor/service/advisor.service';

@Component({
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html',
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving = false;

  managersSharedCollection: IManager[] = [];
  advisorsSharedCollection: IAdvisor[] = [];

  editForm = this.fb.group({
    id: [],
    created: [null, [Validators.required]],
    appointementDate: [null, [Validators.required]],
    startDate: [null, [Validators.required]],
    endDate: [null, [Validators.required]],
    title: [null, [Validators.maxLength(15)]],
    description: [],
    status: [],
    statusChangeDate: [null, [Validators.required]],
    commentary: [],
    manager: [null, Validators.required],
    advisor: [null, Validators.required],
  });

  constructor(
    protected appointmentService: AppointmentService,
    protected managerService: ManagerService,
    protected advisorService: AdvisorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointment }) => {
      if (appointment.id === undefined) {
        const today = dayjs().startOf('day');
        appointment.created = today;
        appointment.startDate = today;
        appointment.endDate = today;
        appointment.statusChangeDate = today;
      }

      this.updateForm(appointment);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appointment = this.createFromForm();
    if (appointment.id !== undefined) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      this.subscribeToSaveResponse(this.appointmentService.create(appointment));
    }
  }

  trackManagerById(index: number, item: IManager): number {
    return item.id!;
  }

  trackAdvisorById(index: number, item: IAdvisor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>): void {
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

  protected updateForm(appointment: IAppointment): void {
    this.editForm.patchValue({
      id: appointment.id,
      created: appointment.created ? appointment.created.format(DATE_TIME_FORMAT) : null,
      appointementDate: appointment.appointementDate,
      startDate: appointment.startDate ? appointment.startDate.format(DATE_TIME_FORMAT) : null,
      endDate: appointment.endDate ? appointment.endDate.format(DATE_TIME_FORMAT) : null,
      title: appointment.title,
      description: appointment.description,
      status: appointment.status,
      statusChangeDate: appointment.statusChangeDate ? appointment.statusChangeDate.format(DATE_TIME_FORMAT) : null,
      commentary: appointment.commentary,
      manager: appointment.manager,
      advisor: appointment.advisor,
    });

    this.managersSharedCollection = this.managerService.addManagerToCollectionIfMissing(this.managersSharedCollection, appointment.manager);
    this.advisorsSharedCollection = this.advisorService.addAdvisorToCollectionIfMissing(this.advisorsSharedCollection, appointment.advisor);
  }

  protected loadRelationshipsOptions(): void {
    this.managerService
      .query()
      .pipe(map((res: HttpResponse<IManager[]>) => res.body ?? []))
      .pipe(
        map((managers: IManager[]) => this.managerService.addManagerToCollectionIfMissing(managers, this.editForm.get('manager')!.value))
      )
      .subscribe((managers: IManager[]) => (this.managersSharedCollection = managers));

    this.advisorService
      .query()
      .pipe(map((res: HttpResponse<IAdvisor[]>) => res.body ?? []))
      .pipe(
        map((advisors: IAdvisor[]) => this.advisorService.addAdvisorToCollectionIfMissing(advisors, this.editForm.get('advisor')!.value))
      )
      .subscribe((advisors: IAdvisor[]) => (this.advisorsSharedCollection = advisors));
  }

  protected createFromForm(): IAppointment {
    return {
      ...new Appointment(),
      id: this.editForm.get(['id'])!.value,
      created: this.editForm.get(['created'])!.value ? dayjs(this.editForm.get(['created'])!.value, DATE_TIME_FORMAT) : undefined,
      appointementDate: this.editForm.get(['appointementDate'])!.value,
      startDate: this.editForm.get(['startDate'])!.value ? dayjs(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? dayjs(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
      statusChangeDate: this.editForm.get(['statusChangeDate'])!.value
        ? dayjs(this.editForm.get(['statusChangeDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      commentary: this.editForm.get(['commentary'])!.value,
      manager: this.editForm.get(['manager'])!.value,
      advisor: this.editForm.get(['advisor'])!.value,
    };
  }
}
