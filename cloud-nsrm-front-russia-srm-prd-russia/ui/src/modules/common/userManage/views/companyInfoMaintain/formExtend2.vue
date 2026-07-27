<template>
  <div ref="content">
    <el-form
      v-if="formData.length > 0"
      :ref="{ refs }"
      :model="modelAll"
      :rules="rules"
      :show-message="false"
      :disabled="disabled"
    >
      <srm-row :gutter="32">
        <srm-col
          v-for="(item, index) in formData"
          :key="item.fieldConfigId"
          :initCol="4"
        >
          <el-form-item
            v-if="item.showCondition ? evalF(item.showCondition) : true"
            :prop="item.fieldCode"
            :label="item.languageCode ? $t(item.languageCode) : item.fieldName"
            :class="itemF(item)"
          >
            <!-- 组件类型为文本 -->
            <el-input
              v-if="item.fieldTypeCode == 'text'"
              v-model="modelAll[item.fieldCode]"
              :maxlength="item.fieldLength"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为日期 -->
            <el-date-picker
              v-if="item.fieldTypeCode == 'date'"
              v-model="modelAll[item.fieldCode]"
              type="date"
              :placeholder="$t('bidMod.datePicker')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为时间 -->
            <el-time-select
              v-if="item.fieldTypeCode == 'dateTime'"
              v-model="modelAll[item.fieldCode]"
              :placeholder="$t('common.selectDate')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />
            <!-- 组件类型为数字 -->
            <el-input
              v-if="item.fieldTypeCode == 'number'"
              v-model="modelAll[item.fieldCode]"
              type="number"
              :max="item.fieldLength"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />

            <!-- 组件类型为多行文本框 -->
            <el-input
              v-if="item.fieldTypeCode == 'textarea'"
              v-model="modelAll[item.fieldCode]"
              type="textarea"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />

            <!-- 组件类型为开关switch -->
            <el-switch
              v-if="item.fieldTypeCode == 'switch'"
              v-model="modelAll[item.fieldCode]"
              active-value="Y"
              inactive-value="N"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
            />

            <!-- 组件类型为下拉框 -->
            <el-select
              v-if="item.fieldTypeCode == 'select' && selectChange(item.fieldCode,modelAll[item.fieldCode])"
              v-model="modelAll[item.fieldCode]"
              :placeholder="$t('common.pleaseSelect')"
              :disabled="disabled || item.editCondition ? evalF(item.editCondition) : false "
              @change="selectChange(item.fieldCode,modelAll[item.fieldCode])"
            >
              <el-option
                v-for="item in selectList[index]"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-select>

            <!-- 组件类型为多选框 -->
            <el-checkbox-group
              v-if="item.fieldTypeCode == 'checkbox'"
              v-model="modelAll[item.fieldCode]"
              :disabled="disabled"
            >
              <el-checkbox
                v-for="(item, index) in checkboxAll[index]"
                :key="item.id"
                :label="item.label"
                :value="item.value"
              />
            </el-checkbox-group>

            <!-- 组件类型为单选框 -->
            <el-radio-group
              v-if="item.fieldTypeCode == 'radio'"
              v-model="modelAll[item.fieldCode]"
              :disabled="disabled"
            >
              <el-radio
                v-for="(item, index) in selectList[index]"
                :key="index"
                :label="item.label"
              >
                {{ item.label }}
              </el-radio>
            </el-radio-group>

            <!-- 上传文件 -->
            <div v-if="item.fieldTypeCode == 'upload'">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: modelAll[item.fieldCode],
                  fileName: modelAll[item.fieldCode+'name']
                }"
                :readonly="disabled || item.editCondition ? evalF(item.editCondition) : false"
                @on-change="({file}) => handleUploadSuccess(file,modelAll,item.fieldCode)"
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
  name: 'FormExtend',
  components: {
    DictSelect
  },
  props: {
    formData: {
      type: Array,
      default: () => {}
    },
    model2: {
      type: Array,
      default: () => {}
    },
    disabled: {
      type: Boolean,
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
      selectList: [],
      checkboxAll: [],
      model: [],
      modelAll: {},
      colorImit: 'red',
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'questManagement',
        fileType: 'images'
      }
    }
  },
  watch: {
    formData: {
      handler () {
        // this.fatchDictData(); // 加载数据字典
      },
      deep: true
    },
    modelAll: {
      handler () {
        let models = this.modelAll
        let attr = []
        for (let key in models) {
          let obj = {
            fieldCode: key,
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
    this.$refs.content.style.setProperty('--colorImit', this.colorImit)
    this.fatchDictData()
    this.start()
    let model2 = {}
    try {
      this.model2.forEach(datas => {
        model2 = Object.assign(model2, datas)
      })
    } catch (e) {}

    this.modelAll = model2
  },
  created () {},

  methods: {
    selectChange (key, val) {
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
        this.$set(row, col + 'name', fileName)
      } else {
        this.$set(row, col, '')
        this.$set(row, col + 'name', '')
      }
      const { id, name } = file
    },
    evalF (val) {
      try {
        return eval(val)
      } catch (error) {
        return true
      }
    },
    // 表单文件上传
    formUploadSuccess (file, code, fieldId) {
      const { id, name } = file
      this.modelAll[code] = id.toString() + '#' + name
    },
    // 初始化
    start () {
      let _this = this
      let modelAll = {}
      this.formData.forEach((element, index) => {
        modelAll[element.fieldCode] = ''
        if (element.isCheck == 'Y') {
          let attr = [
                    {
                      required: true,
                      message: this.$t('common.pleaseInput')
                    }
                  ]
          this.$set(
            this.rules,
            element.fieldCode,
            attr
          )
        }
      })
      this.modelAll = modelAll
      this.$forceUpdate()
    },
    fatchDictData () {
      this.selectList = []
      this.checkboxAll = []
      let _this = this
      async function fors () {
        for (let i = 0; i < _this.formData.length; i++) {
          // 如果是下拉框或者单选框的时候请求字典
          if (
            _this.formData[i].fieldTypeCode == 'select' ||
            _this.formData[i].fieldTypeCode == 'radio'
          ) {
            let res = await getDictItem(_this.formData[i].dictCode)
            let res2 = await adaptDictData(res.data, 'dict')
            _this.selectList.push(res2)
            _this.checkboxAll.push({})
          } else if (_this.formData[i].fieldTypeCode == 'checkbox') {
            // 如果是多选框的时候请求字典
            let resA = await getDictItem(_this.formData[i].dictCode)
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
      return this.model
    }
  }
}
</script>

<style lang="scss">
.c-upload-file{
  display: block;
}
.itemE .el-form-item__label{
    color: var(--colorImit)
  }
</style>
