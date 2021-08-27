jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IEnterprise, Enterprise } from '../enterprise.model';
import { EnterpriseService } from '../service/enterprise.service';

import { EnterpriseRoutingResolveService } from './enterprise-routing-resolve.service';

describe('Service Tests', () => {
  describe('Enterprise routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: EnterpriseRoutingResolveService;
    let service: EnterpriseService;
    let resultEnterprise: IEnterprise | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(EnterpriseRoutingResolveService);
      service = TestBed.inject(EnterpriseService);
      resultEnterprise = undefined;
    });

    describe('resolve', () => {
      it('should return IEnterprise returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultEnterprise = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultEnterprise).toEqual({ id: 123 });
      });

      it('should return new IEnterprise if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultEnterprise = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultEnterprise).toEqual(new Enterprise());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Enterprise })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultEnterprise = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultEnterprise).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
