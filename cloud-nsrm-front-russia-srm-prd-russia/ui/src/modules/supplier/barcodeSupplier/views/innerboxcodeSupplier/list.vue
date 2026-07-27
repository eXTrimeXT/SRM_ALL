<template>
  <el-container class="flex-container innerboxcode_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="filterConfig" @getFormData="getQuerydata" />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 多选删除 -->
          <AuthorityButton type="primary" @click="batchDelete">
            {{ $t("common.delete") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="checkChangeChange"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/innerboxcode/listPage"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import innerboxcodeEdit from './edit.vue'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { innerBoxCodeApi } from 'mods@/barcodeSupplier/api'

export default {
  name: 'InnerboxcodeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile,
    CDownloadLink
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      currentRows: [],
      tableHeader: [
        {
          prop: 'innerBoxCode',
          label: '内箱编码',
          width: 120
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          width: 120
        },
        {
          prop: 'vendorCode',
          label: '供应商编码',
          width: 120
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          width: 100
        },
        {
          prop: 'materialName',
          label: '物料名称',
          width: 100
        },
        {
          prop: 'categoryName',
          label: '品类名称',
          width: 100
        },
        // {
        //   prop: "printCount",
        //   label: "打印次数",
        //   width: 100,
        // },
        {
          prop: 'productionDate',
          label: '生产日期',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          width: 120
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '更新人',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            // {
            //   callback: row => this.editHandle(row),
            //   formattor: () => {
            //     return this.$t('common.edit')
            //   }
            // },
            {
              callback: row => this.viewHandle(row),
              formattor: () => {
                return this.$t('common.view')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'vendorCode', label: '供应商编号' },
        { prop: 'materialCode', label: '物料编码' },
        { prop: 'innerBoxCode', label: '内箱编码' }
      ],
      queryParam: {},
      dictHeaderExportParam: []
    }
  },
  created () {
    console.log(innerBoxCodeApi,'innerBoxCodeApi')
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {

    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          innerBoxCodeApi.delete(row.innerBoxId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => { })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        ctrlHeight: true,
        title: '内箱条码新增',
        name: 'innerboxcodeEdit'
      }
      this.$emit('tab-add', tab)
    },
    viewHandle (row) {
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: 'view'
        },
        ctrlHeight: true,
        title: '内箱条码查看',
        name: 'innerboxcodeEdit' + row.innerBoxId
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: innerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        ctrlHeight: true,
        title: '内箱条码编辑',
        name: 'innerboxcodeEdit' + row.innerBoxId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 多选删除
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.dictHeaderExportParam = rowArr.map(i => (i.innerBoxId))
    },
    // 批量删除
    batchDelete () {
      let idArr = this.dictHeaderExportParam
      if (idArr.length === 0) return this.$message.error('请先勾选内箱条码！！！')
      this.$confirm('当前操作将永久删除数据，确认删除数据？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/base/innerboxcode/bathDelete',
            method: 'POST',
            data: idArr,
            loading: true
          })
            .then(data => {
              this.$message.success('操作成功')
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => { })
    }

  }
}
</script>
