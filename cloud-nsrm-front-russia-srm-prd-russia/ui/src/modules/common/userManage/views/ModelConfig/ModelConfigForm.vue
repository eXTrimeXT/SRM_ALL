<template>
  <div ref="content">
    <el-form
      v-if="dimConfig && dimConfig.length > 0"
      :ref="{ refss }"
      :model="modelAll"
      :rules="initRules"
      :show-message="false"
      :disabled="disabled"
    >
      <srm-row :gutter="32">
        <srm-col
          v-for="(item, index) in dimConfig"
          :key="item.formAttributeId"
          :initCol="item.labelWidth ? parseInt(item.labelWidth) : 3"
        >
          <el-form-item
            v-if="item.showCondition ? evalF(item.showCondition) : true"
            :prop="item.columnName"
            :label="item.languageCode ? $t(item.languageCode) : item.propertyName"
            :class="itemF(item)"
          >
            <!--组件类型为是否勾选框-->
            <div v-if="item.componentType == 'yesOrNo'">
              <el-checkbox
                v-model="modelAll[item.columnName]"
                :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
                true-label="Y"
                false-label="N"
              />
            </div>

            <!-- 组件类型为文本 -->
            <el-input
              v-if="item.componentType == 'text'"
              v-model="modelAll[item.columnName]"
              :maxlength="item.fieldLength"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为日期 -->
            <el-date-picker
              v-if="item.componentType == 'date'"
              v-model="modelAll[item.columnName]"
              type="date"
              :placeholder="$t('purchaseDemand.datePicker')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为时间 -->
            <el-time-select
              v-if="item.componentType == 'dateTime'"
              v-model="modelAll[item.columnName]"
              :placeholder="$t('sourcingBuyer.timePlaceholder')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为数字 -->
            <el-input
              v-if="item.componentType == 'number'"
              v-model="modelAll[item.columnName]"
              type="number"
              :max="item.fieldLength"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为多行文本框 -->
            <el-input
              v-if="item.componentType == 'textarea'"
              v-model="modelAll[item.columnName]"
              type="textarea"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为开关switch -->
            <el-switch
              v-if="item.componentType == 'switch'"
              v-model="modelAll[item.columnName]"
              active-value="Y"
              inactive-value="N"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="item.componentType == 'select' && selectChange(item.columnName,modelAll[item.columnName])"
              v-model="modelAll[item.columnName]"
              :placeholder="$t('common.pleaseSelect')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
              @change="selectChange(item.columnName,modelAll[item.columnName])"
            >
              <el-option
                v-for="selItem in selectList[index]"
                :key="selItem.id"
                :label="selItem.label"
                :value="selItem.value"
              />
            </el-select>
            <!-- 组件类型为多选框 -->
            <el-checkbox-group
              v-if="item.componentType == 'checkbox'"
              v-model="checkboxData"
              :disabled="disabled"
            >
              <el-checkbox
                v-for="(checkItem) in checkboxAll[index]"
                :key="checkItem.id"
                :label="checkItem.label"
                :value="checkItem.value"
              />
            </el-checkbox-group>

            <!-- 组件类型为单选框 -->
            <el-radio-group
              v-if="item.componentType == 'radio'"
              v-model="modelAll[item.columnName]"
              :disabled="disabled"
            >
              <el-radio
                v-for="(selItem) in selectList[index]"
                :key="selItem.value"
                :label="selItem.label"
              >
                {{ selItem.label }}
              </el-radio>
            </el-radio-group>

            <!-- 上传文件 -->
            <div v-if="item.componentType == 'upload'">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: modelAll[item.columnName] || null,
                  fileName: modelAll[item.columnName+'---fileName'] || null
                }"
                :readonly="disabled || item.editCondition ? evalF(item.editCondition) : false"
                @on-change="({file}) => handleUploadSuccess(file,modelAll,item.columnName)"
              />
            </div>
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import DictSelect from 'lib@/components/c-select/dict-select'

export default {
  name: 'ModelConfigForm',
  components: {
    DictSelect
  },
  props: {
    dimConfig: {
      type: Array,
      default: () => {
        return []
      }
    },
    formValue: {
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
    rules: {
      type: Object,
      default: () => {
        return {}
      }
    },
    refss: {
      type: String,
      default: () => {
        return ''
      }
    }
  },
  data () {
    return {
      checkboxData: [],
      checkboxName: '',
      selectList: [],
      checkboxAll: [],
      model: [],
      modelAll: {},
      colorImit: 'red',
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'questManagement',
        fileType: 'images'
      },
      initRules: {}
    }
  },
  watch: {
    checkboxData: {
      handler () {
        this.modelAll[this.checkboxName] = this.checkboxData.toString()
      },
      deep: true
    },
    modelAll: {
      handler () {
        let models = this.modelAll
        let attr = []
        for (let key in models) {
          let obj = {
            columnName: key,
            fieldValue: models[key]
          }
          attr.push(obj)
        }
        this.model = attr
      },
      deep: true
    }
  },
  mounted () {
    if (this.dimConfig) {
      this.$refs.content.style.setProperty('--colorImit', this.colorImit)
      this.fatchDictData()
      this.start()
      let formValue = {}
      try {
        // 只取本维度的值
        if (this.dimConfig && this.dimConfig.length > 0) {
          let dimItem = this.dimConfig[0]
          this.formValue.forEach(datas => {
            if (dimItem.dimId === datas.dimId) {
              formValue[datas.columnName] = datas.fieldValue
            }
          })
        }
      } catch (e) {
      }
      this.modelAll = formValue
    }
  },
  created () {
  },

  methods: {
    submitForm () {
      console.log(this.refss)
      this.$refs[this.refss].validate((valid) => {
        if (valid) {
          return true
        } else {
          return false
        }
      })
    },
    getDataValue () {
      let resutList = []
      if (this.dimConfig) {
        this.dimConfig.forEach(item => {
          let fileName = this.modelAll[item.columnName + '---fileName']
          if (this.modelAll[item.columnName] || fileName) {
            let resultItem = {
              emptyFlag: item.emptyFlag,
              columnName: item.columnName,
              propertyName: item.propertyName,
              formPageId: item.formPageId,
              dimId: item.dimId,
              formAttributeId: item.formAttributeId,
              fieldValue: this.modelAll[item.columnName]
            }
            resutList.push(resultItem)
            if (fileName) {
              let fileNameItem = {
                emptyFlag: item.emptyFlag,
                columnName: item.columnName + '---fileName',
                propertyName: item.propertyName,
                formPageId: item.formPageId,
                dimId: item.dimId,
                formAttributeId: item.formAttributeId,
                fieldValue: this.modelAll[item.columnName + '---fileName']
              }
              resutList.push(fileNameItem)
            }
          } else {
            let resultItem = {
              emptyFlag: item.emptyFlag,
              fieldValue: ''
            }
            resutList.push(resultItem)
          }
        })
      }
      return resutList
    },
    selectChange (key, val) {
      // console.log(key,val)
      let obj = {
        [key]: val
      }
      this.$emit('selectChange', obj)
      return true
    },
    // 修改字体颜色
    itemF (item) {
      if (item.fontColor != '') {
        this.colorImit = item.fontColor
        return 'itemE'
      } else {
        return ''
      }
    },
    // 上传附件成功
    handleUploadSuccess (file, row, col) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        this.$set(row, col, fileId.toString())
        this.$set(row, col + '---fileName', fileName)
      } else {
        this.$set(row, col, '')
        this.$set(row, col + '---fileName', '')
      }
    },
    evalF (val) {
      try {
        // eslint-disable-next-line no-eval
        return eval(val)
      } catch (error) {
        return true
      }
    },
    // 初始化
    start () {
      let modelAll = {}
      this.initRules = this.rules
      this.dimConfig.forEach((element) => {
        modelAll[element.columnName] = ''
        if (element.emptyFlag === 'Y') {
          let attr = [
            {
              required: true,
              message: '请输入'
            }
          ]
          this.$set(
            this.initRules,
            element.columnName,
            attr
          )
          console.log(this.initRules)
        }
      })
      this.modelAll = modelAll
      this.$forceUpdate()
    },
    fatchDictData () {
      this.selectList = []
      this.checkboxAll = []
      // console.log(this.dimConfig);
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
            // console.log("1");
            _this.selectList.push(res2)
            _this.checkboxAll.push({})
          } else if (_this.dimConfig[i].componentType == 'checkbox') {
            // 如果是多选框的时候请求字典
            let resA = await getDictItem(_this.dimConfig[i].dictCode)
            _this.checkboxAll.push(adaptDictData(resA.data, 'dict'))
            // console.log(_this.checkboxAll, 'checkboxAll')
            _this.checkboxName = _this.dimConfig[i].columnName
            _this.selectList.push({})
          } else {
            // console.log("2");
            _this.selectList.push({})
            _this.checkboxAll.push({})
          }
        }
        console.log(_this.modelAll[_this.checkboxName], 'checkboxName')
        if (_this.modelAll[_this.checkboxName]) {
          _this.checkboxData = _this.modelAll[_this.checkboxName].split(',')
        }
      }

      fors()

      // this.dimConfig.forEach(async function(element) {});
      // console.log(this.selectList);
    },
    // 点击暂存提交的时候回传给父级页面
    saveAndSubmit () {
      return this.model
    }
  }
}
</script>

<style lang="scss">
.c-upload-file {
  display: block;
}

.itemE .el-form-item__label {
  color: var(--colorImit)
}
</style>
