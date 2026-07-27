<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <ExportExcel
            v-loading
            page-url="/api-inq/inq/reqhead/souReqInqReportPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('sourcingBuyer.exportData')"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-inq/inq/reqhead/souReqInqReportPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import sourcingApplicationDetail from '../sourcingApplicationBuyer/sourcingApplicationDetail'
import inquiryDetail from '@/modules/buyer/inquiry/views/inquiryManagement/inquiryDetail.vue'
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

export default {
  name: 'QuoteReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    CCategorySelect
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        unit: 'unit'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 业务实体
        {
          prop: 'orgId',
          label: this.$t('sourcingBuyer.orgName'),
          type: 'OUorganizationSelector'
        },
        // 库存组织
        {
          prop: 'organizationId',
          label: this.$t('sourcingBuyer.organizationName'),
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        // 寻源单号
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        },

        // 物料品类
        {
          prop: 'categoryId',
          label: this.$t('sourcingBuyer.categoryType'),
          type: 'catSelect',
          showKey: 'categoryId'
        },
        // 物料编码
        {
          prop: 'materialCode',
          label: this.$t('sourcingBuyer.materialCode'), // 物料编码,
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('sourcingBuyer.vendorName1')
        },
        // 询价单号
        {
          prop: 'inquiryNo',
          label: this.$t('sourcingBuyer.inquiryNo')
        },
        // 发起人
        {
          prop: 'promoter',
          label: this.$t('sourcingBuyer.promoter')

        },
        // 创建时间
        {
          prop: 'dateList',
          label: this.$t('sourcingBuyer.creationDate'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      // 业务实体
      {
        prop: 'orgName',
        label: this.$t('sourcingBuyer.orgName'),
        width: 120
      },
      // 库存组织
      {
        prop: 'organizationName',
        label: this.$t('sourcingBuyer.organizationName'),
        width: 120
      },
      // 寻源单号
      {
        prop: 'reqHeadNo',
        label: this.$t('sourcingBuyer.reqHeadNo'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row)
      },
      // 物料品类
      {
        prop: 'categoryName',
        label: this.$t('sourcingBuyer.categoryType'),
        width: 120
      },
      // 物料编码
      {
        prop: 'materialCode',
        label: this.$t('sourcingBuyer.materialCode'),
        width: 120
      },
      // 物料名称
      {
        prop: 'materialName',
        label: this.$t('sourcingBuyer.categoryName'),
        width: 120
      },
      // 单位
      {
        prop: 'unit',
        label: this.$t('sourcingBuyer.unit'),
        width: 120,
        dataType: 'dict',
        code: 'unit'
      },
      // 预计数量
      {
        prop: 'quantity',
        label: this.$t('sourcingBuyer.quantity'),
        width: 120
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: this.$t('sourcingBuyer.vendorName1')
      },
      // 询价单号
      {
        prop: 'souNo',
        label: this.$t('sourcingBuyer.inquiryNo'),
        width: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readInquiryDetail(row)
      },
      // 目标价（不含税）
      {
        prop: 'notaxTargetPrice',
        label: this.$t('sourcingBuyer.notaxTargetPrice'),
        width: 120
      },
      // 报价币种
      {
        prop: 'currency',
        label: this.$t('sourcingBuyer.currency'),
        width: 120
      },
      // 供应商报价（未税价）
      {
        prop: 'notaxPrice',
        label: this.$t('sourcingBuyer.notaxPrice'),
        width: 120
      },
      // 综合得分
      {
        prop: 'compositeScore',
        label: this.$t('sourcingBuyer.compositeScore'),
        width: 120
      },
      // 排名
      {
        prop: 'ranking',
        label: this.$t('sourcingBuyer.ranking'),
        width: 120
      },
      // 中标结果
      {
        prop: 'selectResult',
        label: this.$t('sourcingBuyer.selectResult'),
        width: 120,
        dataType: 'dict',
        code: 'INQ_SELECT_RESULT'
      },
      // 发起人
      {
        prop: 'promoter',
        label: this.$t('sourcingBuyer.promoter'),
        width: 120
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('sourcingBuyer.creationDate'),
        width: 120
      },
      // 供方报价日期
      {
        prop: 'quoDate',
        label: this.$t('sourcingBuyer.quoDate'),
        width: 120
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (payload) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'dateList', fromProp: 'beginTime', toProp: 'endTime' }
        ])
      }

      this.queryParam = payload || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'view',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.reqHeadNo
        },
        title: row.reqHeadNo,
        name: 'sourcingApplicationDetail' + row.reqHeadNo
      })
    },
    readInquiryDetail (row) {
      this.$emit('tab-add', {
        component: inquiryDetail,
        params: {
          flag: 'view',
          readOnly: true,
          row: row,
          tabName: 'inquiryDetail' + row.souNo
        },
        title: row.souNo,
        name: 'inquiryDetail' + row.souNo
      })
    }
  }
}
</script>
<style lang="scss" scoped></style>
