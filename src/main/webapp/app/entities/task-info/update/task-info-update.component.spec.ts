jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TaskInfoService } from '../service/task-info.service';
import { ITaskInfo, TaskInfo } from '../task-info.model';

import { TaskInfoUpdateComponent } from './task-info-update.component';

describe('TaskInfo Management Update Component', () => {
  let comp: TaskInfoUpdateComponent;
  let fixture: ComponentFixture<TaskInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskInfoService: TaskInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TaskInfoUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TaskInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskInfoService = TestBed.inject(TaskInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const taskInfo: ITaskInfo = { id: 456 };

      activatedRoute.data = of({ taskInfo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(taskInfo));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskInfo>>();
      const taskInfo = { id: 123 };
      jest.spyOn(taskInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskInfo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskInfoService.update).toHaveBeenCalledWith(taskInfo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskInfo>>();
      const taskInfo = new TaskInfo();
      jest.spyOn(taskInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskInfo }));
      saveSubject.complete();

      // THEN
      expect(taskInfoService.create).toHaveBeenCalledWith(taskInfo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TaskInfo>>();
      const taskInfo = { id: 123 };
      jest.spyOn(taskInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskInfoService.update).toHaveBeenCalledWith(taskInfo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
