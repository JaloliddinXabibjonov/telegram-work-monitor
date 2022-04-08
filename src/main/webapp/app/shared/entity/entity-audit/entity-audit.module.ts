import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DiffMatchPatchModule } from 'ng-diff-match-patch';

import { SharedModule } from 'app/shared/shared.module';
import { EntityAuditHistoryModalComponent } from './entity-audit-history-modal.component';
import { NzSpinModule } from 'ng-zorro-antd/spin';

@NgModule({
  imports: [CommonModule, SharedModule, DiffMatchPatchModule, NzSpinModule],
  declarations: [EntityAuditHistoryModalComponent],
})
export class EntityAuditModule {}
