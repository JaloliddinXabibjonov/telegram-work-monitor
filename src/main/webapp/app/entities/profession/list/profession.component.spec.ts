jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { ProfessionService } from '../service/profession.service';

import { ProfessionComponent } from './profession.component';

describe('Profession Management Component', () => {
  let comp: ProfessionComponent;
  let fixture: ComponentFixture<ProfessionComponent>;
  let service: ProfessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProfessionComponent],
      providers: [
        Router,
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'name,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'name,desc',
              })
            ),
          },
        },
      ],
    })
      .overrideTemplate(ProfessionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfessionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProfessionService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ name: 'ABC' }],
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
    expect(comp.professions?.[0]).toEqual(expect.objectContaining({ name: 'ABC' }));
  });

  it('should load a page', () => {
    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.professions?.[0]).toEqual(expect.objectContaining({ name: 'ABC' }));
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['name,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // INIT
    comp.ngOnInit();

    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,desc', 'name'] }));
  });
});
