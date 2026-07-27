<template>
  <SrmDialog
    size="large"
    :title="$t(`quoteTemplate.condition.${readonly ? 'view' : 'edit'}`)"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
  >
    <Condition
      ref="condition"
      :readonly="readonly"
      :attribute-list="availableAttributeNameList"
      :detail-data="detailData"
      :condition-title="$t('quoteTemplate.condition.label')"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <!--确定-->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 应用条件
 */
import { fieldTypeIsDate, fieldTypeIsFormula } from 'lib@/composition/quoteTemplate/utils'
import Condition from 'lib@/composition/quoteTemplate/condition.vue'

export default {
  name: 'ApplicationConditionDialog',

  components: { Condition },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    attrFieldData: {
      type: Array,
      required: true
    },
    detailData: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (value) {
        this.$emit('update:visible', value)
      }
    },

    // 可选的字段属性列表
    availableAttributeNameList () {
      return this.attrFieldData.filter(item => {
        return item.fieldName &&
          // 排除公式类型
          !fieldTypeIsFormula(item.fieldType) &&
          // 排除日期类型
          !fieldTypeIsDate(item.fieldType)
      })
    }
  },

  methods: {
    /* 确定 */
    async confirm () {
      const resultData = await this.$refs.condition.validateData()

      if (!resultData.status) {
        return
      }

      this.$emit('confirm', resultData.data)
      this.dialogVisible = false
    }
  }
}
</script>
