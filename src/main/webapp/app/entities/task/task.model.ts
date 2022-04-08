import { IProfession } from 'app/entities/profession/profession.model';
import { IJob } from 'app/entities/job/job.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ITask {
  id?: number;
  name?: string;
  status?: Status | null;
  professions?: IProfession[] | null;
  job?: IJob;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public name?: string,
    public status?: Status | null,
    public professions?: IProfession[] | null,
    public job?: IJob
  ) {}
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
