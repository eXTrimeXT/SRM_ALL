import type { ISchema } from '@meicloud/render-engine'

export const schemaMapReplacements = (
  schema: ISchema,
  mapping: Record<string, Record<string, any>>,
) => {
  function recursiveProperties(properties: Record<string, ISchema>) {
    Object.keys(properties).forEach(key => {
      if (!properties[key].name) {
        properties[key].name = key
      }

      recursiveField(properties[key])
    })
  }

  function recursiveField(fieldSchema: ISchema) {
    if (mapping[fieldSchema.name!]) {
      Object.assign(fieldSchema, mapping[fieldSchema.name!])
    }

    if (fieldSchema.properties) {
      recursiveProperties(fieldSchema.properties as unknown as Record<string, ISchema>)
    }
  }

  recursiveField(schema)
}
