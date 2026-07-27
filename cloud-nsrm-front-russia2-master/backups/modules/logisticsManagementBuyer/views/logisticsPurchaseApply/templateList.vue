<template>
  <div>
    <template v-for="(col, key) in innerHeader">
      <!-- applyOperateFlag" ====="applyNotEmptyFlag" -->
      <el-table-column
        v-if="col[visibleFlagType]==='Y'"
        :key="key"
        align="center"
        :prop="col.fieldCode"
        :label="col.fieldName"
        :min-width="col.width ||120"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired" v-if="col.purchaseNotEmptyFlag ==='Y'">*</i>{{ col.fieldName }}
        </template>
        <template slot-scope="scope">
          <template v-if="col.showType === 'select'">
            <el-select
              v-model="scope.row[col.prop]"
              clearable
              :type="col.inputType || 'string'"
              :disabled="isReadOnly || col[operateFlagType]!=='Y'"
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in col.options"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </template>
          <template v-else-if="col.fieldCode === 'logisticsCategoryCode'">
            <c-category-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.logisticsCategoryName"
              :scope="scope.row"
              category-type="logistics"
              show-key="categoryName"
              @select="comfirmSelect"
            />
            <span v-else>{{ scope.row.logisticsCategoryName }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'expenseItem'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.expenseItem"
              clearable
              @focus="(val) => { changeLeg(scope.row.leg,scope.row) }"
              @change="(val) => { chargeExpenseItem(val,scope.row) }"
            >
              <el-option
                v-for="item in scope.row.expenseItemList"
                :key="item.id"
                :label="item.chargeName"
                :value="item.chargeCode"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(expenseItemList,scope.row.expenseItem) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'chargeMethod'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.chargeMethod"
              clearable
              @change="(val) => { chargeMethods(val,scope.row) }"
            >
              <el-option
                v-for="item in chargeMethodList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(chargeMethodList,scope.row.chargeMethod) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'leg'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.leg"
              clearable
              @change="(val) => { changeLeg(val,scope.row) }"
            >
              <el-option
                v-for="item in legCodeList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(legCodeList,scope.row.leg) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'fromCountry'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.fromCountryCode"
              @change-value="(value, dictItem) => { countryChangeHandle(dictItem, scope.row) }"
              code="country"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.fromCountry }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'fromProvince'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.fromProvinceCode"
              @change-value="(value, dictItem) => { provinceChangeHandle(dictItem, scope.row) }"
              @focus="provinceFocusHandle(scope.row)"
              code="PROVINCE"
              custom-select-type="PROVINCE"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.fromProvince }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'fromCity'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.fromCityCode"
              @change-value="(value, dictItem) => { cityChangeHandle(dictItem, scope.row) }"
              :code="scope.row.fromProvinceCode"
              custom-select-type="CITY"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.fromCity }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'fromCounty'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.fromCountyCode"
              clearable
              filterable
              @change="(val) => { countyChangeHandle(val,scope.row) }"
            >
              <el-option
                v-for="item in scope.row.startCountyList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ scope.row.fromCounty }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'toCountry'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.toCountryCode"
              @change-value="(value, dictItem) => { toCountryChangeHandle(dictItem, scope.row) }"
              code="country"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.toCountry }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'toProvince'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.toProvinceCode"
              @change-value="(value, dictItem) => { toProvinceChangeHandle(dictItem, scope.row) }"
              @focus="toProvinceFocusHandle(scope.row)"
              code="PROVINCE"
              custom-select-type="PROVINCE"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.toProvince }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'toCity'">
            <DictSelect
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.toCityCode"
              @change-value="(value, dictItem) => { toCityChangeHandle(dictItem, scope.row) }"
              :code="scope.row.toProvinceCode"
              custom-select-type="CITY"
              clearable
              filterable
            />
            <span v-else>{{ scope.row.toCity }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'toCounty'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.toCountyCode"
              clearable
              filterable
              @change="(val) => { toCountyChangeHandle(val,scope.row) }"
            >
              <el-option
                v-for="item in scope.row.endCountyList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ scope.row.toCounty }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'currency'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.currency"
              clearable
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in currencyList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(currencyList,scope.row.currency) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'chargeUnit'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.chargeUnit"
              clearable
              @focus="(val) => { chargeMethods(scope.row.chargeMethod,scope.row) }"
              @change="(val) => { chargeMethodss(val,scope.row) }"
            >
              <el-option
                v-for="item in scope.row.unitList"
                :key="item.id"
                :label="item.chargeUnitName"
                :value="item.chargeUnit"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(unitList,scope.row.chargeUnit) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'wholeArk'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.wholeArk"
              clearable
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in wholeArkList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(wholeArkList,scope.row.wholeArk) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'ifBack'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.ifBack"
              clearable
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in yesNoOptions"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(yesNoOptions,scope.row.ifBack) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'tradeTerm'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.tradeTerm"
              clearable
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in tradeTermList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(tradeTermList,scope.row.tradeTerm) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'importExportMethod'">
            <el-select
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row.importExportMethod"
              clearable
              @change="callback(col, scope.row, scope)"
            >
              <el-option
                v-for="item in importExportMethodList"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <span v-else>{{ $getDictLabelByValue(importExportMethodList,scope.row.importExportMethod) }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'specifiedVendor'">
            <quick-search
              :show-input="scope.row.specifiedVendor"
              show-key="vendorCode"
              :scope-data="scope.row"
              name="scc_sup_company_info"
            />
          </template>
          <template v-else-if="col.fieldCode === 'fromPort'">
            <quick-search
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              :show-input="scope.row.fromPort"
              show-key="portNameZhs"
              :scope-data="scope.row"
              name="ceea_logistics_port_info"
              @close-quicksearch="getPortObj"
            />
            <span v-else>{{ scope.row.fromPort }}</span>
          </template>
          <template v-else-if="col.fieldCode === 'toPort'">
            <quick-search
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              :show-input="scope.row.toPort"
              show-key="portNameZhs"
              :scope-data="scope.row"
              name="ceea_logistics_port_info"
              @close-quicksearch="getPortObj2"
            />
            <span v-else>{{ scope.row.toPort }}</span>
          </template>
          <template v-else-if="['expense', 'singleKmCost', 'singleDragCost', 'beyondBoxCost', 'beyondStorageCost'].includes(col.fieldCode)">
            <el-input
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row[col.fieldCode]"
              v-input-format="{ type: 'float' }"
            />
            <span v-else>{{ scope.row[col.fieldCode] }}</span>
          </template>
          <template v-else>
            <el-input
              v-if="col[operateFlagType]=='Y'&&!isReadOnly"
              v-model="scope.row[col.fieldCode]"
            />
            <span v-else>{{ scope.row[col.fieldCode] }}</span>
          </template>
        </template>
      </el-table-column>
    </template>
  </div>
</template>
<script>
import {
  getDictItem,
  getAllPurCurrency,
  // getRegionBy,
  // getRegion
} from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'

  export default {
    name: 'TemplateList',
    components: {
      QuickSearch,
      CCategorySelect
    },
    props: {
      tableHeader: {
        // 表头数据
        type: Array,
        default: function () {
          return []
        }
      },
      requirementLineList: {
        // 表数据
        type: Array,
        default: function () {
          return []
        }
      },
      // 申请头
      requirementHead: {
        // 表数据
        type: Object,
        default: function () {
          return {}
        }
      },
      isReadOnly: {
        // 订单头数据
        type: Boolean,
        default: function () {
          return false
        }
      },
      operateFlagType: {
        // 是否可操作
        type: String,
        default: function () {
          return 'applyOperateFlag'
        }
      },
      visibleFlagType: {
        // 是否显示
        type: String,
        default: function () {
          return 'applyVisibleFlag'
        }
      }
    },
    data () {
      return {
        innerHeader: [],
        unitList: [],
        currencyList: [],
        legCodeList: [],
        expenseItemList: [],
        tradeTermList: [],
        wholeArkList: [],
        chargeMethodList: [],
        importExportMethodList: [],
        yesNoOptions: [{ value: 'Y', label: '是' }, { value: 'N', label: '否' }]
      }
    },
    watch: {
      tableHeader: {
        immediate: true,
        handler: function (n, o) {
          if (JSON.stringify(n) !== JSON.stringify(o)) {
            this.innerHeader = n.map(({ fieldCode, ...rest }) => {
              // js 数据库字段转驼峰
              let str = fieldCode.toLowerCase().replace(/_(\w)/g, function ($0, $1) {
                return $1.toUpperCase()
              })
              return {
                ...rest,
                fieldCode: str
              }
            })
            // console.log('this.innerHeader--=', this.innerHeader.map(v=>v.fieldCode))
          }
        },
        deep: true
      }
    },
    created () {
      getDictItem('LEG').then(res => {
        this.legCodeList = adaptDictData(res.data, 'dict')
      })
      getDictItem('SUB_LEVEL').then(res => {
        this.unitList = adaptDictData(res.data, 'dict')
      })
      getDictItem('CHARGE_NAME').then(res => {
        this.expenseItemList = adaptDictData(res.data, 'dict')
      })
      getDictItem('CHARGE_LEVEL').then(res => {
        this.chargeMethodList = adaptDictData(res.data, 'dict')
      })
      // 贸易术语
      getDictItem('TRADE_TERM').then(res => {
        this.tradeTermList = adaptDictData(res.data, 'dict')
      })
      // 整柜/拼柜
      getDictItem('FCL /LCL').then(res => {
        this.wholeArkList = adaptDictData(res.data, 'dict')
      })
      // 进出口方式 IMPORT_EXPORT_METHOD
      getDictItem('EXP/IMP').then(res => {
        this.importExportMethodList = adaptDictData(res.data, 'dict')
      })
      // 获取所有币种
      getAllPurCurrency().then(res => {
        this.currencyList = adaptDictData(res.data, 'currency')
      })
      // 加载国
      // getRegionBy({ regionLevelCode: '1.0' }).then(res => {
      //   if (res.data) {
      //     this.countryList = this.adaptProvinceCity(res.data, 'country')
      //   }
      // })
      // // 加载省
      // getRegion({ queryType: 'province' }).then(res => {
      //   if (res.data) {
      //     this.provinceList = this.adaptProvinceCity(res.data, 'province')
      //   }
      // });
    //   getDictItem("LOGISTICS_CONFIG_COLUMNS").then(res => {
    //     if(this.tableHeader.length ===0) {
    //       this.tableHeader = adaptDictData(res.data, "dict").map(val=>({
    //         fieldCode: val.value,
    //         fieldName: val.label,
    //         "applyNotEmptyFlag": "Y",
    //         "applyOperateFlag": "N",
    //         "comments": null,
    //         "purchaseNotEmptyFlag": "Y",
    //         "purchaseOperateFlag": "N",
    //         "templateLineId": null,
    //         "vendorNotEmptyFlag": "Y",
    //         "vendorOperateFlag": "N",
    //         applyVisibleFlag: 'Y',
    //         purchaseVisibleFlag: 'Y',
    //         vendorVisibleFlag: 'Y',
    //       }));
    //     }
    //   });
    },
    methods: {
      updateTemplate (list) {
        this.tableHeader = list
      },
      callback (col, row, scope) {
        if (col.callback) {
          col.callback(row, scope)
        }
      },
      // 根据leg查费项
      changeLeg (val, row) {
        this.$http({
          url:
            '/api-pd/logistics/expense-item/queryChargeCodeDtoBy',
          method: 'get',
          params: {
            legCode: val || '',
            transportModeCode: this.requirementHead.transportModeCode,
            businessModeCode: this.requirementHead.businessModeCode
          }
        }).then(res => {
          if (row.expenseItem) {
            row.expenseItem = null
            row.expenseItemName = null
          }
          row.expenseItemList = res.data
        })
      },
      chargeExpenseItem (val, row) {
        let targe = this.expenseItemList.find(i => val == i.value)
        if (targe) {
          row.expenseItemName = targe.label
        }
      },
      // 根据计费方式查计费单位
      chargeMethods (val, row) {
        this.$http({
          url:
            '/api-base/dict/base-dict/queryBillingCombination',
          method: 'get',
          params: {
            chargeMethod: val
          }
        }).then(res => {
          if (row && row.chargeUnit) {
            row.chargeUnit = null
            row.chargeUnitName = null
          }
          row.unitList = res.data
        })
      },
      chargeMethodss  (val, row) {
        let targe = this.unitList.find(i => val == i.value)
        if (targe) {
          row.chargeUnitName = targe.label
        }
      },
      // 确认选中的品类
      comfirmSelect (node, scope) {
        scope.logisticsCategoryId = node ? node.categoryId : null
        scope.logisticsCategoryName = node ? node.categoryName : ''
        scope.logisticsCategoryCode = node ? node.categoryCode : ''
      },
      // 适配省 市
      adaptProvinceCity (data, type) {
        let arr = []
        if (data && data.length > 0) {
          if (type === 'country') { // 省
            data.forEach(element => {
              arr.push({
                id: (element.regionId).toString(),
                value: element.regionCode,
                label: element.regionName
              })
            })
          } else if (type === 'province') { // 省
            data.forEach(element => {
              arr.push({
                id: (element.regionId).toString(),
                value: element.regionCode,
                label: element.regionName
              })
            })
          } else if (type === 'city') { // 市
            data.forEach(element => {
              arr.push({
                id: (element.regionId).toString(),
                value: element.regionCode,
                label: element.regionName
              })
            })
          } else if (type === 'area') { // 县
            data.forEach(element => {
              arr.push({
                id: (element.regionId).toString(),
                value: element.regionCode,
                label: element.regionName
              })
            })
          }
        }
        return arr
      },
      // 国家下拉加载省
      countryChangeHandle (dictItem, row) {
        row.fromCountry = dictItem.label
        row.fromProvince = null
        row.fromProvinceCode = null
        row.fromCity = null
        row.fromCityCode = null
        row.startCityList = []
        row.fromCounty = null
        row.fromCountyCode = null
        row.startCountyList = []
      },
      // 省下拉加载市
      provinceChangeHandle (dictItem, row) {
        row.fromProvince = dictItem.label
        row.fromCity = null
        row.fromCityCode = null
        row.fromCounty = null
        row.fromCountyCode = null
        row.startCountyList = []
      },
      // 市下拉加载县
      cityChangeHandle (dictItem, row) {
        row.fromCity = dictItem.label
        row.fromCounty = null
        row.fromCountyCode = null
      },
      provinceFocusHandle (row) {
        if (this.requirementHead.templateCode !== 'MD21031005' && !row.fromCountryCode) {
          return
        }
        if (this.requirementHead.templateCode === 'MD21031005') {
          row.fromCountryCode = 'CN'
        }
      },
      // 起发县
      countyChangeHandle (val, row) {
        let targeObj = row.startCountyList.find(i => i.value == val)
        row.fromCounty = targeObj.label
      },
      // 到达国家下拉加载省
      toCountryChangeHandle (dictItem, row) {
        row.toCountry = dictItem.label
        row.toProvince = null
        row.toProvinceCode = null
        row.toCity = null
        row.toCityCode = null
        row.endCityList = []
        row.toCounty = null
        row.toCountyCode = null
        row.endCountyList = []
      },
      // 到达省下拉加载市
      toProvinceChangeHandle (dictItem, row) {
        row.toProvince = dictItem.label
        row.toCity = null
        row.toCityCode = null
        row.toCounty = null
        row.toCountyCode = null
        row.endCountyList = []
      },
      // 到达市下拉加载县
      toCityChangeHandle (dictItem, row) {
        row.toCity = dictItem.label
        row.toCounty = null
        row.toCountyCode = null
      },
      toProvinceFocusHandle (row) {
         if (this.requirementHead.templateCode !== 'MD21031005' && !row.toCountryCode) {
          return
        }
        if (this.requirementHead.templateCode === 'MD21031005') {
          row.toCountryCode = 'CN'
        }
      },
      // 目的县
      toCountyChangeHandle (val, row) {
        let targeObj = row.endCountyList.find(i => i.value == val)
        row.toCounty = targeObj.label
      },
      // 起始港
      getPortObj (val, scope) {
        scope.fromPortId = val.portId
        scope.fromPortCode = val.portCode
        scope.fromPort = val.portNameZhs
      },
      // 目的港
      getPortObj2 (val, scope) {
        scope.toPortId = val.portId
        scope.toPortCode = val.portCode
        scope.toPort = val.portNameZhs
      }
    }
  }
</script>
<style scoped lang="scss">
.toRequired {
  color: #ff4949;
  margin-right: 4px;
}
</style>
