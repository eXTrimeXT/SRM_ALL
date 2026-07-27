<template>
  <el-container class="flex-container the_contractTemplateList_wrapper" direction="vertical">
    <el-main>
      <form-wrapper :form-array="preArr" :init-active="true" @getFormData="getQuerydata" />
      <p style="padding-left: 11px; margin: 3px">
        <el-button type="primary" @click="addOne">
          {{ $t('common.new') }}
        </el-button>
      </p>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cm/template/templHead/listPageByParm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import contractTemplateDetail from './contractTemplateDetail'
import { parseTime } from '@/utils'

export default {
  name: 'ContractTemplateList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    contractTemplateDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'contractTemplateTable',
      tableName: 'contractTemplateList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        {
          prop: 'templType',
          label: this.$t('contractMod.templType'),
          type: 'dict',
          code: 'TEMPLATE_TYPE'
        },
        { prop: 'templName', label: this.$t('contractMod.templHeadId') },
        {
          prop: 'templDescription',
          label: this.$t('contractMod.templDescription')
        },
        {
          prop: 'templStatus',
          label: this.$t('contractMod.templStatus'),
          type: 'dict',
          code: 'TEMPLATE_STATUS'
        },
        { prop: 'createdBy', label: this.$t('common.creator') },
        {
          prop: 'creationDate',
          label: this.$t('orderMod.buyerOrderSynergy.creationDate'),
          type: 'date'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'templStatus',
        label: this.$t('common.status'),
        width: 80,
        dataType: 'dict',
        code: 'TEMPLATE_STATUS'
      },
      {
        prop: 'templType',
        label: this.$t('contractMod.templType'),
        width: 130,
        dataType: 'dict',
        code: 'TEMPLATE_TYPE'
      },
      {
        prop: 'templName',
        label: this.$t('contractMod.templHeadId'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'templDescription',
        label: this.$t('contractMod.templDescription'),
        minWidth: 150
      },
      {
        prop: 'startDate',
        label: '生效时间',
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: '失效时间',
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      { prop: 'invalidReason', label: '失效原因', width: 100 },
      {
        prop: 'createdUserName', // createdBy
        label: '创建人',
        width: 100
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: '最后更新人',
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: '最后更新时间',
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'operation',
        label: '操作',
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.enableOne(row)
            }.bind(this),
            formattor (val) {
              return '生效'
            },
            show: function (row) {
              if (row.templStatus !== 'EFFECTIVE') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.disableOne(row)
            }.bind(this),
            formattor (val) {
              return '失效'
            },
            show: function (row) {
              if (row.templStatus === 'EFFECTIVE') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return '编辑'
            }
          },
          {
            callback: function (row) {
              this.copyOne(row)
            }.bind(this),
            formattor (val) {
              return '复制'
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return '删除'
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    addOne () {
      this.$emit('tab-add', {
        component: contractTemplateDetail,
        params: {
          flag: 'add'
        },
        title: '创建合同模板',
        name: 'contractTemplateDetail'
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: contractTemplateDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.templName,
        name: 'contractTemplateDetail' + row.templName
      })
    },
    enableOne (row) {
      // 生效
      this.$http({
        url: '/api-cm/template/templHead/effective',
        method: 'GET',
        params: { templHeadId: row.templHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success('保存成功')
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    disableOne (row) {
      // 失效
      this.$prompt('失效原因', '撤回失效', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
        .then(({ value }) => {
          if (!value) {
            this.$message.success('请输入失效原因')
            return
          }
          this.$http({
            url: '/api-cm/template/templHead/invalid',
            method: 'POST',
            data: { templHeadId: row.templHeadId, invalidReason: value },
            loading: true
          })
            .then((data) => {
              this.$message.success('保存成功')
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {
          console.info('取消失效！')
        })
    },
    copyOne (row) {
      this.$confirm('确认复制这条数据？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/template/templHead/copy',
            method: 'GET',
            params: { templHeadId: row.templHeadId },
            loading: true
          })
            .then((data) => {
              this.$message.success('复制成功')
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => { })
    },
    deleteOne (row) {
      this.$confirm('当前操将永久删除这条数据，确认删除这条数据？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/template/templHead/deleteContractTemplDTO',
            method: 'GET',
            params: { templHeadId: row.templHeadId },
            loading: true
          })
            .then((data) => {
              this.$message.success('删除成功')
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => { })
    }
  }
}
</script>
<style scoped lang="scss"></style>
