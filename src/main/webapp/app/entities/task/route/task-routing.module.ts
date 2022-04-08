import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TaskComponent } from '../list/task.component';

const taskRoute: Routes = [
  {
    path: '',
    component: TaskComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(taskRoute)],
  exports: [RouterModule],
})
export class TaskRoutingModule {}
