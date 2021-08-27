import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEnterprise, getEnterpriseIdentifier } from '../enterprise.model';

export type EntityResponseType = HttpResponse<IEnterprise>;
export type EntityArrayResponseType = HttpResponse<IEnterprise[]>;

@Injectable({ providedIn: 'root' })
export class EnterpriseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/enterprises');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(enterprise: IEnterprise): Observable<EntityResponseType> {
    return this.http.post<IEnterprise>(this.resourceUrl, enterprise, { observe: 'response' });
  }

  update(enterprise: IEnterprise): Observable<EntityResponseType> {
    return this.http.put<IEnterprise>(`${this.resourceUrl}/${getEnterpriseIdentifier(enterprise) as number}`, enterprise, {
      observe: 'response',
    });
  }

  partialUpdate(enterprise: IEnterprise): Observable<EntityResponseType> {
    return this.http.patch<IEnterprise>(`${this.resourceUrl}/${getEnterpriseIdentifier(enterprise) as number}`, enterprise, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnterprise>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnterprise[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEnterpriseToCollectionIfMissing(
    enterpriseCollection: IEnterprise[],
    ...enterprisesToCheck: (IEnterprise | null | undefined)[]
  ): IEnterprise[] {
    const enterprises: IEnterprise[] = enterprisesToCheck.filter(isPresent);
    if (enterprises.length > 0) {
      const enterpriseCollectionIdentifiers = enterpriseCollection.map(enterpriseItem => getEnterpriseIdentifier(enterpriseItem)!);
      const enterprisesToAdd = enterprises.filter(enterpriseItem => {
        const enterpriseIdentifier = getEnterpriseIdentifier(enterpriseItem);
        if (enterpriseIdentifier == null || enterpriseCollectionIdentifiers.includes(enterpriseIdentifier)) {
          return false;
        }
        enterpriseCollectionIdentifiers.push(enterpriseIdentifier);
        return true;
      });
      return [...enterprisesToAdd, ...enterpriseCollection];
    }
    return enterpriseCollection;
  }
}
