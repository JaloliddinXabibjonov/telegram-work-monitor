import { ITask } from 'app/entities/task/task.model';
import { IOrder } from 'app/entities/order/order.model';

export interface IJob {
  id?: number;
  name?: string;
  tasks?: ITask[] | null;
  orders?: IOrder[] | null;
}

export class Job implements IJob {
  constructor(public id?: number, public name?: string, public tasks?: ITask[] | null, public orders?: IOrder[] | null) {}
}

export function getJobIdentifier(job: IJob): number | undefined {
  return job.id;
}
