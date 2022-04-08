import { AfterViewChecked, Component, ElementRef, EventEmitter, forwardRef, Input, Output, Renderer2 } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NzSelectModeType } from 'ng-zorro-antd/select';
import { finalize, takeUntil } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { ITEMS_PER_PAGE } from '../../../config/pagination.constants';

@Component({
  selector: 'select-input',
  templateUrl: 'devops-select-input.component.html',
  styles: [
    `
      :host {
        border: unset !important;
      }
    `,
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      // eslint-disable-next-line @typescript-eslint/no-use-before-define
      useExisting: forwardRef(() => DevopsSelectInputComponent),
      multi: true,
    },
  ],
})
export class DevopsSelectInputComponent implements ControlValueAccessor, AfterViewChecked {
  @Input() devopsLabelField?: string;
  @Input() devopsValueField?: string;
  @Input() devopsMode: NzSelectModeType = 'default';
  @Input() devopsMaxTagCount?: number;
  @Input() devopsOptionOverflowSize = 8;
  @Input() devopsShowSearch?: boolean;
  @Input() devopsServerSearch?: boolean;
  @Input() data: any[] = [];
  @Input() requestUrl?: string;

  @Output() devopsOnSelect = new EventEmitter();
  @Output() devopsOnSearch = new EventEmitter<string>();

  private readonly destroy$ = new Subject<void>();

  disabled = false;
  item: unknown;
  loading = false;

  constructor(private elementRef: ElementRef, private http: HttpClient, private renderer: Renderer2) {}

  ngAfterViewChecked(): void {
    this.updateBorder();
  }

  private updateBorder(): void {
    const nzInput = (this.elementRef.nativeElement.childNodes as NodeList).item(0)?.firstChild;
    this.renderer.setStyle(nzInput, 'border', this.elementRef.nativeElement.style?.border);
  }

  onChange: (v: unknown) => void = () => {};

  private onTouched = () => {};

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(v: any): void {
    this.item = v || (this.devopsMode === 'multiple' ? [] : {});

    if (this.requestUrl && this.devopsLabelField) {
      this.onSearch((this.item as any)[this.devopsLabelField]);
    }
  }

  emitValue(): void {
    this.onChange(this.item);
    this.devopsOnSelect.emit(this.item);
  }

  onSearch(text: string): void {
    this.devopsOnSearch.emit(text);

    if (!this.devopsServerSearch) return;

    const query: any = {
      size: ITEMS_PER_PAGE,
    };

    if (text && this.devopsLabelField) {
      query[`${this.devopsLabelField}.contains`] = text;
    }

    if (!this.requestUrl) {
      throw new Error(`Property 'resourceUrl' does not set`);
    }

    this.loading = true;
    this.http
      .get(this.requestUrl, { params: query, observe: 'body' })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.loading = false))
      )
      .subscribe((data: any) => (this.data = [...data]));
  }

  compareFn = (o1: any, o2: any): boolean => {
    if (!o1 || !o2) {
      return false;
    }

    if (this.devopsLabelField && !this.devopsValueField) {
      return o1[this.devopsLabelField] === o2[this.devopsLabelField];
    }

    return o1 === o2;
  };

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
}
