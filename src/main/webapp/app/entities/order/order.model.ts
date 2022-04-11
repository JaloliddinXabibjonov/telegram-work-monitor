import * as dayjs from 'dayjs';
import { IJob } from 'app/entities/job/job.model';
import { ITaskInfo } from 'app/entities/task-info/task-info.model';

export interface IOrder {
  id?: number;
  name?: string;
  chatId?: string | null;
  employee?: string | null;
  startedDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  job?: IJob;
  taskInfo?: ITaskInfo | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public name?: string,
    public chatId?: string | null,
    public employee?: string | null,
    public startedDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null,
    public job?: IJob,
    public taskInfo?: ITaskInfo | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
