<template>
  <div style="">
    <el-form
      v-if="formData.length > 0"
      :ref="{ refs }"
      :model="{ model }"
      :rules="{ rules }"
      :show-message="false"
      :disabled="disabled"
    >
      <srm-row :gutter="32">
        <srm-col
          v-for="(item, index) in formData"
          :key="item.fieldConfigId"
          :initCol="3"
        >
          <el-form-item
            v-if="item.showCondition ? evalF(item.showCondition) : true"
            :prop="item.fieldCode"
            :label="item.languageCode ? $t(item.languageCode) : item.fieldName"
          >
            <!-- 组件类型为文本 -->
            <el-input
              v-if="item.fieldTypeCode == 'text'"
              v-model="model[item.fieldCode]"
              :maxlength="item.fieldLength"
            />
            <!-- 组件类型为日期 -->
            <el-date-picker
              v-if="item.fieldTypeCode == 'date'"
              v-model="model[item.fieldCode]"
              type="date"
              :format="$formatDatePicker"
              :placeholder="$t('bidMod.datePicker')"
            />
            <!-- 组件类型为时间 -->
            <el-time-select
              v-if="item.fieldTypeCode == 'dateTime'"
              v-model="model[item.fieldCode]"
              :placeholder="$t('common.selectDate')"
            />
            <!-- 组件类型为数字 -->
            <el-input
              v-if="item.fieldTypeCode == 'number'"
              v-model="model[item.fieldCode]"
              type="number"
              :max="item.fieldLength"
            />

            <!-- 组件类型为多行文本框 -->
            <el-input
              v-if="item.fieldTypeCode == 'textarea'"
              v-model="model[item.fieldCode]"
              type="textarea"
            />

            <!-- 组件类型为开关switch -->
            <el-switch
              v-if="item.fieldTypeCode == 'switch'"
              v-model="model[item.fieldCode]"
              active-value="Y"
              inactive-value="N"
            />

            <!-- 组件类型为下拉框 -->
<!--            <el-select-->
<!--              v-if="item.fieldTypeCode == 'select'"-->
<!--              v-model="model[item.fieldCode]"-->
<!--              :placeholder="$t('common.pleaseSelect')"-->
<!--            >-->
<!--              <el-option-->
<!--                v-for="item in selectList[index]"-->
<!--                :key="item.id"-->
<!--                :label="item.label"-->
<!--                :value="item.value"-->
<!--              />-->
<!--            </el-select>-->
            <DictSelect
              v-if="item.fieldTypeCode == 'select'"
              v-model="model[item.fieldCode]"
              :code="item.dictCode"
            />

            <!-- 组件类型为多选框 -->
            <el-checkbox-group
              v-if="item.fieldTypeCode == 'checkbox'"
              v-model="model[item.fieldCode]"
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
              v-model="model[item.fieldCode]"
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
                  fileId: model[item.fieldCode],
                  fileName: model[item.fieldCode+'name']
                }"
                :readonly="disabled"
                @on-change="({file}) => handleUploadSuccess(file,model,item.fieldCode)"
              />
            </div>

            <!--快查组件-->
            <QuickSearch
              v-if="item.fieldTypeCode == 'quickSearch'"
              ref="quickSearchTool"
              :show-input="model[item.fieldCode]"
              :prop-key="item.quickSearchColumn"
              :show-key="item.quickSearchColumnShow"
              :name="item.dictCode"
              :disabled="disabled"
              @close-quicksearch="getObj($event, item.fieldCode, item.quickSearchColumn)"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
  </div>
</template>

<script>
import { adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'FormExtend',
  components: {
    QuickSearch
  },
  props: {
    formData: {
      type: Array,
      default: () => {}
    },
    model: {
      type: Object,
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
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      selectList: [],
      checkboxAll: [],
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
        this.fatchDictData() // 加载数据字典
      },
      deep: true
    }
  },
  mounted () {
    this.fatchDictData()
  },
  created () {},

  methods: {
    getObj (val, fieldCode, name) {
      // console.log(val[name])
      console.log(this.model[fieldCode])
      this.model[fieldCode] = val[name]
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
    },
    evalF (val) {
      return eval(val)
    },
    fatchDictData () {
      this.selectList = []
      this.checkboxAll = []
      this.formData.forEach(async element => {
        // 如果是下拉框或者单选框的时候请求字典

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

        if (
          element.fieldTypeCode == 'select' ||
          element.fieldTypeCode == 'radio'
        ) {
          let res = await getDictItem(element.dictCode)
          this.selectList.push(adaptDictData(res.data, 'dict'))
          this.checkboxAll.push({})
        } else if (element.fieldTypeCode == 'checkbox') {
          // 如果是多选框的时候请求字典
          let resA = await getDictItem(element.dictCode)
          this.checkboxAll.push(adaptDictData(resA.data, 'dict'))
          this.selectList.push({})
        } else {
          this.selectList.push({})
          this.checkboxAll.push({})
        }
      })
    },
    // 点击暂存提交的时候回传给父级页面
    saveAndSubmit () {
      return this.model
    }
  }
}
</script>

<style scoped lang="scss">
</style>
