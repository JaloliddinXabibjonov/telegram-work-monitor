import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { Status } from 'app/entities/enumerations/status.model';
import { IOrderTask, OrderTask } from '../order-task.model';

import { OrderTaskService } from './order-task.service';

describe('OrderTask Service', () => {
  let service: OrderTaskService;
  let httpMock: HttpTestingController;
  let elemDefault: IOrderTask;
  let expectedResult: IOrderTask | IOrderTask[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderTaskService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      status: Status.NEW,
      startedDate: currentDate,
      endDate: currentDate,
      employeeUsername: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          startedDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a OrderTask', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          startedDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startedDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.create(new OrderTask()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderTask', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          status: 'BBBBBB',
          startedDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
          employeeUsername: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startedDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderTask', () => {
      const patchObject = Object.assign(
        {
          startedDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
          employeeUsername: 'BBBBBB',
        },
        new OrderTask()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          startedDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderTask', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          status: 'BBBBBB',
          startedDate: currentDate.format(DATE_TIME_FORMAT),
          endDate: currentDate.format(DATE_TIME_FORMAT),
          employeeUsername: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          startedDate: currentDate,
          endDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a OrderTask', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOrderTaskToCollectionIfMissing', () => {
      it('should add a OrderTask to an empty array', () => {
        const orderTask: IOrderTask = { id: 123 };
        expectedResult = service.addOrderTaskToCollectionIfMissing([], orderTask);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderTask);
      });

      it('should not add a OrderTask to an array that contains it', () => {
        const orderTask: IOrderTask = { id: 123 };
        const orderTaskCollection: IOrderTask[] = [
          {
            ...orderTask,
          },
          { id: 456 },
        ];
        expectedResult = service.addOrderTaskToCollectionIfMissing(orderTaskCollection, orderTask);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderTask to an array that doesn't contain it", () => {
        const orderTask: IOrderTask = { id: 123 };
        const orderTaskCollection: IOrderTask[] = [{ id: 456 }];
        expectedResult = service.addOrderTaskToCollectionIfMissing(orderTaskCollection, orderTask);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderTask);
      });

      it('should add only unique OrderTask to an array', () => {
        const orderTaskArray: IOrderTask[] = [{ id: 123 }, { id: 456 }, { id: 28987 }];
        const orderTaskCollection: IOrderTask[] = [{ id: 123 }];
        expectedResult = service.addOrderTaskToCollectionIfMissing(orderTaskCollection, ...orderTaskArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderTask: IOrderTask = { id: 123 };
        const orderTask2: IOrderTask = { id: 456 };
        expectedResult = service.addOrderTaskToCollectionIfMissing([], orderTask, orderTask2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderTask);
        expect(expectedResult).toContain(orderTask2);
      });

      it('should accept null and undefined values', () => {
        const orderTask: IOrderTask = { id: 123 };
        expectedResult = service.addOrderTaskToCollectionIfMissing([], null, orderTask, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderTask);
      });

      it('should return initial array if no OrderTask is added', () => {
        const orderTaskCollection: IOrderTask[] = [{ id: 123 }];
        expectedResult = service.addOrderTaskToCollectionIfMissing(orderTaskCollection, undefined, null);
        expect(expectedResult).toEqual(orderTaskCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
