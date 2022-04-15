import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize, pluck, takeUntil } from 'rxjs/operators';

import { IProfession, Profession } from '../profession.model';
import { ProfessionService } from '../service/profession.service';
import { EventManager } from 'app/core/util/event-manager.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../../core/util/alert.service';
import { BaseComponent } from '../../../shared/class/base-component.model';

@Component({
  templateUrl: './profession-update.component.html',
})
export class ProfessionUpdateComponent extends BaseComponent implements OnInit, OnDestroy {
  isSaving = false;
  readonly profession!: IProfession;

  editForm = this.fb.group({
    name: [null, [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
    description: [],
  });

  constructor(
    private readonly eventManager: EventManager,
    private readonly alertService: AlertService,
    public readonly professionService: ProfessionService,
    public readonly activeModal: NgbActiveModal,
    private readonly fb: FormBuilder
  ) {
    super();
  }

  ngOnDestroy(): void {
    this.unsubscribe();
  }

  ngOnInit(): void {
    this.updateForm(this.profession);
  }

  save(): void {
    this.loading = true;
    const profession = this.createFromForm();
    if (profession.name) {
      this.update(profession);
    } else {
      this.create(profession);
    }
  }

  private update(profession: IProfession): void {
    this.professionService
      .update(profession)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(() => this.onSuccess('updated', profession.name));
  }

  private create(profession: IProfession): void {
    this.professionService
      .create(profession)
      .pipe(
        pluck('name'),
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(name => this.onSuccess('created', name));
  }

  private onSuccess(action: 'created' | 'updated', id: any): void {
    this.eventManager.broadcast('ProfessionListModified');
    this.alertService.success('workMonitorApp.profession.' + action, id, true);
    this.activeModal.dismiss();
  }

  protected updateForm(profession: IProfession): void {
    this.editForm.patchValue({
      name: profession.name,
      description: profession.description,
    });
  }

  protected createFromForm(): IProfession {
    return {
      ...new Profession(),
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
