<template>
  <el-container class="flex-container bomhead_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
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
        :source="bomApi.list"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import bomheadEdit from './edit.vue'
import { bomApi } from 'modb@/outsourcingManagement/api'

export default {
  name: 'BomheadList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      bomApi: bomApi,
      name: 'bomheadList',
      tableName: 'bomheadTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [
        {
          prop: 'materialCode',
          label: '总成料号'
        },
        {
          prop: 'materialName',
          label: '总成描述'
        },
        {
          prop: 'orgName',
          label: '业务实体'
        },
        {
          prop: 'organizationName',
          label: '库存组织'
        },
        {
          prop: 'status',
          label: '是否生效',
          width: 100,
          formattor: function (e) {
            if (e == 'Y') {
              return '是'
            } else {
              return '否'
            }
          }
        },
        {
          prop: 'creationDate',
          label: '创建日期'
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
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

      filterConfig: [
        {
          prop: 'materialCode',
          label: '总成料号',
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item',
          preQueryData: { 't.MATERIAL_ATTR': 'OUTSOURCING' }
        },
        { prop: 'orgId', label: '业务实体', type: 'OUorganizationSelector' },
        {
          prop: 'status',
          label: '是否生效',
          type: 'dict',
          code: 'YES_OR_NO'
        },
        { prop: 'createdBy', label: '创建人' },
        {
          prop: 'creationStartDate',
          label: '创建开始日期',
          type: 'date'
        },
        {
          prop: 'creationEndDate',
          label: '创建结束日期',
          type: 'date'
        }
      ],
      queryParam: {},
      form: {
        materialCode: null,
        materialName: null,
        orgId: null,
        orgName: null,
        organizationId: null,
        organizationName: null,
        status: null
      }
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
          bomApi.delete(row.bomHeadId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
      addHandle (row) {
          this.mode = 'add'
          const tab = {
              component: bomheadEdit,
              params: {
                  row,
                  flag: this.mode
              },
              title: '委外BOM维护新增',
              name: 'bomheadEdit'
          }
          this.$emit('tab-add', tab)
      },
      editHandle (row) {
          this.mode = 'edit'
          const tab = {
              component: bomheadEdit,
              params: {
                  row,
                  flag: this.mode
              },
              title: '委外BOM维护编辑',
              name: 'bomheadEdit' + row.bomHeadId
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
