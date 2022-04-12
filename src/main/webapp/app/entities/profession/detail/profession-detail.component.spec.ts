import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProfessionDetailComponent } from './profession-detail.component';

describe('Profession Management Detail Component', () => {
  let comp: ProfessionDetailComponent;
  let fixture: ComponentFixture<ProfessionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfessionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ profession: { name: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(ProfessionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProfessionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load profession on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.profession).toEqual(expect.objectContaining({ name: 'ABC' }));
    });
  });
});
