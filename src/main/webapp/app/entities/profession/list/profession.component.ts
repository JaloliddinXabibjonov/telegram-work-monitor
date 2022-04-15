import { Component, OnDestroy, OnInit } from '@angular/core';
import { TaskService } from '../../task/service/task.service';
import { combineLatest, Subscription } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { finalize, takeUntil } from 'rxjs/operators';

import { IProfession } from '../profession.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventManager } from '../../../core/util/event-manager.service';
import { ProfessionService } from '../service/profession.service';

@Component({
  selector: 'jhi-profession',
  templateUrl: './profession.component.html',
})
export class ProfessionComponent extends BaseComponent implements OnInit, OnDestroy {
  private modifySubscription?: Subscription;
  totalItems = 0;
  professions?: IProfession[];
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  query: any = {};

  exclude = [];
  filterFields = this.professionService.getFilterFields();

  constructor(
    public professionService: ProfessionService,
    public taskService: TaskService,
    private eventManager: EventManager,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super();
  }

  filter(query: any): void {
    this.query = query;
    this.loadPage();
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.handleNavigation();
    this.loading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    const req = {
      ...this.query,
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
    this.loading = true;
    this.professionService
      .query(req)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe(
        res => this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate),
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.loadPage();
    this.registerChangeList();
  }

  private registerChangeList(): void {
    this.modifySubscription = this.eventManager.subscribe('professionListModification', () => this.loadPage());
  }

  trackName(index: number, item: IProfession): string {
    return item.name!;
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'name') {
      result.push('name');
    }
    return result;
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data, params) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.page = pageNumber;
      }
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  protected onSuccess(data: IProfession[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router
        .navigate(['/profession'], {
          queryParams: {
            page: this.page,
            size: this.itemsPerPage,
            sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
          },
        })
        .then();
    }
    this.professions = data ?? [];
    this.ngbPaginationPage = this.page;

    if (this.itemsPerPage !== this.professions.length) {
      window.scrollTo(0, 0);
    }
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }

  get getOption(): any {
    return {
      totalItems: this.totalItems,
      ...this.query,
    };
  }

  ngOnDestroy(): void {
    this.unsubscribe();
    this.modifySubscription?.unsubscribe();
  }
}
