import { IconDefinition } from '@ant-design/icons-angular';
import * as AllIcons from '@ant-design/icons-angular/icons';

const antDesignIcons = AllIcons as {
  [key: string]: IconDefinition;
};

export const NZ_ICONS_CONFIG: IconDefinition[] = Object.keys(antDesignIcons).map(key => antDesignIcons[key]);
