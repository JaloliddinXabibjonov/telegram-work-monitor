import { IConfig } from 'ngx-mask/lib/config';

type Type =
  | 'date' // string
  | 'mask' // string | [string, IConfig['patterns']]
  | 'translation' // string
  | 'link' // null
  | 'boolean' // BmBooleanBadgeType
  | 'file' // BmFile
  | 'amount' // AmountRatio
  | 'json'; // null

export class EntityViewOptions {
  title!: string;
  value: any | HTMLElement;
  type?: Type;
  date?: string;
  mask?: string | [string, IConfig['patterns']];
  translation?: string;
  link?: (v: EntityViewOptions) => void;
}

export class EntityViewOptions2<T> {
  model!: T;
  translationPrefix!: string;
  properties?: {
    excluded?: (keyof T)[];
    custom?: CustomProperty<T>[];
  };
}

class CustomProperty<T> {
  name!: keyof T;
  type?: Type;
  translation?: string;
  format?: any;
  onClick?: () => void;
}
