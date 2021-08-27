import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AdvisorService } from '../service/advisor.service';

import { AdvisorComponent } from './advisor.component';

describe('Component Tests', () => {
  describe('Advisor Management Component', () => {
    let comp: AdvisorComponent;
    let fixture: ComponentFixture<AdvisorComponent>;
    let service: AdvisorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AdvisorComponent],
      })
        .overrideTemplate(AdvisorComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AdvisorComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AdvisorService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.advisors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
