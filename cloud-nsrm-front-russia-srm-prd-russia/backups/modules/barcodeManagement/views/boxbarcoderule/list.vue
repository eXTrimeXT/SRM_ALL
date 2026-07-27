<template>
  <el-container
    class="flex-container barcoderulehead_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.generate.barcoderulehead.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import barcoderuleheadEdit from './edit.vue'

export default {
  name: 'BarcoderuleheadList',
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
      name: 'barcoderuleheadList',
      tableName: 'barcoderuleheadTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'ruleName',
          label: '规则名称',
          width: 100
        },
        {
          prop: 'ruleDesc',
          label: '规则描述',
          width: 150
        },

        {
          prop: 'businessBoxType',
          label: '所属箱型业务',
          width: 100,
          formattor: value => { return this.businessBoxOptions.find(v => v.value === value).label || '' }
        },
        {
          prop: 'businessCodeType',
          label: '所属条码业务',
          width: 100,
          formattor: value => { return this.businessCodeOptions.find(v => v.value === value).label || '' }
        },
        {
          prop: 'categoryName',
          label: '品类名称',
          width: 100
        },
          {
          prop: 'startDate',
          label: '开始日期',
          width: 100
        },
        {
          prop: 'endDate',
          label: '结束时间',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          width: 100
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
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
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
      // 所属业务
      businessBoxOptions: [
        { value: 'INNER_BOX', label: '内箱' },
        { value: 'OUTER_BOX', label: '外箱' }
      ],
      // 所属业务
      businessCodeOptions: [
        { value: 'BAR_CODE', label: '条码' },
        { value: 'QR_CODE', label: '二维码' }
      ],
      filterConfig: [
              { prop: 'ruleName', label: '规则名称' },
              { prop: 'startDate', label: '开始日期' },
              { prop: 'endDate', label: '结束时间' }
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
          this.$api.generate.barcoderulehead.delete(row.ruleHeadId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
      addHandle (row) {
          this.mode = 'add'
          const tab = {
              component: barcoderuleheadEdit,
              params: {
                  row,
                  flag: this.mode
              },
              title: '条码生成规则配置新增',
              name: 'barcoderuleheadEdit'
          }
          this.$emit('tab-add', tab)
      },
      editHandle (row) {
          this.mode = 'edit'
          const tab = {
              component: barcoderuleheadEdit,
              params: {
                  row,
                  flag: this.mode
              },
              title: '条码生成规则配置编辑',
              name: 'barcoderuleheadEdit' + row.ruleHeadId
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
