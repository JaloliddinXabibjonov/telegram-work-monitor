import * as dayjs from 'dayjs';
import { IJob } from 'app/entities/job/job.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  name?: string;
  price?: number | null;
  chatId?: string | null;
  employee?: string | null;
  status?: OrderStatus | null;
  description?: string | null;
  startedDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  job?: IJob;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public name?: string,
    public price?: number | null,
    public chatId?: string | null,
    public employee?: string | null,
    public status?: OrderStatus | null,
    public description?: string | null,
    public startedDate?: dayjs.Dayjs | null,
    public endDate?: dayjs.Dayjs | null,
    public job?: IJob
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
