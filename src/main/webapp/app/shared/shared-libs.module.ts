import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { NzInputNumberModule } from 'ng-zorro-antd/input-number';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzResultModule } from 'ng-zorro-antd/result';
import { NglFilterFieldModule } from 'ngl-filter-field';
import { NzPaginationModule } from 'ng-zorro-antd/pagination';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { NglExportAsModule } from 'ngl-export-as';
import { DevopsSelectInputModule } from './component/select-input/devops-select-input.module';
import { InputValidatorModule } from './directive/input-validator/input-validator.module';

@NgModule({
  exports: [
    FormsModule,
    CommonModule,
    NgbModule,
    InfiniteScrollModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    TranslateModule,
    NzInputModule,
    NzInputNumberModule,
    NzButtonModule,
    NzCheckboxModule,
    NzIconModule,
    NzTableModule,
    NzMessageModule,
    NzDatePickerModule,
    NzResultModule,
    NzToolTipModule,
    NglFilterFieldModule,
    NzPaginationModule,
    NglExportAsModule,
    DevopsSelectInputModule,
    InputValidatorModule,
  ],
})
export class SharedLibsModule {}
