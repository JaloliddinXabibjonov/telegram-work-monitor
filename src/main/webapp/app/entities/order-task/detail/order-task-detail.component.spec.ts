import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderTaskDetailComponent } from './order-task-detail.component';

describe('OrderTask Management Detail Component', () => {
  let comp: OrderTaskDetailComponent;
  let fixture: ComponentFixture<OrderTaskDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderTaskDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orderTask: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrderTaskDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrderTaskDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orderTask on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orderTask).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
