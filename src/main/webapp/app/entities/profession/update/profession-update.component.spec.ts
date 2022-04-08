jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ProfessionService } from '../service/profession.service';
import { IProfession, Profession } from '../profession.model';

import { ProfessionUpdateComponent } from './profession-update.component';

describe('Profession Management Update Component', () => {
  let comp: ProfessionUpdateComponent;
  let fixture: ComponentFixture<ProfessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let professionService: ProfessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProfessionUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ProfessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    professionService = TestBed.inject(ProfessionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const profession: IProfession = { id: 456 };

      activatedRoute.data = of({ profession });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(profession));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profession>>();
      const profession = { id: 123 };
      jest.spyOn(professionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profession }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(professionService.update).toHaveBeenCalledWith(profession);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profession>>();
      const profession = new Profession();
      jest.spyOn(professionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profession }));
      saveSubject.complete();

      // THEN
      expect(professionService.create).toHaveBeenCalledWith(profession);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profession>>();
      const profession = { id: 123 };
      jest.spyOn(professionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(professionService.update).toHaveBeenCalledWith(profession);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
