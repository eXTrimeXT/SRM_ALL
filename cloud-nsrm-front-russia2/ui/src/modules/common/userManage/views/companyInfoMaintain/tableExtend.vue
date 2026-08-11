<template>
  <div>
    <p>
      <el-button
        v-if="!disabled"
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
      :disabled="disabled"
    >
      <!-- v-if="twoDimFlagData == []"  后面开发二维数组需要使用 -->
      <el-table-column
        v-if="!fixedBol"
        :key="index"
        :label="$t('common.sort')"
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        v-for="(col, indexss) in tableData"
        :key="col.fieldId"
        :prop="col.fieldCode"
        :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
      >
        <template slot-scope="scope">
          <!-- 如果是固定维度数据的情况 -->
          <div v-if="contrast(scope.row[col.fieldCode])">
            <!-- 如果是input类型 -->
            <div v-if="col.fieldTypeCode == 'text'">
              {{ scope.row[col.fieldCode] }}
            </div>
            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="col.fieldTypeCode == 'select'"
              v-model="scope.row[col.fieldCode]"
              disabled
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="col in selectList[indexss]"
                :key="col.id"
                :label="col.label"
                :value="col.value"
              />
            </el-select>
          </div>
          <!-- 如果不是固定维度数据的情况 -->
          <div v-else>
            <div v-if="col.fileuploadList != ''">
              <div
                v-for="(item,index) in col.fileuploadList"
                :key="index"
                class="download-link-wrap"
              >
                {{ $t('dashboard.uploadTemplate') }}
                <srm-common-file
                  :default-file="{
                    fileId: item.fileuploadId,
                    fileName: item.fileSourceName
                  }"
                  :readonly="true"
                />
              </div>
            </div>

            <!-- 上传附件 -->
            <div v-if="col.fieldTypeCode == 'upload'">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row[col.fieldCode],
                  fileName: scope.row[col.fieldCode+'name']
                }"
                :readonly="disabled || (col.editCondition ? evalF(col.editCondition) : false)"
                @on-change="({file}) => handleUploadSuccess(file, scope.row,col.fieldCode,scope)"
              />
            </div>

            <!-- 组件类型为文本 -->
            <el-input
              v-if="col.fieldTypeCode == 'text'"
              v-model="scope.row[col.fieldCode]"
              :maxlength="col.fieldLength"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />

            <!-- 组件类型为日期 -->
            <el-date-picker
              v-if="col.fieldTypeCode == 'date'"
              v-model="scope.row[col.fieldCode]"
              type="date"
              :format="$formatDatePicker"
              :placeholder="$t('purchaseDemand.datePicker')"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />
            <!-- 组件类型为时间 -->
            <el-time-select
              v-if="col.fieldTypeCode == 'dateTime'"
              v-model="scope.row[col.fieldCode]"
              :placeholder="$t('common.selectDate')"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />
            <!-- 组件类型为数字 -->
            <el-input
              v-if="col.fieldTypeCode == 'number'"
              v-model="scope.row[col.fieldCode]"
              type="number"
              :max="col.fieldLength"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />

            <!-- 组件类型为多行文本框 -->
            <el-input
              v-if="col.fieldTypeCode == 'textarea'"
              v-model="scope.row[col.fieldCode]"
              type="textarea"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />

            <!-- 组件类型为开关switch -->
            <el-switch
              v-if="col.fieldTypeCode == 'switch'"
              v-model="scope.row[col.fieldCode]"
              active-value="Y"
              inactive-value="N"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
            />

            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="col.fieldTypeCode == 'select'"
              v-model="scope.row[col.fieldCode]"
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="col in selectList[indexss]"
                :key="col.id"
                :label="col.label"
                :value="col.value"
              />
            </el-select>

            <!-- 组件类型为快查类型 -->
            <QuickSearch
              v-if="col.fieldTypeCode == 'quickSearch'"
              :disabled="disabled || (col.editCondition ? evalF(col.editCondition) : false) "
              :showInput="scope.row[col.fieldCode]"
              :show-key="col.quickSearchColumnShow"
              :scope-data="scope"
              :name="col.dictCode"
              @close-quicksearch="getVendorObj"
            />

            <!-- 组件类型为多选框 -->
            <el-checkbox-group
              v-if="col.fieldTypeCode == 'checkbox'"
              v-model="scope.row[col.fieldCode]"
              :disabled="disabled"
            >
              <el-checkbox
                v-for="(col, index) in checkboxAll[indexss]"
                :key="col.id"
                :label="col.label"
                :value="col.value"
              />
            </el-checkbox-group>

            <!-- 组件类型为单选框 -->
            <el-radio-group
              v-if="col.fieldTypeCode == 'radio'"
              v-model="scope.row[col.fieldCode]"
              :disabled="disabled"
            >
              <el-radio
                v-for="(col, index) in selectList[indexss]"
                :key="index"
                :label="col.label"
              >
                {{ col.label }}
              </el-radio>
            </el-radio-group>
          </div>
        </template>
      </el-table-column>
      <!-- 删除 -->
      <el-table-column fixed="right" :label="$t('common.operation')" width="60">
        <template slot-scope="scope">
          <el-button
            type="text"
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
import DictSelect from 'lib@/components/c-select/dict-select'
import QuickSearch from 'lib@/components/QuickSearch'
import TwoDimFlag from './twoDimFlag'

export default {
  name: 'FormExtend',
  components: {
    DictSelect,
    QuickSearch,
    TwoDimFlag
  },
  props: {
    tableData: {// 框架数据如果有的话使用，如果没有的话需要编写dimCode
      type: Array,
      default: () => {}
    },
    disabled: {
      type: Boolean,
      default: true
    },
    addOneTableData: {
      type: Function,
      default: () => {}
    },
    index: {
      type: Number,
      default: () => {}
    },
    tableExtendList: {
      type: Array,
      default: () => {}
    },
    model2: {
      type: Array,
      default: () => {}
    },
    rules: {
      type: Object,
      default: () => {}
    },
    refs: {
      type: String,
      default: () => {}
    },
    dimCode: { // 该项目的dimCode
      type: String,
      default: () => {}
    }
  },
  data () {
    return {
      fixedBol: false,
      selectList: [],
      checkboxAll: [],
      model: [],
      tableExtendListAll: [],
      fixedData: [], // 固定维度列表
      twoDimFlagData: [],
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
    model2: {
      handler () {
        // this.tableExtendList[this.index] = this.model2;
        try {
          this.initialization()
        } catch (e) {}
      },
      deep: true
    },
    tableData: {
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
              fieldCode: key,
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
            if (attrNew[index2].fieldCode == attr[index1].fieldCode) {
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
    } catch (e) {}
  },
  created () {
    let tableData = this.tableData
    let twoDimFlagData = []
    let tableExtendListAll = []
    // console.log(this.$store.state.user.userInfo.userType)
    tableData.forEach(datas => {
      // console.log(datas)
      if (datas.fixedData != '') {
        this.fixedBol = true
        let fixedData = []
        let codes = datas.fieldCode
        fixedData = datas.fixedData.split(',')
        this.fixedData = fixedData
        fixedData.forEach(elem => {
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
    } catch (error) {

    }
  },
  methods: {
    // 确认快查后
    getVendorObj (val, scope) {
      const name = scope.column.property
      console.log(this.tableData)
      let quickSearchName = ''
      let quickSearchColumnShow = ''
      let tableData = this.tableData
      tableData.forEach(datas => {
        if (datas.quickSearchColumn != '') {
          quickSearchName = datas.quickSearchColumn
          quickSearchColumnShow = datas.quickSearchColumnShow
        }
      })
      scope.row[name] = val ? val[quickSearchName] : ''
    },
    evalF (val) {
      return eval(val)
    },

    addOneTableData2 () {
      this.tableExtendListAll.push({})
    },
    contrast (data) {
      let fixData = this.fixedData
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
        for (let i = 0; i < _this.tableData.length; i++) {
          // 如果是下拉框或者单选框的时候请求字典
          // console.log(element);
          if (
            _this.tableData[i].fieldTypeCode == 'select' ||
            _this.tableData[i].fieldTypeCode == 'radio'
          ) {
            let res = await getDictItem(_this.tableData[i].dictCode)
            let res2 = await adaptDictData(res.data, 'dict')
            _this.selectList.push(res2)
            _this.checkboxAll.push({})
          } else if (_this.tableData[i].fieldTypeCode == 'checkbox') {
            // 如果是多选框的时候请求字典
            let resA = await getDictItem(_this.tableData[i].dictCode)
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
      // this.tableExtendList[this.index] = [{ "1011-001": "Y", code123: "sdf" }];
      // console.log(this.model2);
      let arr = Object.keys(this.model2)
      if (arr.length == 0) {
        return false
      }
      let attr = []
      let indexs = this.index
      // let indexs = null
      console.log(this.tableData)
      this.model2.forEach((datas, index) => {
        // if (datas.dimFieldNum == indexs && datas.dimFieldNum != "") {
        //   let obj = {};
        //   delete datas.dimFieldNum;
        //   obj = { ...datas };
        //   attr.push(obj);
        // }

        this.tableData.forEach(item => {
          if (datas.hasOwnProperty(item.fieldCode)) {
            let obj = {}
            delete datas.dimFieldNum
            obj = { ...datas }
            attr.push(obj)
          }
        })

        // for(let key in datas){
        //   if(this.tableData[1].fieldCode == key){
        //     indexs = index
        //   }
        // }

        // let obj = {};
        // delete datas.dimFieldNum;
        // obj = { ...datas };
        // attr.push(obj);
      })
      console.log(attr)
      let attrNew = []
      for (let key in attr[0]) {
        attrNew = attr[0][key].split(',')
      }
      console.log(indexs)
      // for (let key in attr[indexs]) {
      //   attrNew = attr[indexs][key].split(",");
      // }
      attrNew.forEach((datas, Eindexs) => {
        attrNew[Eindexs] = {}
      })
      // debugger;
      // console.log(attrNew)

      attr.forEach((datas, Eindexs) => {
        for (let key in attr[Eindexs]) {
          let attrOne = []
          attrOne = attr[Eindexs][key].split(',')
          attrOne.forEach((datas2, Eindexs2) => {
            attrNew[Eindexs2][key] = datas2
          })
        }
      })
      console.log(attrNew)
      this.tableExtendListAll = attrNew
      this.$forceUpdate()
    }
  }
}
</script>

<style lang="scss">
.el-collapse-item__wrap{
  border-bottom:none;
}
</style>
