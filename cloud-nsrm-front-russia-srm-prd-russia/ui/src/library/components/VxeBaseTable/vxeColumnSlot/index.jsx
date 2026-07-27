import CInput from './cInput'
import CInputNumber from './cInputNumber'
import CSwitch from './cSwitch'
import CSelect from './cSelect'
import CDatePicker from './cDatePicker'

export default {
  inheritAttrs: false,
  props: {
    type: {
      type: String
    }
  },
  // eslint-disable-next-line vue/require-render-return
  render (h) {
    switch (this.type) {
    case 'input':
      return <CInput {...{ on: this.$listeners }} {...{ attrs: this.$attrs }} />
    case 'inputNumber':
      return <CInputNumber {...{ on: this.$listeners }} {...{ attrs: this.$attrs }} />
    case 'switch':
      return <CSwitch {...{ on: this.$listeners }} {...{ attrs: this.$attrs }} />
    case 'select':
      return <CSelect {...{ on: this.$listeners }} {...{ attrs: this.$attrs }} />
    case 'date':
      return <CDatePicker {...{ on: this.$listeners }} {...{ attrs: this.$attrs }} />
    }
  }
}
