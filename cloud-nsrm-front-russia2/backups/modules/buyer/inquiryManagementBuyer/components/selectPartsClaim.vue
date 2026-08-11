<template>
  <srm-dialog
    :title="title"
    :size="size"
    :visible.sync="visible"
  >
    <FormWrapper
      :formArray="filterConfig"
      @getFormData="getQuerydata"
    />
    <BaseTable
      ref="table"
      :columns="columns"
      :dataSource="dataSource"
      :initialize="false"
      row-key="partsClaimId"
      :selection="true"
      border
      @selection-change="selectionChangeHandler"
      @row-dblclick="rowDbclick"
    />
    <el-row type="flex">
      <el-col>
        <CPagination
          ref="queryPagination"
          style="margin: 5px"
          class="c-query-table-pagination"
          :total="queryTotal"
          :page-num="pageNum"
          :page-size="pageSize"
          @current-change="changeCurrentIndex"
          @size-change="changeCurrentSize"
        />
      </el-col>
    </el-row>
    <template #footer class="dialog-footer">
      <el-button
        type="primary"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
      <el-button @click="cancel">
        {{ $t('common.cancel') }}
      </el-button>
    </template>
  </srm-dialog>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import createDictionary from 'lib@/utils/ponyStore'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import CPagination from 'lib@/components/c-pagination'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import { EDITABLE_KEY } from '@/library/components/BaseTable/utils'

const { store, mutation, getLabel, renderSelect } = createDictionary({
  PRICE_TYPE: []
})
const RenderSelect = renderSelect()

export default {
  name: 'SelectPartsClaim',
  components: {
    FormWrapper,
    RenderSelect,
    CUploadFile,
    CDownloadLink,
    CPagination,
    BaseTable
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  props: {
    title: {
      type: String,
      default: '选择零件'
    },
    visible: {
      type: Boolean,
      default: false
    },
    size: {
      type: String,
      default: 'large'
    },
    headData: {
      type: Object,
      required: true
    }
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableData: [],
      gridId: 'list',
      currentRows: [],
      dataSource: [],
      queryTotal: -1,
      pageSize: 10,
      pageNum: 1,
      filterConfig: [
        {
          prop: 'materialCode',
          label: '零件编号'
        },
        {
          prop: 'materialName',
          label: '零件名称'
        },
        /* {
          prop: "vendorCode",
          label: "供应商编码",
          type: "quicksearch",
          showKey: "companyCode",
          name: "scc_sup_company_info"
        }, */
        { prop: 'orgId', label: '需求工厂', type: 'OUorganizationSelector' },
        { prop: 'delModels', label: '车型' }
      ],
      queryParam: {
        pageSize: 10,
        pageNum: 1
      },
      columns: [
        {
          attrs: {
            prop: 'materialCode',
            label: '零件编号'
          }
        },
        {
          attrs: {
            prop: 'materialName',
            label: '零件名称'
          }
        },
        {
          attrs: {
            prop: 'delModels',
            label: '车型'
          }
        },
        {
          attrs: {
            prop: 'orgName',
            label: '工厂名称'
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '供应商名称'
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            label: '供应商编码'
          }
        },
        {
          attrs: {
            prop: 'categoryFullName',
            label: '零件品类'
          }
        },
        {
          attrs: {
            prop: 'priceType',
            label: '价格类型',
            formatter: (value) => getLabel('PRICE_TYPE', value)
          }
        }
      ]
    }
  },
  created () {
    mutation.loadDictionary(['PRICE_TYPE'])
  },
  methods: {
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$emit('receiveData', this.currentRows)
      this.visible = false
    },
    async getQuerydata (params) {
      if (params) {
        this.pageNum = 1
      }
      this.queryParam.pageNum = this.pageNum
      this.queryParam.pageSize = this.pageSize
      this.queryParam = params || this.queryParam
      this.queryParam.vendorId = this.headData.gacmVendorId
      this.queryParam.headPriceType = this.headData.gacmPriceType
      this.queryParam.headPriceAttribute = this.headData.gacmPriceAttribute
      // this.$http({
      //   // url: "/cloud-srm/api-inq/inq/partsclaim/listPage",
      //   url: "/cloud-srm/api-inq/price/approval/queryParts",
      //   method: "POST",
      //   data: this.queryParam,
      //   laoding: true
      // }).then(data => {
      //   if (data && data.data) {
      //     this.dataSource = data.data.list;
      //     this.queryTotal = data.data.total;

      //   }
      // });
      let data = await this.$http({
        url: '/cloud-srm/api-inq/price/approval/queryParts',
        method: 'POST',
        data: this.queryParam,
        laoding: true
      })

      if (data && data.data) {
          this.dataSource = data.data.list
          this.queryTotal = data.data.total
        }
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.pageNum = currentNum
      this.getQuerydata()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.pageSize = currentSize
      this.getQuerydata()
    },
    rowDbclick (row, column, event) {
      if (this.readOnly) {
        row[EDITABLE_KEY] = false
      }
    },
    /* 勾选数据 */
    selectionChangeHandler (data) {
      this.currentRows = data
    }
  }
}
</script>
