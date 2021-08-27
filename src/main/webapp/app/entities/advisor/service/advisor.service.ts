import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAdvisor, getAdvisorIdentifier } from '../advisor.model';

export type EntityResponseType = HttpResponse<IAdvisor>;
export type EntityArrayResponseType = HttpResponse<IAdvisor[]>;

@Injectable({ providedIn: 'root' })
export class AdvisorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/advisors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(advisor: IAdvisor): Observable<EntityResponseType> {
    return this.http.post<IAdvisor>(this.resourceUrl, advisor, { observe: 'response' });
  }

  update(advisor: IAdvisor): Observable<EntityResponseType> {
    return this.http.put<IAdvisor>(`${this.resourceUrl}/${getAdvisorIdentifier(advisor) as number}`, advisor, { observe: 'response' });
  }

  partialUpdate(advisor: IAdvisor): Observable<EntityResponseType> {
    return this.http.patch<IAdvisor>(`${this.resourceUrl}/${getAdvisorIdentifier(advisor) as number}`, advisor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAdvisor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAdvisor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAdvisorToCollectionIfMissing(advisorCollection: IAdvisor[], ...advisorsToCheck: (IAdvisor | null | undefined)[]): IAdvisor[] {
    const advisors: IAdvisor[] = advisorsToCheck.filter(isPresent);
    if (advisors.length > 0) {
      const advisorCollectionIdentifiers = advisorCollection.map(advisorItem => getAdvisorIdentifier(advisorItem)!);
      const advisorsToAdd = advisors.filter(advisorItem => {
        const advisorIdentifier = getAdvisorIdentifier(advisorItem);
        if (advisorIdentifier == null || advisorCollectionIdentifiers.includes(advisorIdentifier)) {
          return false;
        }
        advisorCollectionIdentifiers.push(advisorIdentifier);
        return true;
      });
      return [...advisorsToAdd, ...advisorCollection];
    }
    return advisorCollection;
  }
}
