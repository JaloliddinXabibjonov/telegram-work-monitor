import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderTaskComponent } from './list/order-task.component';
import { OrderTaskDetailComponent } from './detail/order-task-detail.component';
import { OrderTaskUpdateComponent } from './update/order-task-update.component';
import { OrderTaskDeleteDialogComponent } from './delete/order-task-delete-dialog.component';
import { OrderTaskRoutingModule } from './route/order-task-routing.module';

@NgModule({
  imports: [SharedModule, OrderTaskRoutingModule],
  declarations: [OrderTaskComponent, OrderTaskDetailComponent, OrderTaskUpdateComponent, OrderTaskDeleteDialogComponent],
  entryComponents: [OrderTaskDeleteDialogComponent],
})
export class OrderTaskModule {}
