import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AgencyDetailComponent } from './agency-detail.component';

describe('Component Tests', () => {
  describe('Agency Management Detail Component', () => {
    let comp: AgencyDetailComponent;
    let fixture: ComponentFixture<AgencyDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [AgencyDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ agency: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(AgencyDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AgencyDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load agency on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.agency).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
