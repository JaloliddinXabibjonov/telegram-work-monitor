import { NglFilterField } from 'ngl-filter-field';

export interface IEntityConfig {
  getFilterFields(): NglFilterField[];

  openUpdate<T>(model?: T): void;

  openView<T>(model: T): void;

  openDelete(id: number | string): void;

  openHistory?(id: number): void;
}
