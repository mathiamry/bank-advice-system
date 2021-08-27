import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Gender } from 'app/entities/enumerations/gender.model';
import { IAdvisor, Advisor } from '../advisor.model';

import { AdvisorService } from './advisor.service';

describe('Service Tests', () => {
  describe('Advisor Service', () => {
    let service: AdvisorService;
    let httpMock: HttpTestingController;
    let elemDefault: IAdvisor;
    let expectedResult: IAdvisor | IAdvisor[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(AdvisorService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        gender: Gender.MALE,
        telephone: 'AAAAAAA',
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

      it('should create a Advisor', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Advisor()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Advisor', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            gender: 'BBBBBB',
            telephone: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Advisor', () => {
        const patchObject = Object.assign(
          {
            gender: 'BBBBBB',
            telephone: 'BBBBBB',
          },
          new Advisor()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Advisor', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            gender: 'BBBBBB',
            telephone: 'BBBBBB',
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

      it('should delete a Advisor', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addAdvisorToCollectionIfMissing', () => {
        it('should add a Advisor to an empty array', () => {
          const advisor: IAdvisor = { id: 123 };
          expectedResult = service.addAdvisorToCollectionIfMissing([], advisor);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(advisor);
        });

        it('should not add a Advisor to an array that contains it', () => {
          const advisor: IAdvisor = { id: 123 };
          const advisorCollection: IAdvisor[] = [
            {
              ...advisor,
            },
            { id: 456 },
          ];
          expectedResult = service.addAdvisorToCollectionIfMissing(advisorCollection, advisor);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Advisor to an array that doesn't contain it", () => {
          const advisor: IAdvisor = { id: 123 };
          const advisorCollection: IAdvisor[] = [{ id: 456 }];
          expectedResult = service.addAdvisorToCollectionIfMissing(advisorCollection, advisor);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(advisor);
        });

        it('should add only unique Advisor to an array', () => {
          const advisorArray: IAdvisor[] = [{ id: 123 }, { id: 456 }, { id: 75987 }];
          const advisorCollection: IAdvisor[] = [{ id: 123 }];
          expectedResult = service.addAdvisorToCollectionIfMissing(advisorCollection, ...advisorArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const advisor: IAdvisor = { id: 123 };
          const advisor2: IAdvisor = { id: 456 };
          expectedResult = service.addAdvisorToCollectionIfMissing([], advisor, advisor2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(advisor);
          expect(expectedResult).toContain(advisor2);
        });

        it('should accept null and undefined values', () => {
          const advisor: IAdvisor = { id: 123 };
          expectedResult = service.addAdvisorToCollectionIfMissing([], null, advisor, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(advisor);
        });

        it('should return initial array if no Advisor is added', () => {
          const advisorCollection: IAdvisor[] = [{ id: 123 }];
          expectedResult = service.addAdvisorToCollectionIfMissing(advisorCollection, undefined, null);
          expect(expectedResult).toEqual(advisorCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
