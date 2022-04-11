import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { JobComponent } from '../list/job.component';

const jobRoute: Routes = [
  {
    path: '',
    component: JobComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(jobRoute)],
  exports: [RouterModule],
})
export class JobRoutingModule {}
