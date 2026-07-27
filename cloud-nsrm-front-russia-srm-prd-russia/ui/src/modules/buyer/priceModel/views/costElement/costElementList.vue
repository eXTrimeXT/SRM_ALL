<template>
  <el-container
    class="flex-container cost_element_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="edit({}, 'add')"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        checkbox
        :pre-query-data="queryParam"
        :check-change="x => selectRows = x"
        :source="costElement.listPage"
        open-custom-table
        :com-active="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import costElementEdit from './costElementEdit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { costElement } from 'modb@/priceModel/api'

export default {
  name: 'CostElementList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      costElement: costElement,
      selectRows: [],
      tableHeader: [
        // 要素编码
        {
          label: () => this.$t('priceModel.costElement.elementCode'),
          prop: 'elementCode',
          minWidth: 150
        },
        // 要素名称
        {
          label: () => this.$t('priceModel.costElement.elementName'),
          prop: 'elementName',
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOnly(row)
        },
        // 要素类型
        {
          label: () => this.$t('priceModel.costElement.elementType'),
          formattor: val => this.$getDictLabel('COST_ELEMENT_TYPE', val),
          prop: 'elementType',
          minWidth: 130
        },
        // 类型
        {
          prop: 'enableCommon',
          label: this.$t('elementDefinition.type'),
          formattor: val => this.$getDictLabel('COST_ENABLE_COMMON', val),
          minWidth: 100
        },
        // 状态
        {
          label: () => this.$t('priceModel.costElement.status'),
          prop: 'status',
          formattor: val => this.$getDictLabel('COST_ELEMENT_STATUS', val),
          minWidth: 100
        },
        // 业务实体
        {
          label: () => this.$t('priceModel.costElement.orgName'),
          prop: 'orgName',
          minWidth: 150
        },
        // 版本
        {
          label: () => this.$t('priceModel.costElement.elementVersion'),
          prop: 'elementVersion',
          minWidth: 130
        },
        // 关键属性
        {
          label: () => this.$t('priceModel.costElement.crucialAttributes'),
          prop: 'crucialAttributes',
          minWidth: 150
        },
        // 创建人
        {
          label: () => this.$t('priceModel.costElement.createdBy'),
          prop: 'createdBy',
          minWidth: 130
        },
        // 创建日期
        {
          label: () => this.$t('priceModel.costElement.creationDate'),
          prop: 'creationDate',
          minWidth: 150
        },
        // 最近更新人
        {
          label: () => this.$t('priceModel.costElement.lastUpdatedBy'),
          prop: 'lastUpdatedBy',
          minWidth: 130
        },
        // 更新日期
        {
          label: () => this.$t('priceModel.costElement.lastUpdateDate'),
          prop: 'lastUpdateDate',
          minWidth: 150
        },
        // 操作
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          minWidth: 160,
          buttons: [
            // 编辑
            {
              callback: row => this.edit(row),
              show: row => row.status === 'DRAFT',
              formattor: () => this.$t('common.edit')
            },
            // 删除
            {
              callback: row => this.deleteItem(row),
              show: row => row.status === 'DRAFT',
              formattor: () => this.$t('common.delete')
            },
            // 生效
            {
              callback: row => this.effect(row),
              show: row => row.status === 'DRAFT',
              formattor: () => this.$t('priceModel.costElement.effect')
            },
            // 创建新版本
            {
              callback: row => this.createNewVersion(row),
              show: row => row.status === 'VALID',
              formattor: () => this.$t('priceModel.costElement.createNew')
            },
            // 失效
            {
              callback: row => this.failure(row),
              show: row => row.status === 'VALID',
              formattor: () => this.$t('priceModel.costElement.failure')
            },
            // 复制
            {
              callback: row => this.copy(row),
              show: row => row.status === 'VALID' || row.status === 'INVALID',
              formattor: () => this.$t('common.copy')
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        // 业务实体
        {
          prop: 'orgId',
          label: this.$t('priceModel.costElement.orgName'),
          type: 'OUorganizationSelector'
        },
        // 状态
        {
          prop: 'status',
          label: () => this.$t('priceModel.costElement.status'),
          type: 'dict',
          code: 'COST_ELEMENT_STATUS'
        },
        // 更新日期范围
        {
          prop: 'dateList',
          type: 'daterange',
          label: this.$t('dataConfMod.updateDateRange')
        },
        // 要素编码
        {
          prop: 'elementCode',
          label: this.$t('priceModel.costElement.elementCode')
        },
        // 要素名称
        {
          prop: 'elementName',
          label: this.$t('elementDefinition.elementName')
        },
        // 创建人
        {
          label: this.$t('dataConfMod.createdBy'),
          prop: 'createdId',
          type: 'quicksearch',
          propKey: 'userId',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // 类型
        {
          prop: 'enableCommon',
          label: () => this.$t('elementDefinition.type'),
          type: 'dict',
          code: 'COST_ENABLE_COMMON'
        },
        // 是否显示最新
        {
          type: 'switch',
          prop: 'isNew',
          label: () => this.$t('priceModel.costElement.isNew')
        }
      ],
      queryParam: {}
    }
  },

  created () {
    this.getQueryData()
  },

  methods: {
    // 跳转查看界面
    readOnly (row) {
      const tab = {
        component: costElementEdit,
        params: {
          row,
          flag: 'edit',
          readOnly: true
        },
        title: `${this.$t('priceModel.priceModel.readOnly')}-${row.elementName}`,
        name: 'costElementEdit' + row.costElementId
      }
      this.$emit('tab-add', tab)
    },

    // 创建新版本
    createNewVersion (row) {
      costElement.createNewVersion(row.costElementId).then(res => {
        this.$message({
          type: 'success',
          message: this.$t('dataConfMod.newVersionSuccessfully')
        })

        let data = res.data
        const tab = {
          component: costElementEdit,
          params: {
            row: data,
            flag: 'edit'
          },
          title: `${this.$t('dataConfMod.editNewVersion')}-${data.elementName}`,
          name: 'costElementEdit' + data.costElementId
        }
        this.$emit('tab-add', tab)
      })
    },

    // 生效
    effect ({ costElementId }) {
      costElement.takeEffect(costElementId).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },

    // 失效
    failure ({ costElementId }) {
      costElement.failure(costElementId).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },

    // 复制成本要是
    copy (row) {
      // 访问后台，复制单据后，后台会回传新单据的信息，将新单据信息传递给tab页
      costElement.copy(row.costElementId).then(res => {
        this.$message.success(this.$t('logisticsMod.copySuccess'))
        this.getQueryData()

        let row = res.data
        let flag = 'edit'

        const tab = {
          component: costElementEdit,
          params: {
            row,
            flag
          },
          title: `${this.$t('common.edit')}-${row.elementName}`,
          name: 'costElementEdit' + row.costElementId
        }
        this.$emit('tab-add', tab)
      })
    },

    // 删除
    deleteItem ({ costElementId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        costElement.delete(costElementId).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    // 打开编辑页(新增/修改)
    edit (row, flag = 'edit') {
      const tab = {
        component: costElementEdit,
        params: {
          row,
          flag
        },
        title: `${this.$t(`common.${flag}`)}${flag === 'add' ? '' : `-${row.elementName}`}`,
        name: 'costElementEdit' + (flag === 'edit' ? row.costElementId : '')
      }
      this.$emit('tab-add', tab)
    },

    // 列表查询
    getQueryData (v) {
      if (v && v.dateList) {
        // 设置日期筛选范围
        v.lastUpdateDateFrom = v.dateList[0]
        v.lastUpdateDateTo = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.creationDate
        delete v.lastUpdateDate
      }
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    syncFilterParams (values) {
      this.queryParam = values
    }
  }
}
</script>

<style scoped lang="scss">
.cost_element_list_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
<style>
.cost_element_list_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
