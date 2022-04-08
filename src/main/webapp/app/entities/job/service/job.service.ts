import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IEntityConfig } from '../../../shared/entity/entity-config.model';
import { EntityService } from '../../../shared/entity/entity.service';
import { EntityUpdateOptions } from '../../../shared/entity/entity-update/entity-update-options.model';
import { EntityViewOptions } from '../../../shared/entity/entity-view/entity-view-options.model';
import { JobUpdateComponent } from '../update/job-update.component';
import { createRequestOption } from 'app/core/request/request-util';
import { IJob, getJobIdentifier } from '../job.model';
import { NglFilterField, NglFilterFieldType } from 'ngl-filter-field';

export type EntityResponseType = HttpResponse<IJob>;
export type EntityArrayResponseType = HttpResponse<IJob[]>;

@Injectable({ providedIn: 'root' })
export class JobService implements IEntityConfig {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/jobs');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
    private entityService: EntityService
  ) {}

  create(job: IJob): Observable<EntityResponseType> {
    return this.http.post<IJob>(this.resourceUrl, job, { observe: 'response' });
  }

  update(job: IJob): Observable<EntityResponseType> {
    return this.http.put<IJob>(`${this.resourceUrl}/${getJobIdentifier(job) as number}`, job, { observe: 'response' });
  }

  partialUpdate(job: IJob): Observable<EntityResponseType> {
    return this.http.patch<IJob>(`${this.resourceUrl}/${getJobIdentifier(job) as number}`, job, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJob>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJob[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJobToCollectionIfMissing(jobCollection: IJob[], ...jobsToCheck: (IJob | null | undefined)[]): IJob[] {
    const jobs: IJob[] = jobsToCheck.filter(isPresent);
    if (jobs.length > 0) {
      const jobCollectionIdentifiers = jobCollection.map(jobItem => getJobIdentifier(jobItem)!);
      const jobsToAdd = jobs.filter(jobItem => {
        const jobIdentifier = getJobIdentifier(jobItem);
        if (jobIdentifier == null || jobCollectionIdentifiers.includes(jobIdentifier)) {
          return false;
        }
        jobCollectionIdentifiers.push(jobIdentifier);
        return true;
      });
      return [...jobsToAdd, ...jobCollection];
    }
    return jobCollection;
  }

  getFilterFields(): NglFilterField[] {
    return [
      { name: 'id.equals', type: NglFilterFieldType.NUMBER, translation: 'global.field.id' },
      {
        name: 'id.equals',
        type: NglFilterFieldType.NUMBER,
        translation: 'workMonitorApp.job.id',
      },
      {
        name: 'name.contains',
        type: NglFilterFieldType.TEXT,
        translation: 'workMonitorApp.job.name',
      },
    ];
  }

  openUpdate(job?: IJob): void {
    const options: EntityUpdateOptions<JobUpdateComponent> = {
      component: JobUpdateComponent,
      componentInstances: {
        job: job || {},
      },
      modalOptions: { size: 'lg' },
    };

    this.entityService.update(options);
  }

  openView(entity: IJob): void {
    const options: EntityViewOptions[] = [
      { title: 'global.field.id', value: entity.id },
      {
        title: 'workMonitorApp.job.id',
        value: entity.id,
        type: 'translation',
      },
      {
        title: 'workMonitorApp.job.name',
        value: entity.name,
        type: 'translation',
      },
    ];

    this.entityService.view(options, entity.id);
  }
  openDelete(id: number): void {
    const options = {
      useFunction: this.delete(id),
      event: 'jobListModification',
      alertTranslation: 'workMonitorApp.job.deleted',
      alertTranslationValue: id,
    };

    this.entityService.delete(options);
  }

  openHistory(id: number): void {
    const options = {
      qualifiedName: 'JOB',
      entityId: id,
      i18nPrefix: 'workMonitorApp.job',
    };

    this.entityService.history(options);
  }
}
