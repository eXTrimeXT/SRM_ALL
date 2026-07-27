/**
 * 寻源核心 ts 类型声明
 */

// 寻源详情 tab params 声明
export interface AttrsParams {
  flag?: 'add' | 'edit' | 'view' | 'quote';
  readonly?: boolean;
  row?: any;
  tabName?: string;
}
