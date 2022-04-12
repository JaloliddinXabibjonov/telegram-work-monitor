export interface IJob {
  id?: number;
  name?: string;
}

export class Job implements IJob {
  constructor(public id?: number, public name?: string) {}
}

export function getJobIdentifier(job: IJob): number | undefined {
  return job.id;
}
