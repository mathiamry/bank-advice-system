jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAgency, Agency } from '../agency.model';
import { AgencyService } from '../service/agency.service';

import { AgencyRoutingResolveService } from './agency-routing-resolve.service';

describe('Service Tests', () => {
  describe('Agency routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: AgencyRoutingResolveService;
    let service: AgencyService;
    let resultAgency: IAgency | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(AgencyRoutingResolveService);
      service = TestBed.inject(AgencyService);
      resultAgency = undefined;
    });

    describe('resolve', () => {
      it('should return IAgency returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAgency = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAgency).toEqual({ id: 123 });
      });

      it('should return new IAgency if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAgency = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAgency).toEqual(new Agency());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Agency })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAgency = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAgency).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
