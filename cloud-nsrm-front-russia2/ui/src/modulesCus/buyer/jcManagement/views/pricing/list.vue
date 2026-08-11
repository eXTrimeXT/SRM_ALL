<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="100px"
        @getFormData="getQueryData"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            @click="edit('add')"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="inq:jcPrice:exprotDetail"
            @click="exprotDetail"
          >
            {{ $t('cusEntry.common.exprotDetail') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="true"
        :url="tableUrl"
        :checkbox="true"
        :open-custom-table="true"
        :reserve-selection="true"
        :check-change="checkChange"
        row-key="orderNumber"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import detail from './detail'
import priceApi from 'modcb@/jcManagement/api'
import { downloadFileLink } from 'lib@/utils/file'
export default {
  name: 'JcPricingList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      exprotList: [],
      integrationMode: '',
      gridId: 'list',
      currentRows: null,
      tableUrl: priceApi.price.pageList,
      pageSize: 15,
      preArr: [
        {
          prop: 'designProjectCode',
          label: this.$t('bidMod.bidingNum')  // '项目编号'
        },
        {
          prop: 'designProjectName',
          label: this.$t('bidMod.bidingName')  // '项目名称'
        },
        {
          prop: 'fixPriceStatus',
          label: this.$t('vendorMod.approvalStatus'),  // '审核状态'
          type: 'dict',
          code: 'EXT_FIX_PRICE_STATUS'
        },
        {
          prop: 'createdBy',
          label: this.$t('common.creator')   // '创建人'
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),
          type: 'daterange'
        }
      ],
      tableHeader: [
        {
          prop: 'designProjectCode',
          label: this.$t('bidMod.bidingNum'),  // '项目编号'
          width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.edit('view', row)
        },
        {
          prop: 'designProjectName',
          label: this.$t('bidMod.bidingName'),  // '项目名称'
          width: 120
        },
        {
          prop: 'approvalSubmitTime',
          label: this.$t('cusEntry.centralizedPurchase.priceTime'),  // '定价时间'
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'createdFullName',
          label: this.$t('cusEntry.centralizedPurchase.projectLeader'),
          minWidth: 120
        },
        {
          prop: 'fixPriceStatus',
          label: this.$t('components.stratProcess.headers.docStatusValue'),  // '状态'
          dataType: 'dict',
          code: 'EXT_FIX_PRICE_STATUS',
          width: 120
        },
        {
          prop: 'executeTimeFrom',
          label: this.$t('monitorBizConfig.executeStartTime'),  // '执行开始时间'
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'executeTimeTo',
          label: this.$t('monitorBizConfig.executeEndTime'),  // '执行结束时间'
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'createUserOrgOuName',
          label: this.$t('cusEntry.centralizedPurchase.createCompany'),  // '创建单位'
          width: 120
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.edit('edit', row),
              formattor: () => this.$t('common.edit'),
              show: row => row.fixPriceStatus === 'DRAFT'
            },
            {
              callback: row => this.delete(row.purFixPriceHeadId),
              formattor: () => this.$t('common.delete'),
              show: row => row.fixPriceStatus === 'DRAFT'
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  methods: {
    // 勾选导出数据
    checkChange (value) {
      this.exprotList = value
    },
    // 导出明细
    exprotDetail () {
      if (this.exprotList.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectExportData'))
        return false
      }
      if (this.exprotList.length > 1) {
        this.$message.warning(this.$t('cusEntry.tipMessage.oneSelectExportData'))
        return false
      }
      const purFixPriceHeadId = this.exprotList[0].purFixPriceHeadId
      downloadFileLink(
        `/api-sou/npm/pur_fix_price/buyer/downloadExcel/${purFixPriceHeadId}`,
        this.$t('cusEntry.inq.priceOrderDetail'),
        { ...this.queryParams, fixPriceIds: this.exprotList.map(item => item.fixPriceHeadId) }
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 列表查询 */
    getQueryData (params = {}) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    /* 新增、编辑、查看 */
    edit (type, row = {}) {
      const name = type === 'add' ? 'priceOrderDetail' : `priceOrderDetail${row.souNo}`
      const title = type === 'add' ? this.$t('cusEntry.centralizedPurchase.addPriceOrder') : row.souNo
      this.$emit('tab-add', {
        name,
        component: detail,
        params: {
          type,
          tabName: name,
          row,
          readOnly: !['add', 'edit'].includes(type)
        },
        title
      })
    },
    /* 删除 */
    delete (id) {
      priceApi.price.delete(id).then(res => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData(this.queryParam)
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-input-group__append >div{
    display: flex;
    justify-content: center;
}
</style>
