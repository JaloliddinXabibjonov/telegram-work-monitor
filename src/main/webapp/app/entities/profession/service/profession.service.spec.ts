import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProfession, Profession } from '../profession.model';

import { ProfessionService } from './profession.service';

describe('Profession Service', () => {
  let service: ProfessionService;
  let httpMock: HttpTestingController;
  let elemDefault: IProfession;
  let expectedResult: IProfession | IProfession[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProfessionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      name: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Profession', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Profession()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Profession', () => {
      const returnedFromService = Object.assign(
        {
          name: 'BBBBBB',
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

    it('should partial update a Profession', () => {
      const patchObject = Object.assign({}, new Profession());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Profession', () => {
      const returnedFromService = Object.assign(
        {
          name: 'BBBBBB',
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

    it('should delete a Profession', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProfessionToCollectionIfMissing', () => {
      it('should add a Profession to an empty array', () => {
        const profession: IProfession = { name: 'ABC' };
        expectedResult = service.addProfessionToCollectionIfMissing([], profession);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profession);
      });

      it('should not add a Profession to an array that contains it', () => {
        const profession: IProfession = { name: 'ABC' };
        const professionCollection: IProfession[] = [
          {
            ...profession,
          },
          { name: 'CBA' },
        ];
        expectedResult = service.addProfessionToCollectionIfMissing(professionCollection, profession);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Profession to an array that doesn't contain it", () => {
        const profession: IProfession = { name: 'ABC' };
        const professionCollection: IProfession[] = [{ name: 'CBA' }];
        expectedResult = service.addProfessionToCollectionIfMissing(professionCollection, profession);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profession);
      });

      it('should add only unique Profession to an array', () => {
        const professionArray: IProfession[] = [{ name: 'ABC' }, { name: 'CBA' }, { name: '0090b45e-a5ae-4a72-ae69-b315e955255d' }];
        const professionCollection: IProfession[] = [{ name: 'ABC' }];
        expectedResult = service.addProfessionToCollectionIfMissing(professionCollection, ...professionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profession: IProfession = { name: 'ABC' };
        const profession2: IProfession = { name: 'CBA' };
        expectedResult = service.addProfessionToCollectionIfMissing([], profession, profession2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profession);
        expect(expectedResult).toContain(profession2);
      });

      it('should accept null and undefined values', () => {
        const profession: IProfession = { name: 'ABC' };
        expectedResult = service.addProfessionToCollectionIfMissing([], null, profession, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profession);
      });

      it('should return initial array if no Profession is added', () => {
        const professionCollection: IProfession[] = [{ name: 'ABC' }];
        expectedResult = service.addProfessionToCollectionIfMissing(professionCollection, undefined, null);
        expect(expectedResult).toEqual(professionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
