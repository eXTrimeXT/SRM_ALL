<template>
  <el-form
    ref="materialTable"
    class="tableForm"
    :model="materialModle"
    :rules="materialModle.rules"
  >
    <el-table
      ref="table"
      :data="materialModle.tableData"
      stripe
      border
      highlight-current-row
      @selection-change="checkChange"
    >
      <!-- <el-table-column type="selection" /> -->
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.rowNum')"
        prop="rowNum"
      />
      <el-table-column
        width="130"
        :label="$t('purchaseDemand.applyStatus')"
        prop="applyStatus"
        :formatter="formatApplyStatus"
      />
      <el-table-column
        width="300"
        show-overflow-tooltip
        :label="$t('purchaseDemand.fullPathId')"
        prop="orgId"
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.fullPathId') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.orgId'"
            :rules="materialModle.rules.orgId"
          >
            <OrganizationSelectTree
              v-model="scope.row.orgId"
              :disabled="justShow"
              :scope="scope"
              @select="treeselectChange"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="150"
        :label="$t('purchaseDemand.receivedFactory')"
        prop="receivedFactory"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.receivedFactory') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.receivedFactory'"
            :rules="materialModle.rules.receivedFactory"
          >
            <!-- <el-input v-model="scope.row.receivedFactory" /> -->
            <CustomSelect
              v-model="scope.row.receivedFactory"
              :disabled="justShow"
              :organization-id="scope.row.orgId"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="150"
        :label="$t('purchaseDemand.requirementDate')"
        prop="requirementDate"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.requirementDate') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.requirementDate'"
            :rules="materialModle.rules.requirementDate"
          >
            <el-date-picker
              v-model="scope.row.requirementDate"
              :disabled="justShow"
              type="date"
              :picker-options="pickerOptions"
              :format="$formatDatePicker"
              value-format="yyyy-MM-dd"
              @change="dateChange"
              @focus="focus(scope)"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="130"
        :label="$t('purchaseDemand.itemCode')"
        prop="itemCode"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.itemCode') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.itemCode'"
            :rules="materialModle.rules.itemCode"
          >
            <QuickSearch
              v-if="!justShow"
              :show-input="scope.row.itemCode"
              show-key="materialCode"
              :scope-data="scope"
              name="scc_base_material_item_display"
              @close-quicksearch="getCompanyObj"
            />
            <span
              v-else
              v-html="scope.row.itemCode"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="150"
        :label="$t('purchaseDemand.requirementQuantity')"
        prop="requirementQuantity"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.requirementQuantity') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.requirementQuantity'"
            :rules="materialModle.rules.requirementQuantity"
          >
            <el-input
              v-model.number="scope.row.requirementQuantity"
              :disabled="justShow"
              @focus="focus(scope)"
              @change="numberChange"
            />
            <!-- <el-input-number
              @focus="focus(scope)"
              v-model="scope.row.requirementQuantity"
              controls-position="right"
              @change="numberChange"
            ></el-input-number> -->
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="130"
        :label="$t('purchaseDemand.itemName')"
        prop="itemDesc"
      />
      <el-table-column
        width="120"
        :label="$t('purchaseDemand.requirementDepartment')"
        prop="requirementDepartment"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.requirementDepartment') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.requirementDepartment'"
            :rules="materialModle.rules.requirementDepartment"
          >
            <el-input
              v-model="scope.row.requirementDepartment"
              :disabled="justShow"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <!-- <el-table-column width="120" :label="$t(purchaseDemand.categoryName')" prop="categoryName">
        <template slot="header">
          <i class="toRequired">*</i> {{$t(purchaseDemand.categoryName')}}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.categoryName'"
            :rules="materialModle.rules.categoryName"
          >
            <el-input v-model="scope.row.categoryName" />
          </el-form-item>
        </template>
      </el-table-column> -->
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.budget')"
        prop="budget"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.budget') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.budget'"
            :rules="materialModle.rules.budget"
          >
            <el-input
              v-model.number="scope.row.budget"
              :disabled="justShow"
              type="number"
            />
          </el-form-item>
        </template>
      </el-table-column>

      <!-- <el-table-column width="100" :label="$t(purchaseDemand.itemCode')" prop="itemCode" /> -->
      <el-table-column
        width="130"
        :label="$t('purchaseDemand.categoryName')"
        prop="categoryName"
      />
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.unit')"
        prop="unit"
      >
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.unit'"
            :rules="materialModle.rules.unit"
          >
            <dict-select
              v-model="scope.row.unit"
              code="unit"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.notaxPrice')"
        prop="notaxPrice"
      />
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.priceUnit')"
        prop="priceUnit"
      />
      <el-table-column
        width="130"
        :label="$t('purchaseDemand.taxRate')"
        prop="taxKey"
      >
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.taxKey'"
            :rules="materialModle.rules.taxKey"
          >
            <dict-select
              v-model="statementHead.taxKey"
              code="tax"
              disabled
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.currency')"
        prop="currency"
      >
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.currency'"
            :rules="materialModle.rules.currency"
          >
            <dict-select
              v-model="scope.row.currency"
              code="currency"
              disabled
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.totalAmount')"
        prop="totalAmount"
      />
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.applyReason')"
        prop="applyReason"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.applyReason') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.applyReason'"
            :rules="materialModle.rules.applyReason"
          >
            <el-input
              v-model="scope.row.applyReason"
              :disabled="justShow"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.inventoryPlace')"
        prop="inventoryPlace"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.inventoryPlace') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.inventoryPlace'"
            :rules="materialModle.rules.inventoryPlace"
          >
            <el-input
              v-model="scope.row.inventoryPlace"
              :disabled="justShow"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.costType')"
        prop="costType"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.costType') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.costType'"
            :rules="materialModle.rules.costType"
          >
            <el-select
              v-model="scope.row.costType"
              :disabled="justShow"
            >
              <el-option
                v-for="item in costType"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.costNum')"
        prop="costNum"
        show-overflow-tooltip
      >
        <template slot="header">
          <i class="toRequired">*</i> {{ $t('purchaseDemand.costNum') }}
        </template>
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.costNum'"
            :rules="materialModle.rules.costNum"
          >
            <el-input
              v-model="scope.row.costNum"
              :disabled="justShow"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        width="100"
        :label="$t('purchaseDemand.brand')"
        prop="brand"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-form-item
            :prop="'tableData.' + scope.$index + '.brand'"
            :rules="materialModle.rules.brand"
          >
            <el-input
              v-model="scope.row.brand"
              :disabled="justShow"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        prop="rejectReason"
        :label="$t('purchaseDemand.rejectReason')"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="!justShow"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="deleteDetials(scope.$index, scope.row)"
          >
            <!-- 删除 -->
            {{ $t("components.common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
</template>
<script>
import { parseTime, adaptDictData } from '@/utils'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import {
  getDictItemList
} from '@/api/common'
import CustomSelect from './select'
import { applicationAndAuditApi } from 'modb@/purchasingDemand/api'

export default {
  name: 'TableForm',
  components: { OrganizationSelectTree, QuickSearch, CustomSelect },
  props: {
    justShow: {
      type: Boolean,
      default: false
    },
    tableData: {
      type: Array,
      default: null
    }
  },
  data () {
    const checkNumber = (rule, value, callback) => {
      if (value < 0) {
        callback(new Error(this.$t('purchaseDemand.lessThan0Tips')))// 不能小于0
      } else {
        callback()
      }
    }
    return {
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      currentIndex: null,
      costType: [],
      applyStatus: [],
      materialModle: {
        tableData: [],
        rules: {
          fullPathId: { required: true },
          requirementDepartment: { type: 'string', required: true },
          budget: [
            { required: true },
            { validator: checkNumber, trigger: 'blur' }
          ],
          itemCode: { type: 'string', required: true },
          requirementQuantity: [
            { required: true },
            { validator: checkNumber, trigger: 'blur' }
          ],
          requirementDate: { required: true },
          applyReason: { type: 'string', required: true },
          inventoryPlace: { type: 'string', required: true },
          costType: { type: 'string', required: true },
          costNum: { type: 'string', required: true },
          receivedFactory: { type: 'string', required: true },
          taxKey: { required: true }
          // brand: { type: "string", required: true }
        }
      }
    }
  },
  mounted () {
    this.addItem()
    this.importTable()
    const codes = ['COST_TYPE', 'APPLICATION_STATUS'].map(i => ({
      dictCode: i
    }))
    getDictItemList(codes).then(res => {
      const [COST_TYPE, APPLICATION_STATUS] = res.data
      this.costType = adaptDictData(COST_TYPE.COST_TYPE)
      this.applyStatus = adaptDictData(APPLICATION_STATUS.APPLICATION_STATUS)
    })

    this.materialModle.tableData = this.tableData ? [...this.tableData] : []
  },
  methods: {
    formatApplyStatus (row, column, cellValue, index) {
      if (this.applyStatus.length) {
        const target = this.applyStatus.find(i => i.value === cellValue) || {}
        return target.label ? target.label : cellValue
      }
    },
    addItem () {
      this.$on('addItem', () => {
        this.materialModle.tableData.push({})
      })
    },
    importTable () {
      this.$on('import', list => {
        console.table(list)
        this.$set(this.materialModle, 'tableData', list)
      })
    },
    getCompanyObj (val, data) {
      console.log(val)
      const {
        materialId: itemId,
        materialCode: itemCode,
        materialName: itemDesc,
        categoryName
      } = val
      const temp = this.materialModle.tableData[data.$index]
      this.$set(this.materialModle.tableData, data.$index, {
        ...temp,
        itemDesc,
        itemId,
        itemCode,
        categoryName
      })
      this.completeInfo(data.$index)
    },
    treeselectChange (value, instanceId, scope) {
      const { organizationCode, organizationName, organizationId } = value
      scope.row.orgCode = organizationCode
      scope.row.orgId = organizationId
      scope.row.purchaseOrganization = organizationName
      this.completeInfo(scope.$index)
      console.log(this.materialModle.tableData[scope.$index])
    },
    dateChange (value) {
      this.completeInfo(this.currentIndex)
    },
    focus (scope) {
      this.currentIndex = scope.$index
    },
    numberChange (value) {
      const index = this.currentIndex
      const temp = this.materialModle.tableData[index]
      const { notaxPrice } = temp
      if (notaxPrice) {
        this.$set(this.materialModle.tableData, index, {
          ...temp,
          totalAmount: Number((notaxPrice * value).toFixed(2))
        })
      }
    },
    async completeInfo (index) {
      const temp = this.materialModle.tableData[index]
      const { itemId, orgId, requirementDate } = temp
      if (!itemId || !orgId || !requirementDate) return
      const info = await applicationAndAuditApi.getPriceLibraryByParam({
        materialId: itemId,
        organizationId: orgId,
        requirementDate
      })
      const { unit, notaxPrice, taxRate, taxKey, currency } = info.data
      this.$set(this.materialModle.tableData, index, {
        ...temp,
        // categoryName,
        unit,
        notaxPrice,
        taxRate,
        taxKey,
        currency
      })
    },
    checkChange (rows) {
      this.$emit('checkChange', rows)
    },
    deleteDetials (index, row) {
      this.materialModle.tableData.splice(index, 1)
      this.$emit('deleteDetials', index, row)
    }
  }
}
</script>
<style scoped lang="scss">
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
