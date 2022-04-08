jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TaskService } from '../service/task.service';
import { ITask, Task } from '../task.model';
import { IProfession } from 'app/entities/profession/profession.model';
import { ProfessionService } from 'app/entities/profession/service/profession.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';

import { TaskUpdateComponent } from './task-update.component';

describe('Task Management Update Component', () => {
  let comp: TaskUpdateComponent;
  let fixture: ComponentFixture<TaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskService: TaskService;
  let professionService: ProfessionService;
  let jobService: JobService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TaskUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskService = TestBed.inject(TaskService);
    professionService = TestBed.inject(ProfessionService);
    jobService = TestBed.inject(JobService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profession query and add missing value', () => {
      const task: ITask = { id: 456 };
      const professions: IProfession[] = [{ id: 81073 }];
      task.professions = professions;

      const professionCollection: IProfession[] = [{ id: 84167 }];
      jest.spyOn(professionService, 'query').mockReturnValue(of(new HttpResponse({ body: professionCollection })));
      const additionalProfessions = [...professions];
      const expectedCollection: IProfession[] = [...additionalProfessions, ...professionCollection];
      jest.spyOn(professionService, 'addProfessionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(professionService.query).toHaveBeenCalled();
      expect(professionService.addProfessionToCollectionIfMissing).toHaveBeenCalledWith(professionCollection, ...additionalProfessions);
      expect(comp.professionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Job query and add missing value', () => {
      const task: ITask = { id: 456 };
      const job: IJob = { id: 75602 };
      task.job = job;

      const jobCollection: IJob[] = [{ id: 88403 }];
      jest.spyOn(jobService, 'query').mockReturnValue(of(new HttpResponse({ body: jobCollection })));
      const additionalJobs = [job];
      const expectedCollection: IJob[] = [...additionalJobs, ...jobCollection];
      jest.spyOn(jobService, 'addJobToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(jobService.query).toHaveBeenCalled();
      expect(jobService.addJobToCollectionIfMissing).toHaveBeenCalledWith(jobCollection, ...additionalJobs);
      expect(comp.jobsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const task: ITask = { id: 456 };
      const professions: IProfession = { id: 11827 };
      task.professions = [professions];
      const job: IJob = { id: 64664 };
      task.job = job;

      activatedRoute.data = of({ task });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(task));
      expect(comp.professionsSharedCollection).toContain(professions);
      expect(comp.jobsSharedCollection).toContain(job);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 123 };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskService.update).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = new Task();
      jest.spyOn(taskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: task }));
      saveSubject.complete();

      // THEN
      expect(taskService.create).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Task>>();
      const task = { id: 123 };
      jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ task });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskService.update).toHaveBeenCalledWith(task);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProfessionById', () => {
      it('Should return tracked Profession primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProfessionById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackJobById', () => {
      it('Should return tracked Job primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackJobById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedProfession', () => {
      it('Should return option if no Profession is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedProfession(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Profession for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedProfession(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Profession is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedProfession(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
