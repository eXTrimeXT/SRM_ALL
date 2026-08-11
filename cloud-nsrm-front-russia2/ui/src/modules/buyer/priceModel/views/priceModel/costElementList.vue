<template>
  <el-container
    class="flex-container cost_element_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :checkbox="true"
        :pre-query-data="queryParam"
        :check-change="handleCurrentChange"
        :source="costElement.listCostElement"
        open-custom-table
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { costElement } from 'modb@/priceModel/api'

export default {
  name: 'CostElementList',

  components: {
    TableView,
    FormWrapper
  },

  props: {
    orgId: {
      type: [Number, Object],
      required: true
    }
  },

  data () {
    return {
      costElement: costElement,
      tableHeader: [
        {
          label: () => this.$t('priceModel.costElement.elementCode'),
          prop: 'elementCode',
          width: 150
        },
        {
          label: () => this.$t('priceModel.costElement.elementName'),
          prop: 'elementName',
          width: 150
        },
        {
          label: () => this.$t('priceModel.costElement.elementType'),
          formattor: val => this.$getDictLabel('COST_ELEMENT_TYPE', val),
          prop: 'elementType',
          width: 130
        },
        {
          label: () => this.$t('priceModel.costElement.status'),
          prop: 'status',
          formattor: val =>
            this.$getDictLabel('COST_ELEMENT_STATUS', val),
          width: 100
        },
        {
          label: () => this.$t('priceModel.costElement.orgName'),
          prop: 'orgName',
          width: 150
        },
        {
          label: () => this.$t('priceModel.costElement.elementVersion'),
          prop: 'elementVersion',
          width: 130
        },
        {
          label: () => this.$t('priceModel.costElement.crucialAttributes'),
          prop: 'crucialAttributes',
          width: 150
        },
        {
          label: () => this.$t('priceModel.costElement.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 130
        },
        {
          label: () => this.$t('priceModel.costElement.lastUpdatedBy'),
          prop: 'lastUpdatedUserName', // lastUpdatedBy
          width: 130
        },
        {
          label: () => this.$t('priceModel.costElement.lastUpdateDate'),
          prop: 'lastUpdateDate',
          width: 150,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('priceModel.costElement.creationDate'),
          prop: 'creationDate',
          width: 150,
          dataType: 'dateTime'
        }
      ],
      tableData: [],
      currentRows: [],
      queryForm: [
        // 要素类型
        {
          prop: 'elementType',
          type: 'dict',
          code: 'COST_ELEMENT_TYPE',
          label: () => this.$t('priceModel.costElement.elementType')
        },
        // 要素名称
        {
          prop: 'elementName',
          label: () => this.$t('priceModel.costElement.elementName')
        },
        // 类型
        {
          prop: 'enableCommon',
          type: 'dict',
          code: 'COST_ENABLE_COMMON',
          label: () => this.$t('elementDefinition.type')
        },
        // 业务实体
        {
          prop: 'orgId',
          type: 'OUorganizationSelector',
          disabled: true,
          label: () => this.$t('priceModel.costElement.orgName')
        },
        // 创建日期
        {
          prop: 'creationDate',
          type: 'date',
          label: () => this.$t('priceModel.costElement.creationDate')
        }
      ],
      queryParam: {
        status: 'VALID',
        isNew: 'Y'
      },
      // 表单默认参数
      preFormObj: {
        orgId: this.orgId,
        // 默认专用类型
        enableCommon: 'SPECIAL_USE'
      }
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    getQueryData (v) {
      const params = {
        ...this.preFormObj,
        ...this.queryParam,
        ...v
      }
      if (params.enableCommon === 'COMMON') {
        // 通用类型，不用传业务实体ID
        params.orgId = ''
        this.$refs.formWrapper.setValue('orgId', params.orgId)
      }
      this.queryParam = params

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>

<style scoped lang="scss">
.cost_element_list_wrapper {
  height: 400px;
  width: 100%;
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
