import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAgency, getAgencyIdentifier } from '../agency.model';

export type EntityResponseType = HttpResponse<IAgency>;
export type EntityArrayResponseType = HttpResponse<IAgency[]>;

@Injectable({ providedIn: 'root' })
export class AgencyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/agencies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(agency: IAgency): Observable<EntityResponseType> {
    return this.http.post<IAgency>(this.resourceUrl, agency, { observe: 'response' });
  }

  update(agency: IAgency): Observable<EntityResponseType> {
    return this.http.put<IAgency>(`${this.resourceUrl}/${getAgencyIdentifier(agency) as number}`, agency, { observe: 'response' });
  }

  partialUpdate(agency: IAgency): Observable<EntityResponseType> {
    return this.http.patch<IAgency>(`${this.resourceUrl}/${getAgencyIdentifier(agency) as number}`, agency, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAgency>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAgency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAgencyToCollectionIfMissing(agencyCollection: IAgency[], ...agenciesToCheck: (IAgency | null | undefined)[]): IAgency[] {
    const agencies: IAgency[] = agenciesToCheck.filter(isPresent);
    if (agencies.length > 0) {
      const agencyCollectionIdentifiers = agencyCollection.map(agencyItem => getAgencyIdentifier(agencyItem)!);
      const agenciesToAdd = agencies.filter(agencyItem => {
        const agencyIdentifier = getAgencyIdentifier(agencyItem);
        if (agencyIdentifier == null || agencyCollectionIdentifiers.includes(agencyIdentifier)) {
          return false;
        }
        agencyCollectionIdentifiers.push(agencyIdentifier);
        return true;
      });
      return [...agenciesToAdd, ...agencyCollection];
    }
    return agencyCollection;
  }
}
