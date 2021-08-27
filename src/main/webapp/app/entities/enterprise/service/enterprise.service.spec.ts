import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnterprise, Enterprise } from '../enterprise.model';

import { EnterpriseService } from './enterprise.service';

describe('Service Tests', () => {
  describe('Enterprise Service', () => {
    let service: EnterpriseService;
    let httpMock: HttpTestingController;
    let elemDefault: IEnterprise;
    let expectedResult: IEnterprise | IEnterprise[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(EnterpriseService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        address: 'AAAAAAA',
        ninea: 'AAAAAAA',
        email: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Enterprise', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Enterprise()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Enterprise', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            ninea: 'BBBBBB',
            email: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Enterprise', () => {
        const patchObject = Object.assign(
          {
            email: 'BBBBBB',
          },
          new Enterprise()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Enterprise', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            ninea: 'BBBBBB',
            email: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Enterprise', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addEnterpriseToCollectionIfMissing', () => {
        it('should add a Enterprise to an empty array', () => {
          const enterprise: IEnterprise = { id: 123 };
          expectedResult = service.addEnterpriseToCollectionIfMissing([], enterprise);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(enterprise);
        });

        it('should not add a Enterprise to an array that contains it', () => {
          const enterprise: IEnterprise = { id: 123 };
          const enterpriseCollection: IEnterprise[] = [
            {
              ...enterprise,
            },
            { id: 456 },
          ];
          expectedResult = service.addEnterpriseToCollectionIfMissing(enterpriseCollection, enterprise);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Enterprise to an array that doesn't contain it", () => {
          const enterprise: IEnterprise = { id: 123 };
          const enterpriseCollection: IEnterprise[] = [{ id: 456 }];
          expectedResult = service.addEnterpriseToCollectionIfMissing(enterpriseCollection, enterprise);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(enterprise);
        });

        it('should add only unique Enterprise to an array', () => {
          const enterpriseArray: IEnterprise[] = [{ id: 123 }, { id: 456 }, { id: 14470 }];
          const enterpriseCollection: IEnterprise[] = [{ id: 123 }];
          expectedResult = service.addEnterpriseToCollectionIfMissing(enterpriseCollection, ...enterpriseArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const enterprise: IEnterprise = { id: 123 };
          const enterprise2: IEnterprise = { id: 456 };
          expectedResult = service.addEnterpriseToCollectionIfMissing([], enterprise, enterprise2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(enterprise);
          expect(expectedResult).toContain(enterprise2);
        });

        it('should accept null and undefined values', () => {
          const enterprise: IEnterprise = { id: 123 };
          expectedResult = service.addEnterpriseToCollectionIfMissing([], null, enterprise, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(enterprise);
        });

        it('should return initial array if no Enterprise is added', () => {
          const enterpriseCollection: IEnterprise[] = [{ id: 123 }];
          expectedResult = service.addEnterpriseToCollectionIfMissing(enterpriseCollection, undefined, null);
          expect(expectedResult).toEqual(enterpriseCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
