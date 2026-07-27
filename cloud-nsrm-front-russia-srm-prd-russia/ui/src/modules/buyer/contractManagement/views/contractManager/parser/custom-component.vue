<template>
  <component
    :is="namespace"
    :component-info="componentInfo"
    :editable="editable"
    :context="context"
    :value="value"
    style="text-indent: 0;"
    @change="change"
  />
</template>
<script>
import ComponentMap from 'modb@/contractManagement/views/contractManager/parser/enum'
import fixedElem from 'modb@/contractManagement/views/contractModeManager/fixedElem'
import ZReference from './components/z-reference'
import ZText from './components/z-text'
import ZInput from './components/z-input'
import ZDate from './components/z-date'
import ZTable from './components/z-table'
import ZSelect from './components/z-select'
import ZTip from './components/z-tip'
import ZMaterialTable from './components/z-material-table'
import ZImage from './components/z-image'
import ZCheckbox from './components/z-checkbox'
import getComponentInfo from './getComponentInfo'

export default {
  name: 'CustomComponent',
  components: {
    ZReference,
    ZText,
    ZInput,
    ZDate,
    ZTable,
    ZSelect,
    ZTip,
    ZMaterialTable,
    ZImage,
    ZCheckbox
  },
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    elemKey: {},
    code: {
      required: true
    },
    context: {
      required: true
    },
    editable: {
      required: true,
      type: Boolean,
      default: false
    },
    value: {
      required: true
    },
    initialize: {
      type: Boolean
    }
  },
  data () {
    return {
      componentInfo: {},
      namespace: null
    }
  },
  destroyed () {
    this.namespace = null
  },
  mounted () {
    // 获取组件信息
    if (this.code.indexOf('$') > -1) {
      const target = fixedElem.find(i => i.elemCode === this.code)
      this.componentInfo = { ...target, elemKey: this.elemKey }
      this.namespace = ComponentMap[target.addMethod]
    } else {
      getComponentInfo(this.code).then(res => {
        const info = res.data.list[0]
        if (!info) {
          this.componentInfo = {}
          this.namespace = null
          console.warn(`code : ${this.code}, message: info is empty`)
        } else {
          this.componentInfo = { ...info, elemKey: this.elemKey }
          this.namespace = ComponentMap[info.addMethod]
          if (this.initialize) {
            if (/table/gi.test(this.namespace)) {
              this.change([])
            } else {
              this.change(info.initValue)
            }
          }
        }
      })
    }
  },
  methods: {
    change (value) {
      this.$emit('change', value)
    }
  }
}
</script>
