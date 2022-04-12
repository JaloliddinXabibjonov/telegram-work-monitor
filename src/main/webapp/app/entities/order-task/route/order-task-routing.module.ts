import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OrderTaskComponent } from '../list/order-task.component';

const orderTaskRoute: Routes = [
  {
    path: '',
    component: OrderTaskComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderTaskRoute)],
  exports: [RouterModule],
})
export class OrderTaskRoutingModule {}
