import { ITask } from 'app/entities/task/task.model';

export interface IProfession {
  name?: string;
  description?: string | null;
  tasks?: ITask[] | null;
}

export class Profession implements IProfession {
  constructor(public name?: string, public description?: string | null, public tasks?: ITask[] | null) {}
}

export function getProfessionIdentifier(profession: IProfession): string | undefined {
  return profession.name;
}
