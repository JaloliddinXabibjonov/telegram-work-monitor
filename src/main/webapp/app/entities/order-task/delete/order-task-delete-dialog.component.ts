import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderTask } from '../order-task.model';
import { OrderTaskService } from '../service/order-task.service';

@Component({
  templateUrl: './order-task-delete-dialog.component.html',
})
export class OrderTaskDeleteDialogComponent {
  orderTask?: IOrderTask;

  constructor(protected orderTaskService: OrderTaskService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderTaskService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
