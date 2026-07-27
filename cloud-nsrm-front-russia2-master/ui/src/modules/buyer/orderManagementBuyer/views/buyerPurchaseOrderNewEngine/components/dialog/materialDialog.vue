<template>
  <!-- 物料新增 - 物料明细选择-->
  <srm-dialog
    :title="$t('purchaseDemand.materialDetailSelect')"
    size="large"
    :visible.sync="dialogFormVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :before-close="close"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <!-- <FormWrapper
      ref="lineFormRef"
      :preFormObj="preFormObj"
      :form-array="queryForm"
      @getFormData="getQuerydata"
    > -->
    <FormWrapper
      ref="lineFormRef"
      :colLength="2"
      :form-array="queryForm"
      @getFormData="getQuerydata"
    >
      <template #level="{ scope }">
        <div class="form-item-line">
          <el-select
            v-model="scope.selectLevel"
            class="input-with-select"
            :placeholder="$t('common.pleaseSelect')"
            clearable
          >
            <el-option :id="1" :label="$t('purchaseDemand.bigCategory')" value="1" />
            <el-option :id="2" :label="$t('purchaseDemand.midCategory')" value="2" />
            <el-option :id="3" :label="$t('purchaseDemand.smallCategory')" value="3" />
          </el-select>
          <el-input
            v-model="scope.inputLevel"
            :placeholder="$t('common.pleaseTypeContents')"
            clearable
          />
        </div>
      </template>
    </FormWrapper>
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      :check-change="checkChange"
      url="/api-base/material/materialItem/listForOrder"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="confirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'PurchaseDialog',
  components: {
    QuickSearch,
    FormWrapper,
    TableView
  },
  props: {
    queryData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      selections: [],
      pageSize: 15,
      gridId: 'list',
      tableData: [],
      queryParam: {},
      // preFormObj: { selectLevel: '3' },
      queryForm: [
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'level',
          label: this.$t('purchaseDemand.materialCategory'),
          type: 'slot',
          slot: 'level'
        }
      ],
      dialogFormVisible: false,
      tableHeader: [
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          width: 120
        },
        {
          prop: 'bigCategoryName',
          label: () => this.$t('purchaseDemand.materialCate'),
          width: 120
        },
        {
          prop: 'middleCategoryName',
          label: () => this.$t('purchaseDemand.midCategoryName'),
          width: 120
        },
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'),
          width: 120
        },
        {
          prop: 'materialCode',
          label: () => this.$t('purchaseDemand.itemCode'),
          width: 120
        },
        {
          prop: 'materialName',
          label: () => this.$t('purchaseDemand.itemName'),
          width: 120
        }
      ]
    }
  },
  watch: {
    visible (newValue) {
      this.dialogFormVisible = newValue
      if (newValue) {
        this.$nextTick(() => {
          this.$refs['lineFormRef'].reset()
          this.getQuerydata()
        })
      }
    }
  },
  methods: {
    getQuerydata (obj = {}) {
      const params = {
        // level: obj.selectLevel || 3,
        level: obj.selectLevel,
        param: obj.inputLevel
      }
      this.$http({
        url: '/api-base/purchase/purchaseCategory/queryCategoryByType',
        method: 'GET',
        params: { ...params, enabled: 'Y' },
        loading: true
      }).then(res => {
        const categoryRata = res.data.filter(v => v.categoryName !== '服务类')

        this.queryParam = {
          ...this.queryData,
          purchaseCategories: categoryRata,
          categoryName: obj.inputLevel || '',
          materialCode: obj.materialCode || ''
        }
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      })
    },
    checkChange (val) {
      this.selections = val
    },
    close () {
      this.$emit('close', this.selections)
    },
    confirm () {
      this.$emit('confirm', this.selections)
    }
  }
}
</script>

<style lang="scss" scoped>
.form-item-line {
  display: flex;
  align-items: center;
  width: 250px;
}

.input-with-select {
  width: 150px !important;
  margin-right: 10px;
}
</style>
