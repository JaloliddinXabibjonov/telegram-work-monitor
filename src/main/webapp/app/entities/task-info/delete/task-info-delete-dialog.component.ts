import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITaskInfo } from '../task-info.model';
import { TaskInfoService } from '../service/task-info.service';

@Component({
  templateUrl: './task-info-delete-dialog.component.html',
})
export class TaskInfoDeleteDialogComponent {
  taskInfo?: ITaskInfo;

  constructor(protected taskInfoService: TaskInfoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskInfoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
