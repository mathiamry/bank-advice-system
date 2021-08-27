import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AdvisorDetailComponent } from './advisor-detail.component';

describe('Component Tests', () => {
  describe('Advisor Management Detail Component', () => {
    let comp: AdvisorDetailComponent;
    let fixture: ComponentFixture<AdvisorDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [AdvisorDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ advisor: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(AdvisorDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AdvisorDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load advisor on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.advisor).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
