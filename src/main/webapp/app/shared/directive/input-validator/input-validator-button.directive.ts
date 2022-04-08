import { Directive, HostListener, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { EventManager } from '../../../core/util/event-manager.service';
import { AlertService } from '../../../core/util/alert.service';

@Directive({
  selector: '[input-validator-button]',
})
export class InputValidatorButtonDirective {
  @Input('input-validator-button') formGroup: FormGroup;
  @Input() error = 'entity.validation.notFilled';

  constructor(private eventManager: EventManager, private alertService: AlertService) {}

  @HostListener('click', ['$event'])
  onClick(event: PointerEvent): void {
    if (!this.formGroup) {
      // eslint-disable-next-line no-console
      return console.error('FormGroup does not set on directive argument');
    }

    if (this.formGroup.invalid) {
      this.eventManager.broadcast('InputValidator');
      this.alertService.danger(this.error);
      event.preventDefault();
    }
  }
}
