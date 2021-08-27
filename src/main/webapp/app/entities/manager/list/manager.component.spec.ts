import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ManagerService } from '../service/manager.service';

import { ManagerComponent } from './manager.component';

describe('Component Tests', () => {
  describe('Manager Management Component', () => {
    let comp: ManagerComponent;
    let fixture: ComponentFixture<ManagerComponent>;
    let service: ManagerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ManagerComponent],
      })
        .overrideTemplate(ManagerComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ManagerComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ManagerService);

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
      expect(comp.managers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
