import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'job',
        data: { pageTitle: 'workmonitorApp.job.home.title' },
        loadChildren: () => import('./job/job.module').then(m => m.JobModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'workmonitorApp.task.home.title' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'task-info',
        data: { pageTitle: 'workmonitorApp.taskInfo.home.title' },
        loadChildren: () => import('./task-info/task-info.module').then(m => m.TaskInfoModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'workmonitorApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'profession',
        data: { pageTitle: 'workmonitorApp.profession.home.title' },
        loadChildren: () => import('./profession/profession.module').then(m => m.ProfessionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
