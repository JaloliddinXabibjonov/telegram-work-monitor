import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProfessionComponent } from './list/profession.component';
import { ProfessionDetailComponent } from './detail/profession-detail.component';
import { ProfessionUpdateComponent } from './update/profession-update.component';
import { ProfessionDeleteDialogComponent } from './delete/profession-delete-dialog.component';
import { ProfessionRoutingModule } from './route/profession-routing.module';

@NgModule({
  imports: [SharedModule, ProfessionRoutingModule],
  declarations: [ProfessionComponent, ProfessionDetailComponent, ProfessionUpdateComponent, ProfessionDeleteDialogComponent],
  entryComponents: [ProfessionDeleteDialogComponent],
})
export class ProfessionModule {}
