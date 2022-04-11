import { Component, OnInit, OnDestroy } from '@angular/core';
import { TaskService } from '../../task/service/task.service';
import { OrderService } from '../../order/service/order.service';
import { Subscription } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BaseComponent } from '../../../shared/class/base-component.model';
import { finalize, takeUntil } from 'rxjs/operators';

import { ITaskInfo } from '../task-info.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { EventManager } from '../../../core/util/event-manager.service';
import { TaskInfoService } from '../service/task-info.service';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-task-info',
  templateUrl: './task-info.component.html',
})
export class TaskInfoComponent extends BaseComponent implements OnInit, OnDestroy {
  private modifySubscription?: Subscription;
  totalItems = 0;
  taskInfos?: ITaskInfo[];
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  query: any = {};

  exclude = [];
  filterFields = this.taskInfoService.getFilterFields();

  constructor(
    public taskInfoService: TaskInfoService,
    public taskService: TaskService,
    public orderService: OrderService,
    private eventManager: EventManager,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
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
    this.loading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    const req = {
      ...this.query,
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
    this.loading = true;
    this.taskInfoService
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
    this.modifySubscription = this.eventManager.subscribe('taskInfoListModification', () => this.loadPage());
  }

  trackId(index: number, item: ITaskInfo): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
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

  protected onSuccess(data: ITaskInfo[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router
        .navigate(['/task-info'], {
          queryParams: {
            page: this.page,
            size: this.itemsPerPage,
            sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
          },
        })
        .then();
    }
    this.taskInfos = data ?? [];
    this.ngbPaginationPage = this.page;

    if (this.itemsPerPage !== this.taskInfos.length) {
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
