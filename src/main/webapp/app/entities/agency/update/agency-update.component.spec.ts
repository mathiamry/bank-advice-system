jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AgencyService } from '../service/agency.service';
import { IAgency, Agency } from '../agency.model';

import { AgencyUpdateComponent } from './agency-update.component';

describe('Component Tests', () => {
  describe('Agency Management Update Component', () => {
    let comp: AgencyUpdateComponent;
    let fixture: ComponentFixture<AgencyUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let agencyService: AgencyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AgencyUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AgencyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AgencyUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      agencyService = TestBed.inject(AgencyService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const agency: IAgency = { id: 456 };

        activatedRoute.data = of({ agency });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(agency));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Agency>>();
        const agency = { id: 123 };
        jest.spyOn(agencyService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ agency });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: agency }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(agencyService.update).toHaveBeenCalledWith(agency);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Agency>>();
        const agency = new Agency();
        jest.spyOn(agencyService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ agency });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: agency }));
        saveSubject.complete();

        // THEN
        expect(agencyService.create).toHaveBeenCalledWith(agency);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Agency>>();
        const agency = { id: 123 };
        jest.spyOn(agencyService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ agency });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(agencyService.update).toHaveBeenCalledWith(agency);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
