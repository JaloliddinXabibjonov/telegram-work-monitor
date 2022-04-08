import { ITask } from 'app/entities/task/task.model';

export interface IProfession {
  id?: number;
  name?: string;
  description?: string | null;
  tasks?: ITask[] | null;
}

export class Profession implements IProfession {
  constructor(public id?: number, public name?: string, public description?: string | null, public tasks?: ITask[] | null) {}
}

export function getProfessionIdentifier(profession: IProfession): number | undefined {
  return profession.id;
}
