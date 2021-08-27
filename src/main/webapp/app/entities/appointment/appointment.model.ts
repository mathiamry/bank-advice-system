import * as dayjs from 'dayjs';
import { IManager } from 'app/entities/manager/manager.model';
import { IAdvisor } from 'app/entities/advisor/advisor.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IAppointment {
  id?: number;
  created?: dayjs.Dayjs;
  appointementDate?: dayjs.Dayjs;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  title?: string | null;
  description?: string | null;
  status?: Status | null;
  statusChangeDate?: dayjs.Dayjs;
  commentary?: string | null;
  manager?: IManager;
  advisor?: IAdvisor;
}

export class Appointment implements IAppointment {
  constructor(
    public id?: number,
    public created?: dayjs.Dayjs,
    public appointementDate?: dayjs.Dayjs,
    public startDate?: dayjs.Dayjs,
    public endDate?: dayjs.Dayjs,
    public title?: string | null,
    public description?: string | null,
    public status?: Status | null,
    public statusChangeDate?: dayjs.Dayjs,
    public commentary?: string | null,
    public manager?: IManager,
    public advisor?: IAdvisor
  ) {}
}

export function getAppointmentIdentifier(appointment: IAppointment): number | undefined {
  return appointment.id;
}
