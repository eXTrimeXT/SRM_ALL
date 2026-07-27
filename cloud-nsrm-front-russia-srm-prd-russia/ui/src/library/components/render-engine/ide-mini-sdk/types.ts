import type { autorun, observable, FieldModel, ViewModel } from '@meicloud/render-engine'

export type RenderEngineScope<
  ComponentProps = Record<string, any>,
  ExtendsParams = Record<string, any>,
> = {
  $form: ViewModel
  $self: FieldModel
  $props: ComponentProps
  $effect: typeof autorun.effect
  $memo: typeof autorun.memo
  $values: Record<string, any>
  $observable: (target: any, deps?: any[]) => ReturnType<typeof autorun.memo<typeof observable>>
  $$safeGetScope: <T = any>(address: string, scope?: RenderEngineScope) => T
} & ExtendsParams
