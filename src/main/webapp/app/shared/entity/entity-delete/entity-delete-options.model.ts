import { Observable } from 'rxjs';

export class EntityDeleteOptions {
  useFunction!: Observable<any>;
  event!: string;
  alertTranslation?: string;
  alertTranslationValue: any;
}
