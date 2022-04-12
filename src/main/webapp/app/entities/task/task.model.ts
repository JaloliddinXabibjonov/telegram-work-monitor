import { IJob } from 'app/entities/job/job.model';
import { IProfession } from 'app/entities/profession/profession.model';

export interface ITask {
  id?: number;
  name?: string;
  price?: string | null;
  description?: string | null;
  priority?: number | null;
  job?: IJob | null;
  professions?: IProfession[] | null;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public name?: string,
    public price?: string | null,
    public description?: string | null,
    public priority?: number | null,
    public job?: IJob | null,
    public professions?: IProfession[] | null
  ) {}
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
