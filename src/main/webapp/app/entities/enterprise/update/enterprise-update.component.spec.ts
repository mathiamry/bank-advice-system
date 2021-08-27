jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { EnterpriseService } from '../service/enterprise.service';
import { IEnterprise, Enterprise } from '../enterprise.model';
import { IManager } from 'app/entities/manager/manager.model';
import { ManagerService } from 'app/entities/manager/service/manager.service';

import { EnterpriseUpdateComponent } from './enterprise-update.component';

describe('Component Tests', () => {
  describe('Enterprise Management Update Component', () => {
    let comp: EnterpriseUpdateComponent;
    let fixture: ComponentFixture<EnterpriseUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let enterpriseService: EnterpriseService;
    let managerService: ManagerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EnterpriseUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(EnterpriseUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EnterpriseUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      enterpriseService = TestBed.inject(EnterpriseService);
      managerService = TestBed.inject(ManagerService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call manager query and add missing value', () => {
        const enterprise: IEnterprise = { id: 456 };
        const manager: IManager = { id: 81028 };
        enterprise.manager = manager;

        const managerCollection: IManager[] = [{ id: 73245 }];
        jest.spyOn(managerService, 'query').mockReturnValue(of(new HttpResponse({ body: managerCollection })));
        const expectedCollection: IManager[] = [manager, ...managerCollection];
        jest.spyOn(managerService, 'addManagerToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ enterprise });
        comp.ngOnInit();

        expect(managerService.query).toHaveBeenCalled();
        expect(managerService.addManagerToCollectionIfMissing).toHaveBeenCalledWith(managerCollection, manager);
        expect(comp.managersCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const enterprise: IEnterprise = { id: 456 };
        const manager: IManager = { id: 25543 };
        enterprise.manager = manager;

        activatedRoute.data = of({ enterprise });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(enterprise));
        expect(comp.managersCollection).toContain(manager);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Enterprise>>();
        const enterprise = { id: 123 };
        jest.spyOn(enterpriseService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ enterprise });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: enterprise }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(enterpriseService.update).toHaveBeenCalledWith(enterprise);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Enterprise>>();
        const enterprise = new Enterprise();
        jest.spyOn(enterpriseService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ enterprise });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: enterprise }));
        saveSubject.complete();

        // THEN
        expect(enterpriseService.create).toHaveBeenCalledWith(enterprise);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Enterprise>>();
        const enterprise = { id: 123 };
        jest.spyOn(enterpriseService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ enterprise });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(enterpriseService.update).toHaveBeenCalledWith(enterprise);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackManagerById', () => {
        it('Should return tracked Manager primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackManagerById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
