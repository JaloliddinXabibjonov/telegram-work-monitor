import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProfession } from '../profession.model';
import { ProfessionService } from '../service/profession.service';

@Component({
  templateUrl: './profession-delete-dialog.component.html',
})
export class ProfessionDeleteDialogComponent {
  profession?: IProfession;

  constructor(protected professionService: ProfessionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.professionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
