import { defineComponent } from 'vue-demi'
import { h, RecursionField, Schema } from '@meicloud/render-engine'

import { Collapsible } from './collapsible'

export const ToolbarBlock = defineComponent<{ schema: Schema[], collapsible?: boolean }>({
  name: 'QueryFormToolbarBlock',
  props: {
    schema: {
      type: Array,
      default: []
    },
    collapsible: {
      type: Boolean,
      default: true
    }
  },
  setup (props, { listeners }) {
    return () => {
      return h(
        'div',
        {
          staticClass: 'query-form__toolbar',
          style: 'display: inline-flex; gap: 16px; margin-bottom: 16px; min-width: 210px; justify-content: flex-end;'
        },
        {
          default: () => [
            props.schema.map(schema =>
              h(RecursionField, { key: schema.name, props: { name: schema.name, schema } }, {}),
            ),
            props.collapsible
              ? h(
                  Collapsible,
                  {
                    on: listeners
                  },
                  {},
                )
              : null
          ]
        },
      )
    }
  }
})
