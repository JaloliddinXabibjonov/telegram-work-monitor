import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ProfessionComponent } from '../list/profession.component';

const professionRoute: Routes = [
  {
    path: '',
    component: ProfessionComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(professionRoute)],
  exports: [RouterModule],
})
export class ProfessionRoutingModule {}
