export interface IAgency {
  id?: number;
  name?: string;
  address?: string;
  contact?: string | null;
  email?: string;
}

export class Agency implements IAgency {
  constructor(public id?: number, public name?: string, public address?: string, public contact?: string | null, public email?: string) {}
}

export function getAgencyIdentifier(agency: IAgency): number | undefined {
  return agency.id;
}
