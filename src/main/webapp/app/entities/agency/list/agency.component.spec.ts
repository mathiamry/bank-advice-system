import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AgencyService } from '../service/agency.service';

import { AgencyComponent } from './agency.component';

describe('Component Tests', () => {
  describe('Agency Management Component', () => {
    let comp: AgencyComponent;
    let fixture: ComponentFixture<AgencyComponent>;
    let service: AgencyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AgencyComponent],
      })
        .overrideTemplate(AgencyComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AgencyComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AgencyService);

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
      expect(comp.agencies?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
