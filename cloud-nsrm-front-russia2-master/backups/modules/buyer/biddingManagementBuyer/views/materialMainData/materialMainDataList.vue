<template>
  <el-container
    class="flex-container-notab materialMainData_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!--新增-->
          <AuthorityButton
            code="bid:materialMainData:add"
            type="primary"
            @click="navTabsAdd('add')"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :pre-query-data="queryParam"
        :checkbox="false"
        :comActive="$attrs['changeTab']"
        open-custom-table
        url="/api-base/materialFormulaRelate/pageMaterialFormulaRelate"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import materialMainDataDetail from './materialMainDataDetail'

export default {
  name: 'MaterialMainDataList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [
        // 关联类型
        {
          label: this.$t('dataConfMod.formulaRelateType'),
          prop: 'formulaRelateType',
          width: 150,
          dataType: 'dict',
          code: 'FORMULA_RELATE_TYPE'
        },
        // 业务实体
        {
          label: this.$t('bidMod.affairsEntity'),
          prop: 'orgOuNames',
          width: 150
        },
        // 物料编码
        {
          label: this.$t('materialMainData.materialCode'),
          prop: 'materialCode',
          width: 150
        },
        // 物料名称
        {
          label: this.$t('materialMainData.materialName'),
          prop: 'materialName',
          minWidth: 150
        },
        {
          label: this.$t('materialMainData.categoryName'),
          prop: 'categoryName',
          width: 150
        },
        // 单位
        {
          label: this.$t('materialMainData.unit'),
          prop: 'unit',
          formattor: val => this.$getDictLabel('unit', val),
          width: 150
        },
        {
          label: this.$t('materialMainData.priceFormula'),
          prop: 'formulaName',
          width: 150
        },
        {
          label: this.$t('formula.formulaDetail'),
          prop: 'formulaValue',
          width: 150
        },
        // t 状态
        {
          label: this.$t('common.status'),
          prop: 'status',
          width: 100,
          formattor: val => this.$getDictLabel('BASE_INFO_STATUS', val)
        },
        {
          label: this.$t('materialMainData.lastUpdatedBy'),
          prop: 'lastUpdatedUserName', // lastUpdatedBy
          width: 150
        },
        {
          prop: 'lastUpdateDate',
          label: this.$t('materialMainData.lastUpdateDate'),
          width: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 160,
          buttons: [
            // b 编辑
            {
              // 拟定状态
              show: row => row.status === 'DRAFT',
              formattor: () => this.$t('common.edit'),
              callback: row => this.navTabsAdd('edit', row)
            },
            // b 查看
            {
              // 非拟定状态
              show: row => row.status !== 'DRAFT',
              formattor: () => this.$t('common.view'),
              callback: row => this.navTabsAdd('view', row)
            },
            // b 生效
            {
              // 非生效状态
              show: row => row.status !== 'VALID',
              formattor: () => this.$t('common.active'),
              callback: row => this.activeOrInactiveItem(row.relateId, true)
            },
            // b 失效
            {
              // 生效状态
              show: row => row.status === 'VALID',
              formattor: () => this.$t('common.inactive'),
              callback: row => this.activeOrInactiveItem(row.relateId, false)
            },
            // b 删除
            {
              // 生效和失效状态不能删除
              show: row => !['VALID', 'INVALID'].includes(row.status),
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteRow(row.relateId)
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          prop: 'formulaRelateType',
          label: this.$t('dataConfMod.formulaRelateType'),
          type: 'dict',
          code: 'FORMULA_RELATE_TYPE'
        },
        {
          prop: 'orgOuId',
          label: this.$t('bidMod.affairsEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'materialCode',
          label: this.$t('materialMainData.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item_display'
        },
        {
          prop: 'categoryId',
          label: this.$t('common.category'), // 品类
          type: 'catSelect',
          showKey: 'categoryId'
        }
      ],
      queryParam: {}
    }
  },

  created () {
    this.getQueryData()
  },

  methods: {
    /* 删除 */
    deleteRow (relateId) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-base/materialFormulaRelate/deleteMaterialFormulaRelate/${relateId}`,
          method: 'DELETE',
          loading: true
        }).then(data => {
          if (data) {
            this.$message.success(this.$t('common.successUpdate'))
            // 更新列表
            this.getQueryData()
          }
        })
      })
    },

    /* 查询列表数据 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 生效 or 失效 toValid: true生效，false失效 */
    activeOrInactiveItem (relateId, toValid) {
      if (!relateId) return

      this.$http({
        url: 'api-base/materialFormulaRelate/validOrInvalidMaterialFormulaRelate',
        method: 'POST',
        data: {
          relateId,
          toValid
        },
        loading: true
      }).then(data => {
        if (data) {
          this.$message.success(this.$t('common.successUpdate'))
          // 更新列表
          this.getQueryData()
        }
      })
    },

    /* 打开 新增 / 编辑 / 查看 标签页 */
    navTabsAdd (type, row) {
      // 默认新增
      let tabObj = {
        component: materialMainDataDetail,
        params: {
          flag: type
        },
        title: this.$t('common.add'),
        name: 'materialMainDataDetail'
      }
      if (type !== 'add') {
        // 修改 / 查看
        tabObj = {
          ...tabObj,
          params: {
            ...tabObj.params,
            row: row,
            readOnly: type === 'view'
          },
          title: row.relateId,
          name: 'materialMainDataDetail' + row.relateId
        }
      }
      this.$emit('tab-add', tabObj)
    }
  }
}
</script>

<style scoped lang="scss">
.materialMainData_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
  :deep(.el-table th > .cell) {
    display: flex;
    justify-content: center;
  }
}
</style>
