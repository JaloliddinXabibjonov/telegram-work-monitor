import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntityConfig } from '../../../shared/entity/entity-config.model';
import { EntityService } from '../../../shared/entity/entity.service';
import { EntityUpdateOptions } from '../../../shared/entity/entity-update/entity-update-options.model';
import { EntityViewOptions } from '../../../shared/entity/entity-view/entity-view-options.model';
import { ProfessionUpdateComponent } from '../update/profession-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { IProfession, getProfessionIdentifier } from '../profession.model';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<IProfession>;
export type EntityArrayResponseType = HttpResponse<IProfession[]>;

@Injectable({ providedIn: 'root' })
export class ProfessionService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/professions');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService
  ) {}

  create(profession: IProfession): Observable<EntityResponseType> {
    return this.http.post<IProfession>(this.resourceUrl, profession, { observe: 'response' });
  }

  update(profession: IProfession): Observable<EntityResponseType> {
    return this.http.put<IProfession>(`${this.resourceUrl}/${getProfessionIdentifier(profession) as string}`, profession, {
      observe: 'response',
    });
  }

  partialUpdate(profession: IProfession): Observable<EntityResponseType> {
    return this.http.patch<IProfession>(`${this.resourceUrl}/${getProfessionIdentifier(profession) as string}`, profession, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IProfession>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProfession[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProfessionToCollectionIfMissing(
    professionCollection: IProfession[],
    ...professionsToCheck: (IProfession | null | undefined)[]
  ): IProfession[] {
    const professions: IProfession[] = professionsToCheck.filter(isPresent);
    if (professions.length > 0) {
      const professionCollectionIdentifiers = professionCollection.map(professionItem => getProfessionIdentifier(professionItem)!);
      const professionsToAdd = professions.filter(professionItem => {
        const professionIdentifier = getProfessionIdentifier(professionItem);
        if (professionIdentifier == null || professionCollectionIdentifiers.includes(professionIdentifier)) {
          return false;
        }
        professionCollectionIdentifiers.push(professionIdentifier);
        return true;
      });
      return [...professionsToAdd, ...professionCollection];
    }
    return professionCollection;
  }

  getFilterFields(): NglFilterField[] {
    return [
      { name: 'name.contains', type: NglFilterFieldType.TEXT, translation: 'global.field.name' },
      {
        name: 'name.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.profession.name',
      },
      {
        name: 'description.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.profession.description',
      },
    ];
  }

  openUpdate(profession?: IProfession): void {
    const options: EntityUpdateOptions<ProfessionUpdateComponent> = {
      component: ProfessionUpdateComponent,
      componentInstances: {
        profession: profession || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: IProfession): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.name', value: entity.name },
      {
        title: 'workMonitorApp.profession.name',
        value: entity.name,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.profession.description',
        value: entity.description,
        type: 'translation',
      },
    ];

    this.entityService.view(options, entity.name);
  }
  openDelete(id: string): void {
    const options = {
      useFunction: this.delete(id),
      event: 'professionListModification',
      alertTranslation: 'workMonitorApp.profession.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  // openHistory(name: string): void {
  //   const options = {
  //     qualifiedName: 'PROFESSION',
  //     entityId: name,
  //     i18nPrefix: 'workMonitorApp.profession',
  //   };
  //
  //   this.entityService.history(options);
  // }
}
