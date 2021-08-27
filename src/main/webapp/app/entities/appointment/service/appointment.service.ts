import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppointment, getAppointmentIdentifier } from '../appointment.model';

export type EntityResponseType = HttpResponse<IAppointment>;
export type EntityArrayResponseType = HttpResponse<IAppointment[]>;

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/appointments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appointment: IAppointment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appointment);
    return this.http
      .post<IAppointment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(appointment: IAppointment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appointment);
    return this.http
      .put<IAppointment>(`${this.resourceUrl}/${getAppointmentIdentifier(appointment) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(appointment: IAppointment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appointment);
    return this.http
      .patch<IAppointment>(`${this.resourceUrl}/${getAppointmentIdentifier(appointment) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAppointment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAppointment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAppointmentToCollectionIfMissing(
    appointmentCollection: IAppointment[],
    ...appointmentsToCheck: (IAppointment | null | undefined)[]
  ): IAppointment[] {
    const appointments: IAppointment[] = appointmentsToCheck.filter(isPresent);
    if (appointments.length > 0) {
      const appointmentCollectionIdentifiers = appointmentCollection.map(appointmentItem => getAppointmentIdentifier(appointmentItem)!);
      const appointmentsToAdd = appointments.filter(appointmentItem => {
        const appointmentIdentifier = getAppointmentIdentifier(appointmentItem);
        if (appointmentIdentifier == null || appointmentCollectionIdentifiers.includes(appointmentIdentifier)) {
          return false;
        }
        appointmentCollectionIdentifiers.push(appointmentIdentifier);
        return true;
      });
      return [...appointmentsToAdd, ...appointmentCollection];
    }
    return appointmentCollection;
  }

  protected convertDateFromClient(appointment: IAppointment): IAppointment {
    return Object.assign({}, appointment, {
      created: appointment.created?.isValid() ? appointment.created.toJSON() : undefined,
      appointementDate: appointment.appointementDate?.isValid() ? appointment.appointementDate.format(DATE_FORMAT) : undefined,
      startDate: appointment.startDate?.isValid() ? appointment.startDate.toJSON() : undefined,
      endDate: appointment.endDate?.isValid() ? appointment.endDate.toJSON() : undefined,
      statusChangeDate: appointment.statusChangeDate?.isValid() ? appointment.statusChangeDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.created = res.body.created ? dayjs(res.body.created) : undefined;
      res.body.appointementDate = res.body.appointementDate ? dayjs(res.body.appointementDate) : undefined;
      res.body.startDate = res.body.startDate ? dayjs(res.body.startDate) : undefined;
      res.body.endDate = res.body.endDate ? dayjs(res.body.endDate) : undefined;
      res.body.statusChangeDate = res.body.statusChangeDate ? dayjs(res.body.statusChangeDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((appointment: IAppointment) => {
        appointment.created = appointment.created ? dayjs(appointment.created) : undefined;
        appointment.appointementDate = appointment.appointementDate ? dayjs(appointment.appointementDate) : undefined;
        appointment.startDate = appointment.startDate ? dayjs(appointment.startDate) : undefined;
        appointment.endDate = appointment.endDate ? dayjs(appointment.endDate) : undefined;
        appointment.statusChangeDate = appointment.statusChangeDate ? dayjs(appointment.statusChangeDate) : undefined;
      });
    }
    return res;
  }
}
