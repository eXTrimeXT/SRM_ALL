export const ModelInfo = {
  props: ['value', 'componentData'],
  render: function (createElement) {
    let self = this
    let elementProps = this.componentData
    if (typeof this.componentData === 'function') {
      elementProps = this.componentData(self)
      self.value = elementProps.defaultValue
    }
    const childrenElement = []
    if (elementProps.slots) {
      childrenElement.push(...elementProps.slots)
    }
    if (elementProps.children) {
      for (let i = 0; i < elementProps.children.length; i++) {
        const childItem = elementProps.children[i]
        childrenElement.push(
          createElement(childItem.tag, {
            props: {
              componentData: childItem.componentData
            }
          })
        )
      }
    }
    return createElement(elementProps.tag, elementProps, childrenElement)
  }
}

export default ModelInfo
