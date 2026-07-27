<template>
  <el-container
    class="flex-container sitereviewmodel_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t("common.add")
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="siteReviewModel.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import sitereviewmodelEdit from './edit.vue'
import { siteReviewModel } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'SitereviewmodelList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      siteReviewModel: siteReviewModel,
      name: 'sitereviewmodelList',
      tableName: 'sitereviewmodelTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      apStatus: [
        {
          label: '拟定',
          value: 'DRAFT'
        },
        {
          label: '生效',
          value: 'ENABLE'
        },
        {
          label: '失效',
          value: 'UNABLE'
        }
      ],
      tableHeader: [
        {
          prop: 'reviewModelCode',
          label: '模板编码',
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOne(row)
        },
        {
          prop: 'reviewModelName',
          label: '模板名称'
        },
        {
          prop: 'creationDate',
          label: '创建时间'
        },
        {
          prop: 'createdBy',
          label: '创建人'
        },
        {
          prop: 'approveStatus',
          label: '状态',
          width: 200,
          formattor: val => {
            const apStatus = this.apStatus
            let datas = ''
            apStatus.forEach(element => {
              if (element.value == val) {
                datas = element.label
              }
            })
            return datas
          }
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              // 编辑
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              // 提交
              callback: row => this.inactiveHandle(row, 'active'),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.submit')
              }
            },
            {
              // 删除
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              // 生效
              callback: row => this.inactiveHandle(row, 'active'),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'UNABLE',
              formattor: () => {
                return this.$t('common.active')
              }
            },
            {
              // 失效
              callback: row => this.inactiveHandle(row, 'inactive'),
              // code: "pr:requirementApply:edit",
              show: row => row.approveStatus === 'ENABLE',
              formattor: () => {
                return this.$t('common.inactive')
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'reviewModelName',
          label: '评审模板名称'
        },
        {
          prop: 'queryCreationDate',
          label: '创建时间',
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 点击失效或者生效，inactive为失效
    inactiveHandle (row, bol) {
      const reviewModelId = row.reviewModelId
      let type = ''
      if (bol == 'inactive') {
        type = 'UNABLE'
      } else if (bol == 'active') {
        type = 'ENABLE'
      }
      const obj = {
        reviewModelId: reviewModelId,
        approveStatus: type
      }
      siteReviewModel.update(obj).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    readOne (row) {
      console.log(row)
      this.mode = 'edit'
      const tab = {
        component: sitereviewmodelEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: true
        },
        title: '现场评审模板管理编辑',
        name: 'sitereviewmodelEdit' + row.reviewModelId
      }
      this.$emit('tab-add', tab)
    },
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
          siteReviewModel.delete(row.reviewModelId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: sitereviewmodelEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '现场评审模板管理新增',
        name: 'sitereviewmodelEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: sitereviewmodelEdit,
        params: {
          row,
          flag: this.mode
        },
        title: '现场评审模板管理编辑',
        name: 'sitereviewmodelEdit' + row.reviewModelId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
