import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TaskInfoComponent } from './list/task-info.component';
import { TaskInfoDetailComponent } from './detail/task-info-detail.component';
import { TaskInfoUpdateComponent } from './update/task-info-update.component';
import { TaskInfoDeleteDialogComponent } from './delete/task-info-delete-dialog.component';
import { TaskInfoRoutingModule } from './route/task-info-routing.module';

@NgModule({
  imports: [SharedModule, TaskInfoRoutingModule],
  declarations: [TaskInfoComponent, TaskInfoDetailComponent, TaskInfoUpdateComponent, TaskInfoDeleteDialogComponent],
  entryComponents: [TaskInfoDeleteDialogComponent],
})
export class TaskInfoModule {}
