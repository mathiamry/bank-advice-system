import { IManager } from 'app/entities/manager/manager.model';

export interface IEnterprise {
  id?: number;
  name?: string;
  address?: string;
  ninea?: string | null;
  email?: string;
  manager?: IManager | null;
}

export class Enterprise implements IEnterprise {
  constructor(
    public id?: number,
    public name?: string,
    public address?: string,
    public ninea?: string | null,
    public email?: string,
    public manager?: IManager | null
  ) {}
}

export function getEnterpriseIdentifier(enterprise: IEnterprise): number | undefined {
  return enterprise.id;
}
