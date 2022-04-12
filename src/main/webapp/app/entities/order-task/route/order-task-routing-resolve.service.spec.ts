jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IOrderTask, OrderTask } from '../order-task.model';
import { OrderTaskService } from '../service/order-task.service';

import { OrderTaskRoutingResolveService } from './order-task-routing-resolve.service';

describe('OrderTask routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: OrderTaskRoutingResolveService;
  let service: OrderTaskService;
  let resultOrderTask: IOrderTask | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(OrderTaskRoutingResolveService);
    service = TestBed.inject(OrderTaskService);
    resultOrderTask = undefined;
  });

  describe('resolve', () => {
    it('should return IOrderTask returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrderTask = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultOrderTask).toEqual({ id: 123 });
    });

    it('should return new IOrderTask if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrderTask = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultOrderTask).toEqual(new OrderTask());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as OrderTask })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrderTask = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultOrderTask).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
