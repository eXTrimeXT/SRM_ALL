<template>
  <div class="wrapper">
    <el-form ref="requireForm" :model="requireInfo" :rules="requireRules">
      <SrmRow>
        <SrmCol :init-col="4">
          <el-form-item label="一级分类" prop="oneCategoryName">
            <QuickSearch
              :disabled="readonly"
              :show-input="requireInfo.oneCategoryName"
              show-key="categoryName"
              :scope-data="requireInfo"
              name="scc_base_purchase_category5"
              :pre-query-data="{
                't.LEVEL': 1
              }"
              multiSelect
              @close-quicksearch="getOneCatObj"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="二级分类" prop="twoCategoryName">
            <QuickSearch
              :disabled="readonly"
              :show-input="requireInfo.twoCategoryName"
              show-key="categoryName"
              :scope-data="requireInfo"
              name="scc_base_purchase_category5"
              :pre-query-data="{
                't.LEVEL': 2,
                parentIds: oneLevel.join(',')
              }"
              multiSelect
              @close-quicksearch="getTwoCatObj"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- supList -->
          <el-form-item label="供应商名称" prop="supName">
            <QuickSearch
              :disabled="readonly"
              :show-input="requireInfo.supName"
              show-key="companyName"
              :scope-data="requireInfo"
              name="scc_sup_company_info_display"
              multiSelect
              @close-quicksearch="getSupObj"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- orgList -->
          <!-- 公司 -->
          <el-form-item label="申请单位" prop="orgName">
            <OrganizationSelector
              ref="organizationSelector"
              v-model="requireInfo.orgName"
              :disabled="readonly"
              :parent-id="-1"
              node-type="OU"
              multiple
              :placeholder="$t('common.pleaseSelect')"
              @select="selectHandler"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- personList -->
          <el-form-item label="采购员" prop="personName">
            <QuickSearch
              :disabled="readonly"
              :showInput="requireInfo.personName"
              show-key="nickname"
              :scope-data="requireInfo"
              name="scc_rbac_user_display"
              multiSelect
              @close-quicksearch="getBuyerPersonObj"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="requireInfo.brand" :disabled="readonly" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- materialList -->
          <el-form-item label="物资名称" prop="materialName">
            <QuickSearch
              :disabled="readonly"
              :showInput="requireInfo.materialName"
              :scope-data="requireInfo"
              name="scc_base_material_item_contract"
              multiSelect
              @close-quicksearch="getMaterialObj"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <!-- orderStatusList -->
          <el-form-item label="订单状态" prop="orderStatusList">
            <DictSelect
              v-model="requireInfo.orderStatusList"
              :disabled="readonly"
              code="PURCHASE_ORDER"
              multiple
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="单项物资订单数" prop="orderNum">
            <el-input v-model="requireInfo.orderNum" :disabled="readonly">
              <DictSelect
                slot="prepend"
                v-model="requireInfo.orderNumType"
                :disabled="readonly"
                class="input-prepend"
                code="DESIGN_PLAN_COMPARE"
              />
            </el-input>
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="单项物资采购金额（未税）" prop="buyMoney">
            <el-input v-model="requireInfo.buyMoney" :disabled="readonly">
              <DictSelect
                slot="prepend"
                v-model="requireInfo.buyMoneyType"
                :disabled="readonly"
                class="input-prepend"
                code="DESIGN_PLAN_COMPARE"
              />
            </el-input>
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="上年订单日期从" prop="lastYearOrderDateStart">
            <el-date-picker
              v-model="requireInfo.lastYearOrderDateStart"
              :disabled="readonly"
              type="date"
              :value-format="formatDate"
              :format="formatDate"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="上年订单日期到" prop="lastYearOrderDateEnd">
            <el-date-picker
              v-model="requireInfo.lastYearOrderDateEnd"
              :disabled="readonly"
              type="date"
              :value-format="formatDate"
              :format="formatDate"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="上上年订单日期从" prop="lastLastYearOrderDateStart">
            <el-date-picker
              v-model="requireInfo.lastLastYearOrderDateStart"
              :disabled="readonly"
              type="date"
              :value-format="formatDate"
              :format="formatDate"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="4">
          <el-form-item label="上上年订单日期到" prop="lastLastYearOrderDateEnd">
            <el-date-picker
              v-model="requireInfo.lastLastYearOrderDateEnd"
              :disabled="readonly"
              type="date"
              :value-format="formatDate"
              :format="formatDate"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>
    <div v-if="!readonly" class="btns">
      <el-button type="primary" @click="pullOrderData(1)">
        拉取1
      </el-button>
      <el-button style="margin-left:10px;" type="primary" @click="pullOrderData(2)">
        拉取2
      </el-button>
    </div>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      formatDate: 'yyyy-MM-dd',
      requireInfo: {
        oneCategoryName: null,
        twoCategoryName: null,
        supName: null,
        orgName: null,
        personName: null,
        brand: null,
        materialName: null,
        orderStatusList: [],
        orderNum: null,
        orderNumType: null,
        buyMoney: null,
        buyMoneyType: null,
        lastYearOrderDateStart: null,
        lastYearOrderDateEnd: null,
        lastLastYearOrderDateStart: null,
        lastLastYearOrderDateEnd: null
      },
      requireRules: {
        lastYearOrderDateStart: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        lastYearOrderDateEnd: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        lastLastYearOrderDateStart: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        lastLastYearOrderDateEnd: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }]
      },
      // 供应商编码列表
      supList: [],
      // 公司编码列表
      orgList: [],
      // 采购员id列表
      personList: [],
      // 物料编码列表
      materialList: [],
      // 一级品类
      oneLevel: [],
      // 二级品类
      twoLevel: []
    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    },
    queryParams () {
      const {
        brand, orderStatusList, orderNum, orderNumType, buyMoney, buyMoneyType,
        lastYearOrderDateStart, lastYearOrderDateEnd, lastLastYearOrderDateStart, lastLastYearOrderDateEnd
      } = this.requireInfo
      // 默认取二级品类的id，没有取一级品类
      let categoryIds = this.twoLevel.length ? this.twoLevel : this.oneLevel
      let params = {
        categoryIds,
        supList: this.supList,
        orgList: this.orgList,
        personList: this.personList,
        brand,
        materialList: this.materialList,
        orderStatusList,
        orderNum,
        orderNumType,
        buyMoney,
        buyMoneyType,
        lastYearOrderDateStart,
        lastYearOrderDateEnd,
        lastLastYearOrderDateStart,
        lastLastYearOrderDateEnd
      }
      return params
    }
  },
  methods: {
    getOneCatObj (val) {
      if (val && val.length) {
        this.requireInfo.oneCategoryName = val.map(item => item.categoryName).join(';')
        this.oneLevel = val.map(item => item.categoryId)
      } else {
        this.requireInfo.oneCategoryName = ''
        this.oneLevel = []
      }
    },
    getTwoCatObj (val) {
      if (val && val.length) {
        this.requireInfo.twoCategoryName = val.map(item => item.categoryName).join(';')
        this.twoLevel = val.map(item => item.categoryId)
      } else {
        this.requireInfo.twoCategoryName = ''
        this.twoLevel = []
      }
    },
    // 申请单位（公司）
    selectHandler (val) {
      console.log('val', val)
      if (val && val.length) {
        this.orgList = val.map(item => item.organizationCode)
      } else {
        this.orgList = []
      }
    },
    // 采购组织选择
    orgSelect (node, value, scope) {
      console.log('node', node)
      if (node.length) {
        this.baseForm.sccSouJcAgreementOrgList = node.map(item => ({
          buyOrgId: item.organizationId,
          buyOrgCode: item.organizationCode,
          buyOrgName: item.organizationName
        }))
      }
    },
    // 供应商快查
    getSupObj (val, scope) {
      console.log('val', val)
      if (val && val.length > 0) {
        this.requireInfo.supName = val.map(item => item.companyName).join(';')
        this.supList = val.map(item => item.companyCode)
      } else {
        this.requireInfo.supName = ''
        this.supList = []
      }
    },
    // 采购员快查
    getBuyerPersonObj (val, scope) {
      console.log('val', val)
      if (val && val.length > 0) {
        this.requireInfo.personName = val.map(item => item.nickname).join(';')
        this.personList = val.map(item => item.userId)
      } else {
        this.requireInfo.personName = ''
        this.personList = []
      }
    },
    // 物资名称
    getMaterialObj (val, scope) {
      console.log('val', val)
      if (val && val.length > 0) {
        this.requireInfo.materialName = val.map(item => item.materialName).join(';')
        this.materialList = val.map(item => item.materialCode)
      } else {
        this.requireInfo.materialName = ''
        this.materialList = []
      }
    },
    pullOrderData (type) {
      this.$refs.requireForm.validate(valid => {
        if (valid) {
          this.$emit('pull-order', type, this.queryParams)
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.input-prepend {
  width: 80px !important;
}
.ml-10 {
  margin-left: 10px;
}
.btns {
  text-align: right;
  margin-bottom: 10px;
}
</style>
<style>
.el-input-group__append, .el-input-group__prepend {
  background-color: #fff !important;
}
</style>
