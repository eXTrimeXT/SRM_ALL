<template>
  <el-form
    ref="scheduleForm"
    class="tableForm"
    :model="scheduleForm"
    :rules="scheduleRules"
    :show-message="false"
  >
    <el-table
      ref="elTable"
      :data="scheduleForm.scheduleList"
      style="width: 100%"
      border
      height="250px"
      @selection-change="checkChange"
    >
      <el-table-column
        v-if="checkbox"
        type="selection"
      />
      <!-- <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('purSettlementMod.tabindex')"
        width="50"
      /> -->
      <slot name="header" />
      <el-table-column
        fixed="left"
        align="center"
        prop="rowNum"
        :label="$t('purchaseDemand.lineNum')"
        width="60"
      />
      <template v-for="(col, key) in innerHeader">
        <!-- applyOperateFlag" ====="applyNotEmptyFlag" -->
        <el-table-column
          v-if="col[visibleFlagType] === 'Y'"
          :key="key"
          align="center"
          :prop="col.fieldCode"
          :label="col.fieldName"
          :min-width="col.width || 100"
          :show-overflow-tooltip="true"
        >
          <!-- <template slot="header" slot-scope="scope">
            <i class="toRequired">*</i>{{col.fieldName}}
          </template> -->
          <template slot-scope="scope">
            <template v-if="col.fieldCode === 'fromCountry'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.fromCountryCode'"
                :rules="scheduleRules.fromCountryCode"
              >
                <DictSelect
                  v-model="scope.row.fromCountryCode"
                  @change-value="(value, dictItem) => { countryChangeHandle(dictItem, scope.row) }"
                  code="country"
                  :disabled="isReadOnly"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'fromProvince'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.fromProvinceCode'"
                :rules="scheduleRules.fromProvinceCode"
              >
                <DictSelect
                  v-if="col[operateFlagType]=='Y'&&!isReadOnly"
                  v-model="scope.row.fromProvinceCode"
                  @change-value="(value, dictItem) => { provinceChangeHandle(dictItem, scope.row) }"
                  code="PROVINCE"
                  custom-select-type="PROVINCE"
                  :disabled="isReadOnly"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'fromCity'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.fromCityCode'"
                :rules="scheduleRules.fromCityCode"
              >
                <DictSelect
                  v-if="col[operateFlagType]=='Y'&&!isReadOnly"
                  v-model="scope.row.fromCityCode"
                  @change-value="(value, dictItem) => { cityChangeHandle(dictItem, scope.row) }"
                  :code="scope.row.fromProvinceCode"
                  custom-select-type="CITY"
                  :disabled="isReadOnly"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'fromCounty'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.fromCountyCode'"
                :rules="scheduleRules.fromCountyCode"
              >
                <el-select
                  v-model="scope.row.fromCountyCode"
                  clearable
                  filterable
                  :disabled="isReadOnly"
                  @change="
                    val => {
                      countyChangeHandle(val, scope.row);
                    }
                  "
                  @focus="
                    val => {
                      countyFocusHandle(val, scope.row);
                    }
                  "
                >
                  <el-option
                    v-for="item in scope.row.startCountyList"
                    :key="item.id"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'toCountry'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.toCountryCode'"
                :rules="scheduleRules.toCountryCode"
              >
                <DictSelect
                  v-model="scope.row.toCountryCode"
                  @change-value="(value, dictItem) => { toCountryChangeHandle(dictItem, scope.row) }"
                  code="country"
                  :disabled="isReadOnly"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'toProvince'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.toProvinceCode'"
                :rules="scheduleRules.toProvinceCode"
              >
                <DictSelect
                  v-model="scope.row.toProvinceCode"
                  @change-value="(value, dictItem) => { toProvinceChangeHandle(dictItem, scope.row) }"
                  code="PROVINCE"
                  custom-select-type="PROVINCE"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'toCity'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.toCityCode'"
                :rules="scheduleRules.toCityCode"
              >
                <DictSelect
                  v-model="scope.row.toCityCode"
                  @change-value="(value, dictItem) => { toCityChangeHandle(dictItem, scope.row) }"
                  :code="scope.row.fromProvinceCode"
                  custom-select-type="CITY"
                  :disabled="isReadOnly"
                  clearable
                  filterable
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'toCounty'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.toCountyCode'"
                :rules="scheduleRules.toCountyCode"
              >
                <el-select
                  v-model="scope.row.toCountyCode"
                  clearable
                  filterable
                  :disabled="isReadOnly"
                  @change="
                    val => {
                      toCountyChangeHandle(val, scope.row);
                    }
                  "
                  @focus="
                    val => {
                      toCountyFocusHandle(val, scope.row);
                    }
                  "
                >
                  <el-option
                    v-for="item in scope.row.endCountyList"
                    :key="item.id"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'fromPort'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.fromPort'"
                :rules="scheduleRules.fromPort"
              >
                <quick-search
                  :show-input="scope.row.fromPort"
                  show-key="portNameZhs"
                  :scope-data="scope.row"
                  name="ceea_logistics_port_info"
                  :disabled="isReadOnly"
                  @close-quicksearch="getPortObj"
                />
              </el-form-item>
            </template>
            <template v-else-if="col.fieldCode === 'toPort'">
              <el-form-item
                :prop="'scheduleList.' + scope.$index + '.toPort'"
                :rules="scheduleRules.toPort"
              >
                <quick-search
                  :show-input="scope.row.toPort"
                  show-key="portNameZhs"
                  :scope-data="scope.row"
                  name="ceea_logistics_port_info"
                  :disabled="isReadOnly"
                  @close-quicksearch="getPortObj2"
                />
              </el-form-item>
            </template>
            <template v-else>
              <el-input
                v-model="scope.row[col.fieldCode]"
                :disabled="isReadOnly"
              />
            </template>
          </template>
        </el-table-column>
      </template>
      <template v-if="!transportFlag">
        <el-table-column
          key="90"
          align="center"
          prop="wholeArk"
          label="FCL/LCL"
          min-width="150"
        >
          <!-- <template slot="header" slot-scope="scope">
        <i class="toRequired">*</i>FCL/LCL
      </template> -->
          <template slot-scope="scope">
            <el-form-item
              :prop="'scheduleList.' + scope.$index + '.wholeArk'"
              :rules="scheduleRules.wholeArk"
            >
              <el-select
                v-model="scope.row.wholeArk"
                :disabled="isReadOnly"
              >
                <el-option
                  v-for="item in paymentWayOpts"
                  :key="item.id"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column
          key="91"
          align="center"
          prop="mon"
          label="Mon"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.mon"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="92"
          align="center"
          prop="tue"
          label="Tue"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.tue"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="93"
          align="center"
          prop="wed"
          label="Wed"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.wed"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="94"
          align="center"
          prop="thu"
          label="Thu"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.thu"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="95"
          align="center"
          prop="fri"
          label="Fri"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.fri"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="96"
          align="center"
          prop="sat"
          label="Sat"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.sat"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="97"
          align="center"
          prop="sun"
          label="Sun"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.sun"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          key="98"
          align="center"
          prop="transitTime"
          label="Transit Time_PTP（Days）"
          min-width="220"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.transitTime"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 船公司/航空公司 -->
        <el-table-column
          key="99"
          align="center"
          prop="shipCompanyName"
          :label="$t('logisticsMod.shipCompanyName')"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.shipCompanyName"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 中转港/中转站 -->
        <el-table-column
          key="100"
          align="center"
          prop="transferPort"
          :label="$t('logisticsMod.transferPort')"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.transferPort"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
      </template>
      <template v-else>
        <!-- 车数 -->
        <el-table-column
          align="center"
          prop="charNum"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.carNum')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.charNum"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 兆瓦数 -->
        <el-table-column
          align="center"
          prop="megawatt"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.megawatt')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.megawatt"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 是否满足车源 -->
        <el-table-column
          align="center"
          prop="ifSatisfied"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.ifMeetCarSource')"
        >
          <template slot-scope="scope">
            <el-select
              v-model="scope.row.ifSatisfied"
              :disabled="isReadOnly"
            >
              <el-option
                v-for="item in yesNoOptions"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </template>
        </el-table-column>
        <!-- 不可满足原因 -->
        <el-table-column
          align="center"
          prop="satisfiableSituation"
          width="130"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.satisfiableSituation')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.satisfiableSituation"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 特殊说明 -->
        <el-table-column
          align="center"
          prop="specialInstructions"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.specialInstructions')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.specialInstructions"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 备注 -->
        <el-table-column
          align="center"
          prop="remarks"
          :show-overflow-tooltip="true"
          :label="$t('common.remark')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.remarks"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
        <!-- 评标结论 -->
        <el-table-column
          align="center"
          prop="bidResult"
          :show-overflow-tooltip="true"
          :label="$t('logisticsMod.bidResult')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.bidResult"
              :disabled="isReadOnly"
            />
          </template>
        </el-table-column>
      </template>
      <slot name="footer" />
    </el-table>
  </el-form>
</template>
<script>
import {
  getDictItem,
  getRegionBy,
  getRegion
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
    scheduleForm: {
      // 表数据
      type: Object,
      default: function () {
        return {
          scheduleList: []
        }
      }
    },
    checkbox: {
      type: Boolean,
      default: false
    },
    // 陆运、铁运
    transportFlag: {
      type: Boolean,
      default: false
    },
    checkChange: {
      type: Function,
      default: function () {
        return null
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
      paymentWayOpts: [],
      countryList: [],
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      scheduleRules: {
        fromCountryCode: [{ required: false }],
        toCountryCode: [{ required: false }],
        fromProvinceCode: [{ required: false }],
        fromCityCode: [{ required: false }],
        fromCountyCode: [{ required: false }],
        toProvinceCode: [{ required: false }],
        toCityCode: [{ required: false }],
        toCountyCode: [{ required: false }],
        fromPort: [{ required: false }],
        toPort: [{ required: false }],
        wholeArk: [{ required: false }]
      }
    }
  },
  watch: {
    tableHeader: {
      immediate: true,
      handler: function (n, o) {
        if (JSON.stringify(n) !== JSON.stringify(o)) {
          let fieldCodeList = [
            'FROM_COUNTRY',
            'FROM_PROVINCE',
            'FROM_CITY',
            'FROM_COUNTY',
            'FROM_PLACE',
            'TO_COUNTRY',
            'TO_PROVINCE',
            'TO_CITY',
            'TO_COUNTY',
            'TO_PLACE',
            'FROM_PORT',
            'TO_PORT'
          ]
          let arr = n.filter(i => fieldCodeList.includes(i.fieldCode))

          this.innerHeader = arr.map(({ fieldCode, ...rest }) => {
            // js 数据库字段转驼峰
            let str = fieldCode
              .toLowerCase()
              .replace(/_(\w)/g, function ($0, $1) {
                return $1.toUpperCase()
              })
            return {
              ...rest,
              fieldCode: str
            }
          })
          this.$nextTick(() => {
            this.$refs.elTable.doLayout() // 暂时解决表格错位的问题
          })
          // console.log('this.innerHeader--=', this.innerHeader.map(v=>v.fieldCode))
        }
      },
      deep: true
    }
  },
  created () {
    getDictItem('FCL /LCL').then(res => {
      this.paymentWayOpts = adaptDictData(res.data, 'dict')
    })
    // 加载国
    getRegionBy({ regionLevelCode: '1.0' }).then(res => {
      if (res.data) {
        this.countryList = this.adaptProvinceCity(res.data, 'country')
      }
    })
    getDictItem('LOGISTICS_CONFIG_COLUMNS').then(res => {
      if (this.tableHeader.length === 0) {
        this.tableHeader = adaptDictData(res.data, 'dict').map(val => ({
          fieldCode: val.value,
          fieldName: val.label,
          applyVisibleFlag: 'Y',
          purchaseVisibleFlag: 'Y',
          vendorVisibleFlag: 'Y',
          applyNotEmptyFlag: 'Y',
          applyOperateFlag: 'Y',
          comments: null,
          purchaseNotEmptyFlag: 'Y',
          purchaseOperateFlag: 'Y',
          templateLineId: null,
          vendorNotEmptyFlag: 'Y',
          vendorOperateFlag: 'Y'
        }))
      }
    })
  },
  methods: {
    validate (callback) {
      this.$refs.scheduleForm.validate(callback)
    },
    callback (col, row, scope) {
      if (col.callback) {
        col.callback(row, scope)
      }
    },
    // 适配省 市
    adaptProvinceCity (data, type) {
      let arr = []
      if (data && data.length > 0) {
        if (type === 'country') {
          // 省
          data.forEach(element => {
            arr.push({
              id: element.regionId.toString(),
              value: element.regionCode,
              label: element.regionName
            })
          })
        } else if (type === 'province') {
          // 省
          data.forEach(element => {
            arr.push({
              id: element.regionId.toString(),
              value: element.regionCode,
              label: element.regionName
            })
          })
        } else if (type === 'city') {
          // 市
          data.forEach(element => {
            arr.push({
              id: element.regionId.toString(),
              value: element.regionCode,
              label: element.regionName
            })
          })
        } else if (type === 'area') {
          // 县
          data.forEach(element => {
            arr.push({
              id: element.regionId.toString(),
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
    // 起发县
    countyChangeHandle (val, row) {
      let targeObj = row.startCountyList.find(i => i.value == val)
      row.fromCounty = targeObj.label
      this.scheduleForm.scheduleList.push({})
      this.scheduleForm.scheduleList.splice(
        this.scheduleForm.scheduleList.length - 1,
        1
      )
    },
    countyFocusHandle (val, row) {
      if (!row.fromCityCode) return
      let parame = {
        regionLevelCode: '4.0',
        parentRegionCode: row.fromCityCode
      }
      getRegionBy(parame).then(res => {
        if (res.data) {
          row.startCountyList = this.adaptProvinceCity(res.data, 'area')
          this.scheduleForm.scheduleList.push({})
          this.scheduleForm.scheduleList.splice(
            this.scheduleForm.scheduleList.length - 1,
            1
          )
        }
      })
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
    // 目的县
    toCountyChangeHandle (val, row) {
      let targeObj = row.endCountyList.find(i => i.value == val)
      row.toCounty = targeObj.label
    },
    toCountyFocusHandle (val, row) {
      if (!row.toCityCode) return
      let parame = { regionLevelCode: '4.0', parentRegionCode: row.toCityCode }
      getRegionBy(parame).then(res => {
        if (res.data) {
          row.endCountyList = this.adaptProvinceCity(res.data, 'area')
          this.scheduleForm.scheduleList.push({})
          this.scheduleForm.scheduleList.splice(
            this.scheduleForm.scheduleList.length - 1,
            1
          )
        }
      })
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
<style scoped>
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
