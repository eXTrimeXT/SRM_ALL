export default {
  name: 'CQueryFormButton',
  props: {
    showExpand: {
      type: Boolean,
      default: true
    },
    showQueryBtn: {
      type: Boolean,
      default: true
    },
    showResetBtn: {
      type: Boolean,
      default: true
    },
    expand: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleClick (eventName) {
      this.$emit(eventName)
    }
  },
  render (h) {
    let queryBtn = ''
    let resetBtn = ''
    let expandBtn = ''

    if (this.showQueryBtn) {
      queryBtn = (
        <ElButton
          type="primary"
          onClick={() => this.handleClick('query')}
        >
          {/* 查询 */}
          { this.$t('common.search')}
        </ElButton>
      )
    }

    if (this.showResetBtn) {
      resetBtn = <ElButton onClick={() => this.handleClick('reset')}>
        {/* 重置 */}
        {this.$t('common.reset')}
      </ElButton>
    }

    if (this.showExpand) {
      expandBtn = (
        <ElButton
          type="text"
          onClick={() => this.handleClick('expand')}
        >
          { this.expand ? this.$t('bidMod.collapseForm') : this.$t('bidMod.expandForm')}
        </ElButton>
      )
    }

    return (
      <div class="c-query-form-btn">
        { queryBtn}
        { resetBtn}
        { expandBtn}
      </div>
    )
  }
}
