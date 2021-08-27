import { IUser } from 'app/entities/user/user.model';
import { IAppointment } from 'app/entities/appointment/appointment.model';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface IManager {
  id?: number;
  gender?: Gender | null;
  telephone?: string;
  user?: IUser;
  appointments?: IAppointment[] | null;
}

export class Manager implements IManager {
  constructor(
    public id?: number,
    public gender?: Gender | null,
    public telephone?: string,
    public user?: IUser,
    public appointments?: IAppointment[] | null
  ) {}
}

export function getManagerIdentifier(manager: IManager): number | undefined {
  return manager.id;
}
