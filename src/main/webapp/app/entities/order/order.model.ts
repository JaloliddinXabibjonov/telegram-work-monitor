import * as dayjs from 'dayjs';
import { IJob } from 'app/entities/job/job.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface IOrder {
  id?: number;
  startedDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  status?: Status | null;
  job?: IJob | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public startedDate?: dayjs.Dayjs,
    public endDate?: dayjs.Dayjs,
    public status?: Status | null,
    public job?: IJob | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
