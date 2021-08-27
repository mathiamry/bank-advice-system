jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAdvisor, Advisor } from '../advisor.model';
import { AdvisorService } from '../service/advisor.service';

import { AdvisorRoutingResolveService } from './advisor-routing-resolve.service';

describe('Service Tests', () => {
  describe('Advisor routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: AdvisorRoutingResolveService;
    let service: AdvisorService;
    let resultAdvisor: IAdvisor | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(AdvisorRoutingResolveService);
      service = TestBed.inject(AdvisorService);
      resultAdvisor = undefined;
    });

    describe('resolve', () => {
      it('should return IAdvisor returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdvisor = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAdvisor).toEqual({ id: 123 });
      });

      it('should return new IAdvisor if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdvisor = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAdvisor).toEqual(new Advisor());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Advisor })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdvisor = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAdvisor).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
