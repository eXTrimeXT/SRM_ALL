<template>
  <div>
    <p class="header-btn">
      <el-button
        v-if="!disabled && addButtonFlag=='Y'"
        type="primary"
        class="detail-pbtn"
        @click="addOneTableData2(index)"
      >
        {{ $t("common.new") }}
      </el-button>
    </p>
    <el-table
      :data="tableExtendListAll"
      style="width: 100%"
      border
      max-height="250px"
    >
      <!-- v-if="twoDimFlagData == []"  后面开发二维数组需要使用 -->
      <el-table-column
        v-if="!fixedBol"
        :key="index"
        :label="$t('contractMod.order')"
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        v-for="(col, indexss) in dimConfig"
        :key="col.formAttributeId"
        :prop="col.columnName"
        :label="col.languageCode ? $t(col.languageCode) : col.propertyName"
        :width="col.labelWidth ? col.labelWidth : ''"
      >
        <template slot-scope="scope">
          <!-- 如果是固定维度数据的情况 -->
          <div v-if="contrast(scope.row[col.columnName])">
            <!-- 如果是input类型 -->
            <div v-if="col.componentType == 'text'">
              {{ scope.row[col.columnName] }}
            </div>
            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="col.componentType == 'select'"
              v-model="scope.row[col.columnName]"
              disabled
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="selItem in selectList[indexss]"
                :key="selItem.id"
                :label="selItem.label"
                :value="selItem.value"
              />
            </el-select>
          </div>
          <!-- 如果不是固定维度数据的情况 -->
          <div v-else>
            <div v-if="col.fileuploadList != ''">
              <div
                v-for="(item,fileIndex) in col.fileuploadList"
                :key="fileIndex"
                class="download-link-wrap"
              >
                {{ $t('dashboard.uploadTemplate') }}
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: item.fileuploadId,
                    fileName: item.fileSourceName
                  }"
                  :readonly="true"
                />
              </div>
            </div>

            <!-- 上传附件 -->
            <div v-if="col.componentType == 'upload'">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row[col.columnName],
                  fileName: scope.row[col.columnName+'name']
                }"
                :readonly="false"
                @on-change="({file}) => handleUploadSuccess(file, scope.row,col.columnName,scope)"
              />
            </div>

            <!--组件类型为是否勾选框-->
            <div v-if="col.componentType == 'yesOrNo'">
              <el-checkbox
                v-model="scope.row[col.columnName]"
                :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
                true-label="Y"
                false-label="N"
              />
            </div>

            <!-- 组件类型为文本 -->
            <el-input
              v-if="col.componentType == 'text'"
              v-model="scope.row[col.columnName]"
              :maxlength="col.fieldLength"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />

            <!-- 组件类型为日期 -->
            <el-date-picker
              v-if="col.componentType == 'date'"
              v-model="scope.row[col.columnName]"
              type="date"
              :format="$formatDatePicker"
              :placeholder="$t('purchaseDemand.datePicker')"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />
            <!-- 组件类型为时间 -->
            <el-time-select
              v-if="col.componentType == 'dateTime'"
              v-model="scope.row[col.columnName]"
              :placeholder="$t('sourcingBuyer.timePlaceholder')"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />
            <!-- 组件类型为数字 -->
            <el-input
              v-if="col.componentType == 'number'"
              v-model="scope.row[col.columnName]"
              type="number"
              :max="col.fieldLength"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />

            <!-- 组件类型为多行文本框 -->
            <el-input
              v-if="col.componentType == 'textarea'"
              v-model="scope.row[col.columnName]"
              type="textarea"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />

            <!-- 组件类型为开关switch -->
            <el-switch
              v-if="col.componentType == 'switch'"
              v-model="scope.row[col.columnName]"
              active-value="Y"
              inactive-value="N"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
            />

            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="col.componentType == 'select'"
              v-model="scope.row[col.columnName]"
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="selItem in selectList[indexss]"
                :key="selItem.id"
                :label="selItem.label"
                :value="selItem.value"
              />
            </el-select>

            <!-- 组件类型为快查类型 -->
            <QuickSearch
              v-if="col.componentType == 'quickSearch'"
              :showInput="scope.row[col.columnName]"
              :show-key="col.quickSearchColumnShow"
              :scope-data="scope"
              :name="col.dictCode"
              @close-quicksearch="getVendorObj"
            />

            <!-- 组件类型为多选框 -->
            <el-checkbox-group
              v-if="col.componentType == 'checkbox'"
              v-model="scope.row[col.columnName]"
              :disabled="disabled"
            >
              <el-checkbox
                v-for="(checkboxItem) in checkboxAll[indexss]"
                :key="checkboxItem.id"
                :label="checkboxItem.label"
                :value="checkboxItem.value"
              />
            </el-checkbox-group>

            <!-- 组件类型为单选框 -->
            <el-radio-group
              v-if="col.componentType == 'radio'"
              v-model="scope.row[col.columnName]"
              :disabled="disabled"
            >
              <el-radio
                v-for="(radioItem, radioIndex) in selectList[indexss]"
                :key="radioIndex"
                :label="radioItem.label"
              >
                {{ radioItem.label }}
              </el-radio>
            </el-radio-group>
          </div>
        </template>
      </el-table-column>
      <!-- 删除 -->
      <el-table-column v-if="!dimConfig[0].rowAttribute || dimConfig[0].rowAttribute == ''" fixed="right" :label="$t('common.operation')" width="60">
        <template slot-scope="scope">
          <el-button
            type="text"
            :disabled="disabled"
            @click="handleDelClickCustomer(scope.$index, scope.row)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- <twoDimFlag
      v-if="twoDimFlagData != []"
    ></twoDimFlag> -->
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import QuickSearch from 'lib@/components/QuickSearch'
import TwoDimFlag from './twoDimFlag'

export default {
  name: 'ModelConfigTable',
  components: {
    QuickSearch,
    TwoDimFlag
  },
  props: {
    addButtonFlag: {
      type: [Boolean, String],
      default: () => 'Y'
    },
    dimConfig: {
      type: Array,
      default: () => {
        return []
      }
    },
    disabled: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    addOneTableData: {
      type: Function,
      default: () => {
        return null
      }
    },
    index: {
      type: Number,
      default: () => {
        return null
      }
    },
    tableExtendList: {
      type: Array,
      default: () => {
        return []
      }
    },
    tableValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    rules: {
      type: Object,
      default: () => {
        return {}
      }
    },
    refs: {
      type: String,
      default: () => {
        return ''
      }
    },
    dimCode: { // 该项目的dimCode
      type: String,
      default: () => {
        return ''
      }
    }
  },
  data () {
    return {
      // disabled: false,
      fixedBol: false,
      selectList: [],
      checkboxAll: [],
      model: [],
      tableExtendListAll: [],
      rowAttribute: [], // 固定维度列表
      twoDimFlagData: [],
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'accessFlowSetting',
        fileType: 'images'
      },
      form: {

      }
    }
  },
  watch: {
    tableValue: {
      handler () {
        // this.tableExtendList[this.index] = this.tableValue;
        try {
          this.initialization()
          // eslint-disable-next-line no-empty
        } catch (e) {}
      },
      deep: true
    },
    dimConfig: {
      handler () {
        this.fatchDictData() // 加载数据字典
      },
      deep: true
    },
    tableExtendListAll: {
      immediate: true,
      handler () {
        let models = this.tableExtendListAll
        let attr = []
        models.forEach(datas => {
          for (let key in datas) {
            // console.log(key + "---" + datas[key]);
            let obj = {
              columnName: key,
              fieldValue: datas[key],
              dimFieldNum: this.index
            }
            attr.push(obj)
          }
        })
        // console.log(attr);
        let attrNew = [{}]
        attr.forEach((datas, index1) => {
          let bol = false
          let index3 = 0
          attrNew.forEach((datas2, index2) => {
            if (attrNew[index2].columnName == attr[index1].columnName) {
              bol = true
              index3 = index2
            }
          })
          if (bol == false) {
            attrNew.push(attr[index1])
          } else {
            let str = attrNew[index3].fieldValue
            attrNew[index3].fieldValue = str + ',' + attr[index1].fieldValue
          }
        })
        attrNew.shift()
        this.model = attrNew
        // console.log(this.model);
      },
      deep: true
    }
  },
  mounted () {
    this.fatchDictData()
    try {
      this.initialization()
      // eslint-disable-next-line no-empty
    } catch (e) {}
  },
  created () {
    let dimConfig = this.dimConfig
    let twoDimFlagData = []
    let tableExtendListAll = []
    dimConfig.forEach(datas => {
      if (datas.rowAttribute && datas.rowAttribute !== '') {
        console.log(datas)
        this.fixedBol = true
        let rowAttribute = []
        let codes = datas.columnName
        rowAttribute = datas.rowAttribute.split(',')
        this.rowAttribute = rowAttribute
        rowAttribute.forEach(elem => {
          tableExtendListAll.push({
            [codes]: elem
          })
        })
      }
      if (datas.twoDimFlag == 'Y') {
        twoDimFlagData.push(datas)
      }
    })
    // this.$set(this.tableExtendListAll,tableExtendListAll)
    this.tableExtendListAll = tableExtendListAll

    this.twoDimFlagData = twoDimFlagData

    try {
      if (JSON.stringify(this.tableExtendList[this.index][0]) != '{}') {
        this.tableExtendListAll = this.tableExtendList[this.index]
      }
      // eslint-disable-next-line no-empty
    } catch (error) {

    }
  },
  methods: {
    getDataValue () {
      let resutList = []
      if (this.dimConfig && this.tableExtendListAll && this.tableExtendListAll.length > 0) {
        this.dimConfig.forEach(item => {
          let resultItem = {
            columnName: item.columnName,
            propertyName: item.propertyName,
            formPageId: item.formPageId,
            dimId: item.dimId,
            formAttributeId: item.formAttributeId,
            fieldValue: ''
          }
          this.tableExtendListAll.forEach((valueItem) => {
            let value = valueItem[item.columnName] ? valueItem[item.columnName] : ''
            resultItem['fieldValue'] = resultItem['fieldValue'] + ',' + value
          })
          resultItem['fieldValue'] = resultItem['fieldValue'].slice(1)
          resutList.push(resultItem)
        })
      }
      return resutList
    },
    // 确认快查后
    getVendorObj (val, scope) {
      const name = scope.column.property
      console.log(this.dimConfig)
      let quickSearchName = ''
      let quickSearchColumnShow = ''
      let dimConfig = this.dimConfig
      dimConfig.forEach(datas => {
        if (datas.quickSearchColumn != '') {
          quickSearchName = datas.quickSearchColumn
          quickSearchColumnShow = datas.quickSearchColumnShow
        }
      })
      scope.row[name] = val ? val[quickSearchName] : ''
    },
    evalF (val) {
      // eslint-disable-next-line no-eval
      return eval(val)
    },

    addOneTableData2 () {
      this.tableExtendListAll.push({})
    },
    contrast (data) {
      let fixData = this.rowAttribute
      let bol = false
      fixData.forEach(element => {
        if (element == data) {
          bol = true
        }
      })
      return bol
    },
    // 上传附件成功
    handleUploadSuccess (file, row, col, scope) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        this.$set(row, col, fileId.toString())
        this.$set(row, col + 'name', fileName)
      } else {
        this.$set(row, col, '')
        this.$set(row, col + 'name', '')
      }
    },
    // 行删除
    handleDelClickCustomer (indexs, row) {
      this.tableExtendListAll.splice(indexs, 1)
    },
    fatchDictData () {
      this.selectList = []
      this.checkboxAll = []
      let _this = this
      async function fors () {
        for (let i = 0; i < _this.dimConfig.length; i++) {
          // 如果是下拉框或者单选框的时候请求字典
          // console.log(element);
          if (
            _this.dimConfig[i].componentType == 'select' ||
            _this.dimConfig[i].componentType == 'radio'
          ) {
            let res = await getDictItem(_this.dimConfig[i].dictCode)
            let res2 = await adaptDictData(res.data, 'dict')
            _this.selectList.push(res2)
            _this.checkboxAll.push({})
          } else if (_this.dimConfig[i].componentType == 'checkbox') {
            // 如果是多选框的时候请求字典
            let resA = await getDictItem(_this.dimConfig[i].dictCode)
            _this.checkboxAll.push(adaptDictData(resA.data, 'dict'))
            _this.selectList.push({})
          } else {
            _this.selectList.push({})
            _this.checkboxAll.push({})
          }
        }
      }
      fors()
    },
    // 点击暂存提交的时候回传给父级页面
    saveAndSubmit () {
      return this.tableExtendList
    },
    // 加载初始化
    initialization () {
      if (!(this.tableValue && this.tableValue.length > 0)) {
        return false
      }
      let count = 0
      let tempTableDataObj = {}
      if (this.dimConfig && this.dimConfig.length > 0) {
        let dimItem = this.dimConfig[0]
        this.tableValue.forEach((datas) => {
          if (datas.dimId === dimItem.dimId) {
            // count = datas.fieldValue ? datas.fieldValue.split(',').length : 0
            count = datas.fieldValue.split(',').length
            tempTableDataObj[datas.columnName] = datas.fieldValue.split(',')
          }
        })
      }

      if (count > 0) {
        this.tableExtendListAll = []
      }
      for (var num = 0; num < count; num++) {
        let row = {}
        Object.keys(tempTableDataObj).forEach(key => {
          row[key] = tempTableDataObj[key][num]
        })
        this.tableExtendListAll.push(row)
      }
      this.$forceUpdate()
    }
  }
}
</script>

<style lang="scss">
.header-btn{
  margin: 0 0 8px 0;
}
.el-collapse-item__wrap{
  border-bottom:none;
}
</style>
