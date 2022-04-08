import { Authority } from '../../config/authority.constants';
import { Subject } from 'rxjs';

export abstract class BaseComponent {
  protected readonly destroy$ = new Subject<void>();
  readonly authority = Authority;
  loading: boolean;

  protected unsubscribe(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
