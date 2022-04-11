import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TaskInfoComponent } from '../list/task-info.component';

const taskInfoRoute: Routes = [
  {
    path: '',
    component: TaskInfoComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(taskInfoRoute)],
  exports: [RouterModule],
})
export class TaskInfoRoutingModule {}
