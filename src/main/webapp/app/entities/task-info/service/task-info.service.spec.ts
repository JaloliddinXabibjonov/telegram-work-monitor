import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Status } from 'app/entities/enumerations/status.model';
import { ITaskInfo, TaskInfo } from '../task-info.model';

import { TaskInfoService } from './task-info.service';

describe('TaskInfo Service', () => {
  let service: TaskInfoService;
  let httpMock: HttpTestingController;
  let elemDefault: ITaskInfo;
  let expectedResult: ITaskInfo | ITaskInfo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TaskInfoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      price: 0,
      status: Status.NEW,
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TaskInfo', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TaskInfo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskInfo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          price: 1,
          status: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskInfo', () => {
      const patchObject = Object.assign(
        {
          price: 1,
          description: 'BBBBBB',
        },
        new TaskInfo()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskInfo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          price: 1,
          status: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TaskInfo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTaskInfoToCollectionIfMissing', () => {
      it('should add a TaskInfo to an empty array', () => {
        const taskInfo: ITaskInfo = { id: 123 };
        expectedResult = service.addTaskInfoToCollectionIfMissing([], taskInfo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskInfo);
      });

      it('should not add a TaskInfo to an array that contains it', () => {
        const taskInfo: ITaskInfo = { id: 123 };
        const taskInfoCollection: ITaskInfo[] = [
          {
            ...taskInfo,
          },
          { id: 456 },
        ];
        expectedResult = service.addTaskInfoToCollectionIfMissing(taskInfoCollection, taskInfo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskInfo to an array that doesn't contain it", () => {
        const taskInfo: ITaskInfo = { id: 123 };
        const taskInfoCollection: ITaskInfo[] = [{ id: 456 }];
        expectedResult = service.addTaskInfoToCollectionIfMissing(taskInfoCollection, taskInfo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskInfo);
      });

      it('should add only unique TaskInfo to an array', () => {
        const taskInfoArray: ITaskInfo[] = [{ id: 123 }, { id: 456 }, { id: 93929 }];
        const taskInfoCollection: ITaskInfo[] = [{ id: 123 }];
        expectedResult = service.addTaskInfoToCollectionIfMissing(taskInfoCollection, ...taskInfoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskInfo: ITaskInfo = { id: 123 };
        const taskInfo2: ITaskInfo = { id: 456 };
        expectedResult = service.addTaskInfoToCollectionIfMissing([], taskInfo, taskInfo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskInfo);
        expect(expectedResult).toContain(taskInfo2);
      });

      it('should accept null and undefined values', () => {
        const taskInfo: ITaskInfo = { id: 123 };
        expectedResult = service.addTaskInfoToCollectionIfMissing([], null, taskInfo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskInfo);
      });

      it('should return initial array if no TaskInfo is added', () => {
        const taskInfoCollection: ITaskInfo[] = [{ id: 123 }];
        expectedResult = service.addTaskInfoToCollectionIfMissing(taskInfoCollection, undefined, null);
        expect(expectedResult).toEqual(taskInfoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
