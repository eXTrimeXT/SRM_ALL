export const generateSchemaPage = ({ name, schema, useScope, components }) => ({
  name: name,
  setup() {
    return (h) => {
      const innerScope = useScope && useScope()

      return h('RenderEngine', {
        props: {
          schemaKey: name,
          scope: innerScope && (innerScope.value || innerScope),
          components: components,
          schema: schema,
        },
      })
    }
  },
})

/**
 * 按顺序生成 x-index, 减少手写维护
 * 维护到底层，默认开启？
 *
 * @param {Object} properties
 * @returns {Object}
 */
export const generateXindexInOrder = (properties) => {
  return Object.keys(properties).reduce(
    (acc, fieldName, index) => ({
      ...acc,
      [fieldName]: {
        ...properties[fieldName],
        'x-index': properties[fieldName]['x-index'] ?? index + 1,
      },
    }),
    {},
  )
}
