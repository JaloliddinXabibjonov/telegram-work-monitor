import { NgModule } from '@angular/core';
import { EntityViewModalComponent } from './entity-view-modal.component';
import { SharedModule } from '../../shared.module';
import { NzListModule } from 'ng-zorro-antd/list';
import { RouterModule } from '@angular/router';
import { DatePipe } from '@angular/common';

@NgModule({
  imports: [SharedModule, NzListModule, RouterModule],
  declarations: [EntityViewModalComponent],
  exports: [EntityViewModalComponent],
  providers: [DatePipe],
})
export class EntityViewModule {}
