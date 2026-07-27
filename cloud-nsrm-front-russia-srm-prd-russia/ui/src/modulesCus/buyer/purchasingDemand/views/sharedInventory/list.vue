<template>
  <el-container
    class="flex-container drawingshead_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #drawingsVersion="{scope}">
          <el-input v-model="scope.drawingsVersion" v-input-format="{type:'number',negative:false,zero:false}" />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- code="generate:drawingshead:add" -->
          <AuthorityButton
            type="primary"
            code="shared:add"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="shared:delete"
            @click="deleteHandle"
          >
            删除
          </AuthorityButton>
          <MImport
            ref="import"
            :title="iModal.title"
            code="shared:import"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="true"
        :adept-mei-ql="true"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-sup-ce/api-ql/PrShareStock/query"
        :open-custom-table="true"
      ></TableView>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { drawingsHeadApi } from 'modb@/basicSetting/api/basicSetting'
import { transformMQL } from '@/library/utils/util'
import { sharedINV } from 'modc@/buyer/purchasingDemand/api'

export default {
  name: 'DrawingsheadList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      sourceQueryParams: {},
      drawingsHeadApi: drawingsHeadApi,
      dictCodes: {
        drawingsType: 'DRAWING_TYPE',
        isLatest: 'DRAWING_IS_LATEST',
        drawingsStatus: 'DRAWINGS_STATUS'
      },
      extraData: {
        fileModular: 'cm',
        fileFunction: 'gradingRules',
        fileType: 'excel'
      },
      name: 'drawingsheadList',
      tableName: 'drawingsheadTable',
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images', // 文件所属类型
        fileMaxSize: 10 * 1024 * 1024
      },
      iModal: {
        title: '导入', // Excel导入
        upLoadUrl: '/api-sup-ce/prShareStock/import'
      },
      currentRows: [],
      tableHeader: [],
      tableData: [],
      filterConfig: [
        { prop: 'materialCode', label: '物料编码' },
        { prop: 'materialName', label: '物料名称' },
        { prop: 'model', label: '规格型号' },
        { prop: 'company', label: '公司名称' },
        { prop: 'storeName', label: '库位' },
      ],
      expLoading: false,
      queryParam: {},
      queryObj: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.tableHeader = [
      {
        prop: 'materialCode',
        label: '物料编码',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'materialName',
        label: '物料名称',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'model',
        label: '规格型号',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'unit',
        label: '计量单位',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'qty',
        label: '数量',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'company',
        label: '公司名称',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'price',
        label: '参考单价',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'org',
        label: '业务实体',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'storeName',
        label: '库位',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'storeAddress',
        label: '库房地址',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'storeContacts',
        label: '库房联系人',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'area',
        label: '区域',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'createdUserName',
        label: '创建人',
        width: 120
      },
      {
        prop: 'department',
        label: '创建单位',
        width: 120,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: (row, socpe) => this.saveHandle(row, socpe),
            show: row => row.editable,
            // code: 'generate:drawingshead:add',
            formattor: () => {
              return '提交'
            }
          },
          {
            callback: (row, socpe) => this.cancelEdit(row, socpe),
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.cancel')
            }
          },
          {
            callback: (row, socpe) => this.editHandle(row, socpe),
            show: row => !row.editable,
            // code: 'generate:drawingshead:add',
            formattor: () => {
              return '编辑'
            }
          },
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.queryObj = values
    },
    getQuerydata (params) {
      this.sourceQueryParams = params
      const { ...rest } = params || this.queryParam
      this.queryParam = transformMQL.listPageData({
        type:'PrShareStock',
        action: 'query',
        params:{ ...rest },
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getMaterial (value, scope) {
      scope.row.materialId = value.materialId
      scope.row.materialCode = value.materialCode
      scope.row.materialName = value.materialName
      this.$refs[this.gridId].setTableData(tableData => {
        this.$set(tableData, scope.$index, scope.row)
      })
    },
    deleteHandle () {
      const currentRows = this.currentRows
      if (currentRows.length === 0) {
        this.$message.warning('至少选择一行单据进行删除')
        return false
      } else if (currentRows.length > 0) {
        let attr = []
        currentRows.forEach(datas => {
          attr.push(datas.stockId)
        })
        const saveData = transformMQL.save(
          'PrShareStock',
          attr,
          'delete',
        )
        sharedINV.delete(saveData).then(datas => {
          this.$message({
            message: '保存成功',
            type: 'success',
          })
          this.getQuerydata()
        })
      }
    },
    addHandle () {
      const userInfo = this.$store.getters.user.userInfo
      console.log(userInfo)
      this.$refs[this.gridId].addOneEditableColumn({
        createdId: userInfo?.userId,
        createdBy: userInfo?.username,
        createdUserName: userInfo?.nickname,
        editable: true,
        isNew: true
      })
    },
    saveHandle (row) {
      const saveData = transformMQL.save(
        'PrShareStock',
        [row],
        'save',
      )
      sharedINV.save(saveData).then(datas => {
        this.$message({
          message: '保存成功',
          type: 'success',
        })
        this.getQuerydata(this.sourceQueryParams)
      })
    },
    editHandle (row, scope) {
      this.$set(scope.row, 'editable', true)
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    activeHandel () {
      if (this.currentRows.length === 0) {
        this.$message.warning(this.$t('vendorMod.pleaseCheckAtLeastOnePieceOfData'))
        return
      }
      let rows = { drawingsIds: [], status: 'Y' }
      this.currentRows.forEach(item => {
        rows.drawingsIds.push(item.drawingsId)
      })
      drawingsHeadApi.updateStatus(rows).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    inactiveHandel () {
      if (this.currentRows.length === 0) {
        this.$message.warning(this.$t('vendorMod.pleaseCheckAtLeastOnePieceOfData'))
        return
      }
      let rows = { drawingsIds: [], status: 'N' }
      this.currentRows.forEach(item => {
        rows.drawingsIds.push(item.drawingsId)
      })
      drawingsHeadApi.updateStatus(rows).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', fileExtendType = '' } = file || {}
      this.$set(row, 'fileuploadId', fileId)
      this.$set(row, 'attachName', fileName)
      this.$set(row, 'attachType', fileExtendType)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/prShareStock/downloadTemplate',
        '导入模板'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>
