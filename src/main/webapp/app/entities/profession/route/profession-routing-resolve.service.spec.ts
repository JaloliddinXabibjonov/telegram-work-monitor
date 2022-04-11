jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IProfession, Profession } from '../profession.model';
import { ProfessionService } from '../service/profession.service';

import { ProfessionRoutingResolveService } from './profession-routing-resolve.service';

describe('Profession routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProfessionRoutingResolveService;
  let service: ProfessionService;
  let resultProfession: IProfession | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(ProfessionRoutingResolveService);
    service = TestBed.inject(ProfessionService);
    resultProfession = undefined;
  });

  describe('resolve', () => {
    it('should return IProfession returned by find', () => {
      // GIVEN
      service.find = jest.fn(name => of(new HttpResponse({ body: { name } })));
      mockActivatedRouteSnapshot.params = { name: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfession = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultProfession).toEqual({ name: 'ABC' });
    });

    it('should return new IProfession if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfession = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProfession).toEqual(new Profession());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Profession })));
      mockActivatedRouteSnapshot.params = { name: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfession = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultProfession).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
