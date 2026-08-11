<template>
<!-- 库存详情 -->
  <SrmDialog
    :title="$t('outsource.stockDetails')"
    size="large"
    :visible.sync="dialogVisible"
    append-to-body
    destroy-on-close
    :before-close="close"
  >
    <TableView
      ref="invDetailTableInfo"
      :table-data="tableData"
      :table-header="tableHeader"
      table-max-height="400px"
      :pre-query-data="queryParams"
      :adeptMeiQl="true"
      :open-custom-table="false"
      style="height: 400px"
      :url="tableViewUrl"
    >
      <template #amountSlot="{ scope }">
        <template v-if="scope.row.amount >= 0">
          <span style="color: red;">
            <strong>{{ scope.row.amount }}</strong>
          </span>
        </template>
        <template v-else>
          <span style="color: green;">
            <strong>{{ scope.row.amount }}</strong>
          </span>
        </template>
      </template>
    </TableView>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'InvDetailDialog',
  components: {
    TableView
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    row: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      tableViewUrl: '/api-sup-ce/api-ql/OsRealTimeInvRecordBuyer/query',
      tableData: [],
      tableHeader: [
        {
          label: this.$t('dataConfMod.businessType'),  // '业务类型'
          prop: 'businessType',
          width: 110,
          dataType: 'dict',
          code: 'OS_VENDOR_INV_RECORD_BUSINESS_TYPE'
        },
        {
          label: this.$t('outsource.businessNumber'),  // '业务单号'
          prop: 'businessNo',
          minWidth: 130
        },
        {
          label: this.$t('outsource.changeQuantity'),  // '变动数量'
          prop: 'amount',
          width: 100,
          showType: 'slot',
          slot: 'amountSlot'
        },
        {
          label: this.$t('outsource.supplierInventory'),  // '供方库存'
          prop: 'vendorInvAmount',
          width: 100
        },
        {
          label: this.$t('outsource.executionTime'),  // '执行时间'
          prop: 'creationDate',
          width: 150,
          dataType: 'dateTime'
        }
      ],
      queryParams: {}
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
  watch: {
    visible (nVal) {
      if (nVal) {
        this.getQuerydata()
      }
    }
  },
  methods: {
    getQuerydata (params = {}) {
      const { organizationId, baseMaterialId, vendorId, invId, creationDate } = this.row
      this.queryParams = transformMQL.listPageData({
        type: 'OsRealTimeInvRecordBuyer',
        action: 'query',
        params: {
          ...params,
          organizationId,
          baseMaterialId,
          vendorId,
          invId,
          creationDate // 创建时间小于等于当前行的创建时间
        },
        filterOperator: {
          creationDate: 'le'
        }
      })

      this.$nextTick(() => {
        this.$refs.invDetailTableInfo.query()
      })
    },
    close () {
      this.$emit('close')
    }
  }
}
</script>
