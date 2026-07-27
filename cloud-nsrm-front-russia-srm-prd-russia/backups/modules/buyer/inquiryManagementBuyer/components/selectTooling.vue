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
      ref="toolingTable"
      :columns="columns"
      :dataSource="dataSource"
      :initialize="false"
      row-key="toolingId"
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

const { store, mutation, getLabel, renderSelect } = createDictionary({})
const RenderSelect = renderSelect()

export default {
  name: 'SelectTooling',
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
      default: '选择工装'
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
        { prop: 'toolingName', label: '工装' }
      ],
      queryParam: {
        pageSize: 10,
        pageNum: 1
      },
      columns: [
        {
          attrs: {
            prop: 'toolingCode',
            label: '系统工装编码'
          }
        },
        {
          attrs: {
            prop: 'toolingName',
            label: '工装名称'
          }
        }
      ]
    }
  },
  created () {},
  methods: {
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$emit('receiveData', this.currentRows)
      this.visible = false
    },
    getQuerydata (params) {
      if (params) {
        this.pageNum = 1
      }
      this.queryParam.pageNum = this.pageNum
      this.queryParam.pageSize = this.pageSize
      this.queryParam = params || this.queryParam
      this.queryParam.vendorId = this.headData.gacmVendorId
      this.queryParam.headPriceType = this.headData.gacmPriceType
      this.queryParam.headPriceAttribute = this.headData.gacmPriceAttribute
      this.$http({
        url: '/cloud-srm/api-inq/price/approval/queryToolings',
        method: 'POST',
        data: this.queryParam,
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.dataSource = data.data.list
          this.queryTotal = data.data.total
        }
      })
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
