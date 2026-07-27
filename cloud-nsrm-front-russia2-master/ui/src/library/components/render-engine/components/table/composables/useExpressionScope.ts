import { provide, computed } from 'vue-demi'
import { useExpressionScope, lazyMerge, SchemaExpressionScopeSymbol } from '@meicloud/render-engine'

export const useTableExpressionScopeProvide = (value: Record<string, any>) => {
  const scopeRef = useExpressionScope()

  const expressionScopeRef = computed(() => lazyMerge(scopeRef!.value, value))

  provide(SchemaExpressionScopeSymbol, expressionScopeRef)
}
