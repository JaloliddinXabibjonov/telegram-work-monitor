jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITaskInfo, TaskInfo } from '../task-info.model';
import { TaskInfoService } from '../service/task-info.service';

import { TaskInfoRoutingResolveService } from './task-info-routing-resolve.service';

describe('TaskInfo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TaskInfoRoutingResolveService;
  let service: TaskInfoService;
  let resultTaskInfo: ITaskInfo | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(TaskInfoRoutingResolveService);
    service = TestBed.inject(TaskInfoService);
    resultTaskInfo = undefined;
  });

  describe('resolve', () => {
    it('should return ITaskInfo returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTaskInfo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTaskInfo).toEqual({ id: 123 });
    });

    it('should return new ITaskInfo if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTaskInfo = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTaskInfo).toEqual(new TaskInfo());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TaskInfo })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTaskInfo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTaskInfo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
