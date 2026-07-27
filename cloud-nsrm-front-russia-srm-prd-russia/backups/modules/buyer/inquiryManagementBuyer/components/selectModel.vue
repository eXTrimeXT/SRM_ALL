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
    <!--   :dataSource="dataSource" -->
    <BaseTable
      ref="table"
      :noRule="true"
      :columns="columns"
      :dataSource="dataSource"
      :initialize="false"
      row-key="modelCode"
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
    <template
      #footer
      class="dialog-footer"
    >
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
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import CPagination from 'lib@/components/c-pagination'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import { EDITABLE_KEY } from '@/library/components/BaseTable/utils'

export default {
  name: 'SelectModel',
  components: {
    FormWrapper,
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
      required: false
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
        { prop: 'orgId', label: '需求工厂', type: 'OUorganizationSelector' },
        {
          prop: 'modelCode',
          label: '车型代码'
        },
        {
          prop: 'modelName',
          label: '车型名称'
        }
      ],
      queryParam: {
        pageSize: 10,
        pageNum: 1
      },
      columns: [
        {
          attrs: {
            prop: 'orgName',
            label: '工厂名称'
          }
        },
        {
          attrs: {
            prop: 'orgCode',
            label: '工厂编码'
          }
        },
        {
          attrs: {
            prop: 'modelCode',
            label: '车型代码'
          }
        },
        {
          attrs: {
            prop: 'modelName',
            label: '车型名称'
          }
        }
      ]
    }
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
      console.log('params:::', params)
      if (params) {
        this.pageNum = 1
      }
      this.queryParam = params || {}
      this.queryParam.pageNum = this.pageNum
      this.queryParam.pageSize = this.pageSize
      // let data = await this.$http({
      //   url: "/cloud-srm/api-inq/price/approval/listSalePlanModel",
      //   method: "POST",
      //   data: this.queryParam,
      // })
      // if (data && data.data) {
      //   this.dataSource = data.data.list;
      //   this.queryTotal = data.data.total;
      // }

      this.$http({
        url: '/cloud-srm/api-inq/price/approval/listSalePlanModel',
        method: 'POST',
        data: this.queryParam
      })
        .then((res) => {
          if (res && res.data) {
            this.dataSource = res.data.list
            this.queryTotal = res.data.total
          }
        })
        .catch((error) => {
          console.error('error', error)
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
