import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { EnterpriseService } from '../service/enterprise.service';

import { EnterpriseComponent } from './enterprise.component';

describe('Component Tests', () => {
  describe('Enterprise Management Component', () => {
    let comp: EnterpriseComponent;
    let fixture: ComponentFixture<EnterpriseComponent>;
    let service: EnterpriseService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EnterpriseComponent],
      })
        .overrideTemplate(EnterpriseComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EnterpriseComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(EnterpriseService);

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
      expect(comp.enterprises?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
