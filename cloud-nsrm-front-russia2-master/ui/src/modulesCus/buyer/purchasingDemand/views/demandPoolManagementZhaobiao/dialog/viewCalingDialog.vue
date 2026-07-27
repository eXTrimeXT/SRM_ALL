<template>
  <!-- 查看定标信息 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.calibrationInfo')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.wonBidVendor')"
        minWidth="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="linkman"
        :label="$t('vendorMod.vendorContact')"
        minWidth="150"
        show-overflow-tooltip
      />
      <!-- 供应商联系人电话 -->
      <el-table-column
        prop="phone"
        :label="$t('cusEntry.orderMod.extVendorPhone')"
        minWidth="150"
        show-overflow-tooltip
      />
      <!-- 定标金额 -->
      <el-table-column
        prop="bidAmountByTenKilo"
        :label="$t('cusEntry.supplement20250121.bidAmountByTenKilo')"
        minWidth="150"
        show-overflow-tooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import { extPrSouRequirementApi } from 'modc@/buyer/purchasingDemand/api'

export default {
  name: 'InvoiceDialog',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      tableData: []
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
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getFormDetail()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    viewDepositNo (row) {

    },
    async getFormDetail () {
      this.tableData = []
      let requirementHeadId = this.editRows.requirementHeadId
      let transfromParams = transformMQL.save('ExtPrSouRequirementBidResult', {
        filter: {
          requirementHeadId: {
            eq: requirementHeadId
          }
        }
      }, 'query')
      const response = await extPrSouRequirementApi.query(transfromParams)
      if (response?.data?.records.length) {
        this.tableData = response.data.records
      }
    }
  }
}
</script>
