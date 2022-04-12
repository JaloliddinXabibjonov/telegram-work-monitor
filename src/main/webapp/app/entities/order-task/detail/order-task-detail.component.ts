import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrderTask } from '../order-task.model';

@Component({
  selector: 'jhi-order-task-detail',
  templateUrl: './order-task-detail.component.html',
})
export class OrderTaskDetailComponent implements OnInit {
  orderTask: IOrderTask | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderTask }) => {
      this.orderTask = orderTask;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
