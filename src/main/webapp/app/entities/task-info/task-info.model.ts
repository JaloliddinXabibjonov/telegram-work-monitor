import { ITask } from 'app/entities/task/task.model';
import { IOrder } from 'app/entities/order/order.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ITaskInfo {
  id?: number;
  price?: number | null;
  status?: Status | null;
  description?: string | null;
  tasks?: ITask[] | null;
  orders?: IOrder[] | null;
}

export class TaskInfo implements ITaskInfo {
  constructor(
    public id?: number,
    public price?: number | null,
    public status?: Status | null,
    public description?: string | null,
    public tasks?: ITask[] | null,
    public orders?: IOrder[] | null
  ) {}
}

export function getTaskInfoIdentifier(taskInfo: ITaskInfo): number | undefined {
  return taskInfo.id;
}
