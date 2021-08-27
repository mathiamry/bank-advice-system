import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAgency, Agency } from '../agency.model';

import { AgencyService } from './agency.service';

describe('Service Tests', () => {
  describe('Agency Service', () => {
    let service: AgencyService;
    let httpMock: HttpTestingController;
    let elemDefault: IAgency;
    let expectedResult: IAgency | IAgency[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(AgencyService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        address: 'AAAAAAA',
        contact: 'AAAAAAA',
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

      it('should create a Agency', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Agency()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Agency', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            contact: 'BBBBBB',
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

      it('should partial update a Agency', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
            email: 'BBBBBB',
          },
          new Agency()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Agency', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            contact: 'BBBBBB',
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

      it('should delete a Agency', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addAgencyToCollectionIfMissing', () => {
        it('should add a Agency to an empty array', () => {
          const agency: IAgency = { id: 123 };
          expectedResult = service.addAgencyToCollectionIfMissing([], agency);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(agency);
        });

        it('should not add a Agency to an array that contains it', () => {
          const agency: IAgency = { id: 123 };
          const agencyCollection: IAgency[] = [
            {
              ...agency,
            },
            { id: 456 },
          ];
          expectedResult = service.addAgencyToCollectionIfMissing(agencyCollection, agency);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Agency to an array that doesn't contain it", () => {
          const agency: IAgency = { id: 123 };
          const agencyCollection: IAgency[] = [{ id: 456 }];
          expectedResult = service.addAgencyToCollectionIfMissing(agencyCollection, agency);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(agency);
        });

        it('should add only unique Agency to an array', () => {
          const agencyArray: IAgency[] = [{ id: 123 }, { id: 456 }, { id: 92019 }];
          const agencyCollection: IAgency[] = [{ id: 123 }];
          expectedResult = service.addAgencyToCollectionIfMissing(agencyCollection, ...agencyArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const agency: IAgency = { id: 123 };
          const agency2: IAgency = { id: 456 };
          expectedResult = service.addAgencyToCollectionIfMissing([], agency, agency2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(agency);
          expect(expectedResult).toContain(agency2);
        });

        it('should accept null and undefined values', () => {
          const agency: IAgency = { id: 123 };
          expectedResult = service.addAgencyToCollectionIfMissing([], null, agency, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(agency);
        });

        it('should return initial array if no Agency is added', () => {
          const agencyCollection: IAgency[] = [{ id: 123 }];
          expectedResult = service.addAgencyToCollectionIfMissing(agencyCollection, undefined, null);
          expect(expectedResult).toEqual(agencyCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
