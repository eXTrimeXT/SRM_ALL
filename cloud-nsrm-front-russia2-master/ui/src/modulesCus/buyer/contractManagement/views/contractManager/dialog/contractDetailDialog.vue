<template>
<!-- 查看邀请历史 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.viewInvitationHistory')"
    size="large"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :before-close="close"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <FormWrapper :colLength="3" :form-array="searchFormConfig" @getFormData="getQueryData" />
    <TableView
      ref="list"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :checkbox="true"
      :checkChange="handleCurrentChange"
      :com-active="$attrs['changeTab']"
      :open-custom-tabl="false"
      :url="tableViewUrl"
      :adeptMeiQl="true"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">
        <!-- 取消 -->
        {{ $t("components.common.cancel") }}
      </el-button>
      <el-button type="primary" @click="confirm">
        <!-- 确定 -->
       {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import inviteHttp from 'modcb@/inviteManagement/api'
import { uniqBy } from 'lodash'

export default {
  name: 'InviteHistoryDialog',
  components: {
    FormWrapper,
    TableView
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    mode: { // 用于标识集采合同：collect，临采合同：temp
      type: String,
      default: 'collect'
    }
  },
  data () {
    return {
      searchFormConfig: [
        {
          prop: 'projectName',
          // 定价单号
          label: this.$t('cusEntry.inq.priceOrderNo')
        },
        {
          prop: 'buId',
          // 公司
          label: this.$t('components.organization.COMPANY'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          // 供应商
          label: this.$t('common.vendor'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'itemCode',
          // 物料编码
          label: $t('common.materialCode')
        }
      ],
      tableData: [],
      tableHeader: [],
      queryParam: {},
      tableViewUrl: inviteHttp.hisListPageUrl,
      selectedRows: []
    }
  },
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getQueryData()
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'projectName',
        // 定价单号
        label: $t('cusEntry.inq.priceOrderNo')
      },
      {
        prop: 'buName',
        // 公司
        label: $t('components.organization.COMPANY')
      },
      {
        prop: 'vendorName',
        // 供应商
        label: $t('common.vendor')
      },
      {
        prop: 'itemCode',
        // 物料编码
        label: $t('common.materialCode')
      },
      {
        prop: 'itemName',
        // 物料名称
        label: $t('common.materialName')
      },
      {
        prop: 'noTaxPrice',
        // 未税单价
        label: $t('bid_mod.untaxedPrice')
      },
      {
        prop: 'quality',
        // 数量
        label: $t('bid_mod.quantity')
      },
      {
        prop: 'noTaxTotalPrice',
        // 未税总价
        label: $t('competition.orderNotaxTotalPrice')
      }
    ]
  },
  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'SouInviteHistoryBuyer',
        action: 'query',
        params
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },
    close () {
      this.$emit('close')
    },
    confirm () {
      // 请勾选列表
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      if (uniqBy(this.selectedRows, 'buName').length > 1) {
        // 同一个公司才可以一起创建
        return this.$message.warning(this.$t('cusEntry.supplement20250205.sameCompanyCanCreateTogether'))
      }
      console.log('####', uniqBy(this.selectedRows, 'vendorName'))
      if (uniqBy(this.selectedRows, 'vendorName').length > 1) {
        // 同一个供应商才可以一起创建
        return this.$message.warning($t('cusEntry.supplement20250205.sameSupplierCanCreateTogether'))
      }
      this.$emit('confirm', this.selectedRows)
    }
  }
}
</script>
