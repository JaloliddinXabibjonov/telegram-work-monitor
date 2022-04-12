import * as dayjs from 'dayjs';
import { ITask } from 'app/entities/task/task.model';
import { IOrder } from 'app/entities/order/order.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IOrderTask {
  id?: number;
  status?: Status | null;
  startedDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  employeeUsername?: string | null;
  task?: ITask | null;
  order?: IOrder | null;
}

export class OrderTask implements IOrderTask {
  constructor(
    public id?: number,
    public status?: Status | null,
    public startedDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null,
    public employeeUsername?: string | null,
    public task?: ITask | null,
    public order?: IOrder | null
  ) {}
}

export function getOrderTaskIdentifier(orderTask: IOrderTask): number | undefined {
  return orderTask.id;
}
