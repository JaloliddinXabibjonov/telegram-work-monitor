import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'job',
        data: { pageTitle: 'workMonitorApp.job.home.title' },
        loadChildren: () => import('./job/job.module').then(m => m.JobModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'workMonitorApp.task.home.title' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'workMonitorApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'profession',
        data: { pageTitle: 'workMonitorApp.profession.home.title' },
        loadChildren: () => import('./profession/profession.module').then(m => m.ProfessionModule),
      },
      {
        path: 'order-task',
        data: { pageTitle: 'workMonitorApp.orderTask.home.title' },
        loadChildren: () => import('./order-task/order-task.module').then(m => m.OrderTaskModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
