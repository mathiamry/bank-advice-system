import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnterpriseDetailComponent } from './enterprise-detail.component';

describe('Component Tests', () => {
  describe('Enterprise Management Detail Component', () => {
    let comp: EnterpriseDetailComponent;
    let fixture: ComponentFixture<EnterpriseDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [EnterpriseDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ enterprise: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(EnterpriseDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(EnterpriseDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load enterprise on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.enterprise).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
