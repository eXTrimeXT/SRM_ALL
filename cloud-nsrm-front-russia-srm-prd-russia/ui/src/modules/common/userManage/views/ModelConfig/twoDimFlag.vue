<template>
  <div>
    <p>
      <el-button
        v-if="!disabled && twoDimFlagData == []"
        type="primary"
        class="detail-pbtn"
        @click="addOneTableData(index)"
      >
        {{ $t("common.new") }}
      </el-button>
    </p>
    <el-table
      v-if="twoDimFlagData == []"
      :data="tableExtendListAll"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        :key="index"
        :label="$t('contractMod.order')"
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
          <!-- 上传附件 -->
          <div v-if="col.fieldTypeCode == 'upload'">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: scope.row[col.fieldCode],
                fileName: scope.row[col.fieldCode+'name']
              }"
              :readonly="false"
              @on-change="({file}) => handleUploadSuccess(file, scope.row,col.fieldCode,scope)"
            />
          </div>

          <!-- 组件类型为文本 -->
          <el-input
            v-if="col.fieldTypeCode == 'text'"
            v-model="scope.row[col.fieldCode]"
            :maxlength="col.fieldLength"
            :disabled="disabled"
          />

          <!-- 组件类型为日期 -->
          <el-date-picker
            v-if="col.fieldTypeCode == 'date'"
            v-model="scope.row[col.fieldCode]"
            type="date"
            :placeholder="$t('purchaseDemand.datePicker')"
            :disabled="disabled"
          />
          <!-- 组件类型为时间 -->
          <el-time-select
            v-if="col.fieldTypeCode == 'dateTime'"
            v-model="scope.row[col.fieldCode]"
            :placeholder="$t('sourcingBuyer.timePlaceholder')"
            :disabled="disabled"
          />
          <!-- 组件类型为数字 -->
          <el-input
            v-if="col.fieldTypeCode == 'number'"
            v-model="scope.row[col.fieldCode]"
            type="number"
            :max="col.fieldLength"
            :disabled="disabled"
          />

          <!-- 组件类型为多行文本框 -->
          <el-input
            v-if="col.fieldTypeCode == 'textarea'"
            v-model="scope.row[col.fieldCode]"
            type="textarea"
            :disabled="disabled"
          />

          <!-- 组件类型为开关switch -->
          <el-switch
            v-if="col.fieldTypeCode == 'switch'"
            v-model="scope.row[col.fieldCode]"
            active-value="Y"
            inactive-value="N"
            :disabled="disabled"
          />

          <!-- 组件类型为下拉框 -->
          <el-select
            v-if="col.fieldTypeCode == 'select'"
            v-model="scope.row[col.fieldCode]"
            :placeholder="$t('common.pleaseSelect')"
          >
            <el-option
              v-for="selItem in selectList[indexss]"
              :key="selItem.id"
              :label="selItem.label"
              :value="selItem.value"
            />
          </el-select>

          <!-- 组件类型为多选框 -->
          <el-checkbox-group
            v-if="col.fieldTypeCode == 'checkbox'"
            v-model="scope.row[col.fieldCode]"
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
            v-if="col.fieldTypeCode == 'radio'"
            v-model="scope.row[col.fieldCode]"
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
        </template>
      </el-table-column>
      <!-- 删除 -->
      <el-table-column
        fixed="right"
        :label="$t('common.operation')"
        width="60"
      >
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

    <two-dim-flag />
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
// import TwoDimFlag from './twoDimFlag'

export default {
  name: 'TwoDimFlag',
  // components: {
  //   TwoDimFlag
  // },
  props: {
    tableData: {
      type: Array,
      default: () => {}
    },
    disabled: {
      type: Boolean,
      default: () => {}
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
    }
  },
  data () {
    return {
      // disabled: false,
      selectList: [],
      checkboxAll: [],
      model: [],
      tableExtendListAll: [],
      twoDimFlagData: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'accessFlowSetting',
        fileType: 'images'
      }
    }
  },
  watch: {
    model2: {
      handler () {
        // this.tableExtendList[this.index] = this.model2;
        try {
          this.initialization()
          // eslint-disable-next-line no-empty
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
  created () {
    this.tableExtendListAll = this.tableExtendList[this.index]
    let tableData = this.tableData
    let twoDimFlagData = []
    tableData.forEach(datas => {
      if (datas.twoDimFlag == 'Y') {
        twoDimFlagData.push(datas)
      }
    })
    this.twoDimFlagData = twoDimFlagData
  },
  mounted () {
    this.fatchDictData()
    try {
      this.initialization()
      // eslint-disable-next-line no-empty
    } catch (e) {}
  },
  methods: {
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
      this.model2.forEach(datas => {
        if (datas.dimFieldNum == indexs && datas.dimFieldNum != '') {
          let obj = {}
          delete datas.dimFieldNum
          obj = { ...datas }
          attr.push(obj)
        }
      })
      // console.log(attr);
      let attrNew = []
      for (let key in attr[0]) {
        attrNew = attr[0][key].split(',')
      }
      attrNew.forEach((datas, Eindexs) => {
        attrNew[Eindexs] = {}
      })

      attr.forEach((datas, Eindexs) => {
        for (let key in attr[Eindexs]) {
          let attrOne = []
          attrOne = attr[Eindexs][key].split(',')
          attrOne.forEach((datas2, Eindexs2) => {
            attrNew[Eindexs2][key] = datas2
          })
        }
      })
      // console.log(attrNew);
      this.tableExtendListAll = attrNew
      this.$forceUpdate()
    }
  }
}
</script>

<style scoped lang="scss"></style>
