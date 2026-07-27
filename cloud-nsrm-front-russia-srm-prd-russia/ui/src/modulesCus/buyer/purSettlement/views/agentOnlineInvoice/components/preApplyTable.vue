<template>
  <SrmDialog
    size="xLarge"
    title="选择预付款申请明细"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
    class="source-order-wrapper"
  >
    <!-- <FormWrapper :form-array="preArr" @getFormData="getQuerydata" /> -->
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :page-size="15"
      :adeptMeiQl="true"
      :checkbox="true"
      :checkChange="checkChange"
      :comActive="$attrs['changeTab']"
      url="/api-sup-ce/api-ql/AdvanceApply/query"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'

export default {
  name: 'PreApplyTable',
  components: {
    TableView,
    FormWrapper
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    preApplyParams: {
      type: Object,
      default: () => {}
    }
  },

  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      queryParam: {},
      preArr: [],
      tableData: [],
      tableHeader: [
        {
          prop: 'advanceApplyNumber',
          label: this.$t('purSettlementMod.advanceApplyNum'),
          minWidth: 120
        },
        {
          prop: 'creationDate',
          label: this.$t('purSettlementMod.appliedDate'),
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : null,
          minWidth: 120
        },
        {
          prop: 'includeTaxAmount',
          label: this.$t('accountMod.advancePaymentAmount'),
          minWidth: 120
        },
        {
          prop: 'unWrittenOffAmount',
          label: this.$t('purSettlementMod.unWrittenOffAmount2'),
          minWidth: 120
        },
        {
          prop: 'currencyName',
          label: this.$t('quota.currency'),
          minWidth: 120
        },
        {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRate2'),
          minWidth: 120
        },
        {
          prop: 'createdFullName',
          label: this.$t('purSettlementMod.prepaymentCreator'),
          minWidth: 120
        }
      ],
      selection: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    getQuerydata (v) {
      // 默认查出所选 业务实体 + 供应商 + 币种
      let params = {
        orgId: { eq: this.preApplyParams.orgId },
        vendorId: { eq: this.preApplyParams.vendorId },
        currencyCode: { eq: this.preApplyParams.currencyCode }
      }

      this.queryParam = {
        type: 'AdvanceApply',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (selected) {
      this.selection = selected
    },
    handleConfirm () {
      if (this.selection.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      let selection = this.selection.map(item => {
        return { ...item, curWrittenOffAmount: item.unWrittenOffAmount }
      })
      this.$emit('after-confirm', selection)
      this.dialogVisible = false
    }
  }
}
</script>
<style>
.source-order-wrapper .the_TableView .table-wrapper {
  display: block !important;
  flex: none;
}
</style>
