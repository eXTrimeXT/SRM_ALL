export type RenderEngineProps = {
  components?: Record<string, any>;
  schema: Record<string, any>;
  viewModelProps?: Record<string, any>;
  schemaDefinitions?: Record<string, any>;
  scope?: Record<string, any>;
}

export const renderEngineProps = {
  schema: {
    type: Object,
    required: true
  },
  components: {
    type: Object,
    default: () => ({})
  },
  scope: {
    type: Object,
    default: () => ({})
  },
  pageAttrs: {
    type: Object,
    default: () => ({})
  },
  viewModelProps: Object,
  schemaDefinitions: Object,
  schemaKey: [String, Number],
  readOnly: {
    type: Boolean,
    default: undefined
  },
  layoutProps: {
    type: Object,
    default: () => ({})
  },
  scrollToFirstError: {
    type: Boolean,
    default: true
  }
}
