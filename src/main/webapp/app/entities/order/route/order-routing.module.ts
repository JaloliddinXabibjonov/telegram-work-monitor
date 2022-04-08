import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { OrderComponent } from '../list/order.component';

const orderRoute: Routes = [
  {
    path: '',
    component: OrderComponent,
    data: {
      defaultSort: 'id,asc',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderRoute)],
  exports: [RouterModule],
})
export class OrderRoutingModule {}
