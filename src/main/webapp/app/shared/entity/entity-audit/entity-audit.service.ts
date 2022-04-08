import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { EntityAuditEvent } from './entity-audit-event.model';

@Injectable({ providedIn: 'root' })
export class EntityAuditService {
  constructor(private http: HttpClient) {}

  getAllAudited(): Observable<string[]> {
    return this.http.get<string[]>('api/audits/entity/all');
  }

  findByEntity(entity: string, limit: number): Observable<HttpResponse<EntityAuditEvent[]>> {
    const params: HttpParams = new HttpParams().append('entityType', entity).append('limit', limit.toString());

    return this.http.get<EntityAuditEvent[]>('api/audits/entity/changes', {
      params,
      observe: 'response',
    });
  }

  getPrevVersion(qualifiedName: string, entityId: string, commitVersion: number): Observable<HttpResponse<EntityAuditEvent>> {
    const params: HttpParams = new HttpParams()
      .append('qualifiedName', qualifiedName)
      .append('entityId', entityId)
      .append('commitVersion', commitVersion.toString());

    return this.http.get<EntityAuditEvent>('api/audits/entity/changes/version/previous', {
      params,
      observe: 'response',
    });
  }

  getHistoryByEntityId(qualifiedName: string, entityId: string): Observable<any> {
    const params: HttpParams = new HttpParams().append('qualifiedName', qualifiedName || '').append('entityId', entityId || '-1');

    return this.http.get<any>('/services/bmms/api/audits/entity/history', {
      params,
      observe: 'body',
    });
  }
}
