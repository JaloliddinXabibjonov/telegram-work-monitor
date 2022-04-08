import { NgModule } from '@angular/core';
import { DevopsSelectInputComponent } from './devops-select-input.component';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [CommonModule, FormsModule, NzSelectModule],
  declarations: [DevopsSelectInputComponent],
  exports: [DevopsSelectInputComponent],
})
export class DevopsSelectInputModule {}
