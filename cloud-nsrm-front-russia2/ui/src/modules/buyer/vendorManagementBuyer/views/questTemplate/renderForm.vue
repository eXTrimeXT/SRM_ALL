<template>
  <div>
    <el-tabs
      v-model="activeTab"
      type="card"
    >
      <el-tab-pane
        v-for="item in configData"
        :key="item.questTemplatePropGroupCode"
        :label="item.questTemplatePropGroupName"
        :name="item.questTemplatePropGroupCode"
        :lazy="true"
      >
        <div class="fileInfoDiv">
          <!-- 表单 -->
          <el-form
            v-if="item.questTemplatePropGroupType==='form'"
            :model="resData[item.questTemplatePropGroupCode]"
          >
            <srm-row :gutter="32">
              <template v-for="formItem in item.formItems">
                <srm-col
                  v-if="formItem.enabledFlag==='Y'"
                  :key="formItem.fieldConfigId"
                  :span="6"
                >
                  <el-form-item
                    :prop="formItem.questTemplatePropField"
                    :label="formItem.questTemplatePropFieldDesc"
                    :rules="[
                      {
                        required: formItem.emptyFlag==='Y' ? true:false,
                        message:this.$t('vendorMod.required')  // '必填'
                      }
                    ]"
                  >
                    <!-- 文本 -->
                    <el-input
                      v-if="formItem.questTemplatePropType==='text'"
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      :disabled="disabled"
                      clearable
                    />
                    <!-- 数字 -->
                    <el-input
                      v-else-if="formItem.questTemplatePropType==='number'"
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      v-input-format="{ type: 'float' }"
                      :disabled="disabled"
                      clearable
                    />
                    <!-- 多行文本 -->
                    <el-input
                      v-else-if="formItem.questTemplatePropType==='mText'"
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      type="textarea"
                      :rows="2"
                      :disabled="disabled"
                      clearable
                    />
                    <!-- 日期 -->
                    <el-date-picker
                      v-else-if="formItem.questTemplatePropType==='date'"
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      :disabled="disabled"
                      clearable
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                    <!-- 开关 -->
                    <div
                      v-else-if="formItem.questTemplatePropType==='switch'"
                      style="margin-top: 28px;"
                    >
                      <el-switch
                        v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                        :disabled="disabled"
                        active-value="Y"
                        inactive-value="N"
                      />
                    </div>
                    <DictSelect
                      v-else-if="formItem.questTemplatePropType==='select'"
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      :disabled="disabled"
                      :code="formItem.questTemplatePropDict"
                    />
                    <div
                      v-else-if="formItem.questTemplatePropType==='file'"
                      class="formRenderFile"
                    >
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: (resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]).split('#')[0],
                          fileName: (resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]).split('#')[1]
                        }"
                        :readonly="disabled"
                        @on-change="({file}) => formUploadSuccess(file,item.questTemplatePropGroupCode,formItem.questTemplatePropField)"
                      />
                    </div>
                    <el-input
                      v-else
                      v-model="resData[item.questTemplatePropGroupCode][formItem.questTemplatePropField]"
                      :disabled="disabled"
                      clearable
                    />
                  </el-form-item>
                </srm-col>
              </template>
            </srm-row>
          </el-form>
          <!-- 表格 -->
          <div v-if="item.questTemplatePropGroupType==='table'">
            <!-- 编辑模式下才需要新增按钮 -->
            <div
              v-if="optType==='edit'"
              class="optDiv"
            >
              <el-button
                type="primary"
                @click="addFiledItem(item.questTemplatePropGroupCode,item.rowObj)"
              >
                {{ $t('common.add') }}
              </el-button>
            </div>
            <el-table
              :data="resData[item.questTemplatePropGroupCode]"
              style="width: 100%"
              border
            >
              <el-table-column
                type="index"
                width="60"
                :label="$t('contractMod.order')"
              />
              <template v-for="col in item.colItems">
                <el-table-column
                  v-if="col.enabledFlag==='Y'"
                  :key="col.questTemplatePropField"
                  :prop="col.questTemplatePropField"
                  :label="col.questTemplatePropFieldDesc"
                  min-width="110px"
                >
                  <template
                    v-if="col.emptyFlag==='Y'"
                    slot="header"
                  >
                    <i class="toRequired">*</i>
                    {{ col.questTemplatePropFieldDesc }}
                  </template>
                  <template slot-scope="scope">
                    <!-- 文本 -->
                    <el-input
                      v-if="col.questTemplatePropType==='text'"
                      v-model="scope.row[col.questTemplatePropField]"
                      clearable
                      :disabled="disabled"
                    />
                    <!-- 数字 -->
                    <el-input
                      v-else-if="col.questTemplatePropType==='number'"
                      v-model="scope.row[col.questTemplatePropField]"
                      v-input-format="{ type: 'float' }"
                      clearable
                      :disabled="disabled"
                    />
                    <!-- 多行文本 -->
                    <el-input
                      v-else-if="col.questTemplatePropType==='mText'"
                      v-model="scope.row[col.questTemplatePropField]"
                      type="textarea"
                      :rows="2"
                      clearable
                      :disabled="disabled"
                    />
                    <!-- 日期 -->
                    <el-date-picker
                      v-else-if="col.questTemplatePropType==='date'"
                      v-model="scope.row[col.questTemplatePropField]"
                      :disabled="disabled"
                      clearable
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                    <!-- 开关 -->
                    <el-switch
                      v-else-if="col.questTemplatePropType==='switch'"
                      v-model="scope.row[col.questTemplatePropField]"
                      :disabled="disabled"
                      active-value="Y"
                      inactive-value="N"
                    />
                    <DictSelect
                      v-else-if="col.questTemplatePropType==='select'"
                      v-model="scope.row[col.questTemplatePropField]"
                      :disabled="disabled"
                      :code="col.questTemplatePropDict"
                    />
                    <div
                      v-else-if="col.questTemplatePropType==='file'"
                      class="formRenderFile"
                    >
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: (scope.row[col.questTemplatePropField]).split('#')[0],
                          fileName: (scope.row[col.questTemplatePropField]).split('#')[1]
                        }"
                        :readonly="disabled"
                        @on-change="({file}) => tableUploadSuccess(file,scope.row,col.questTemplatePropField)"
                      />
                    </div>
                    <el-input
                      v-else
                      v-model="scope.row[col.questTemplatePropField]"
                      clearable
                      :disabled="disabled"
                    />
                  </template>
                </el-table-column>
              </template>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import DictSelect from 'lib@/components/c-select/dict-select'

export default {
  name: 'RenderForm',
  components: { DictSelect },
  props: {
    questTemplateId: { // 模板ID
      type: [String, Number],
      default: ''
    },
    questSupId: { // 供应商ID
      type: [String, Number],
      default: ''
    },
    disabled: { // 预览都是禁用
      type: Boolean,
      default: false
    },
    optType: {
      type: String,
      default: 'view'
    },
    comData: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'questManagement',
        fileType: 'images'
      },
      activeTab: '',
      configData: [],
      mergeForm: {},
      sourceList: [],
      resData: {},
      resFormateData: {} // 提交给后端最后数据
    }
  },
  watch: {
    questTemplateId (new_value, old_value) {
      if (new_value && new_value !== old_value) {
        this.getTempDetail(new_value)
      }
    }
  },
  created () {
    if (this.questTemplateId) {
      this.getTempDetail(this.questTemplateId)
    }
  },
  mounted () {
    this.$forceUpdate()
  },
  methods: {
    // 通过id查询模板数据
    getTempDetail (questTemplateId) {
      this.$http({
        url: '/api-sup/quest/questTemplate/questTemplateData',
        method: 'GET',
        params: { questTemplateId },
        loading: true
      }).then(res => {
        this.configData = this.adaptConfigData(res.data.questTemplateTabArr) // 配置信息
        this.activeTab = this.configData[0].questTemplatePropGroupCode
        this.resFormateData = this.formateResData(res.data)
      }).catch(err => {
        this.configData = this.adaptConfigData([]) // 配置信息
        this.resFormateData = this.formateResData([])
        console.log(err)
      })
    },
    // 适配数据
    adaptConfigData (data) {
      let resArr = []
      let resData = {}
      let TableRowObj = {}
      if (data && data.length > 0) {
        data.forEach(elm => {
          if (elm.showFlag === 'Y') { // 显示的配置
            if (elm.questTemplatePropGroupType === 'form') {
              let colConfig = []
              resData[elm.questTemplatePropGroupCode] = {}
              elm.questTemplatePropArr.forEach(item => {
                colConfig.push(
                  {
                    questTemplatePropField: item.questTemplatePropField, // 字段名
                    questTemplatePropFieldDesc: item.questTemplatePropFieldDesc, // 字段描述
                    questTemplatePropId: item.questTemplatePropId, // 配置ID
                    questTemplatePropSort: item.questTemplatePropSort, // 字段排序
                    questTemplatePropType: item.questTemplatePropType, // 组件类型
                    emptyFlag: item.emptyFlag, // 是否必填
                    enabledFlag: item.enabledFlag, // 是否显示
                    questTemplatePropDict: item.questTemplatePropDict // 字典编码
                  }
                )
                resData[elm.questTemplatePropGroupCode][item.questTemplatePropField] = '' // 初始字段值
              })
              resArr.push({
                questTemplatePropGroupType: elm.questTemplatePropGroupType,
                questTemplatePropGroupName: elm.questTemplatePropGroupName,
                questTemplatePropGroupId: elm.questTemplatePropGroupId,
                questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
                formItems: colConfig
              })
            } else { // 表格显示
              let colConfig = []
              let rowObj = {}
              resData[elm.questTemplatePropGroupCode] = []
              elm.questTemplatePropArr.forEach(colItem => {
                rowObj[colItem.questTemplatePropField] = ''
                TableRowObj[colItem.questTemplatePropField] = ''
                colConfig.push(
                  {
                    questTemplatePropField: colItem.questTemplatePropField, // 字段名
                    questTemplatePropFieldDesc: colItem.questTemplatePropFieldDesc, // 字段描述
                    questTemplatePropId: colItem.questTemplatePropId, // 配置ID
                    questTemplatePropSort: colItem.questTemplatePropSort, // 字段排序
                    questTemplatePropType: colItem.questTemplatePropType, // 组件类型
                    emptyFlag: colItem.emptyFlag, // 是否必填
                    enabledFlag: colItem.enabledFlag, // 是否显示
                    questTemplatePropDict: colItem.questTemplatePropDict // 字典编码
                  }
                )
              })
              resArr.push({
                questTemplatePropGroupType: elm.questTemplatePropGroupType,
                questTemplatePropGroupName: elm.questTemplatePropGroupName,
                questTemplatePropGroupId: elm.questTemplatePropGroupId,
                questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
                rowObj: rowObj, // 行字段信息
                colItems: colConfig
              })
            }
          }
        })
      }
      // this.$set()
      this.resData = resData
      // 数据回显
      if (this.comData.length > 0) { // 存在数据显示
        this.comData.forEach(elm => {
          if (elm.questTemplatePropGroupType === 'form') { // 表单数据回显
            elm.fieldInfoList.forEach(item => {
              this.resData[elm.questTemplatePropGroupCode][item.questTemplatePropField] = item.questTemplatePropFieldData // 初始字段值
            })
          } else { // 表格数据回显
            let firstRow = elm.fieldInfoList[0]
            let questTemplatePropFieldData = firstRow.questTemplatePropFieldData ? (firstRow.questTemplatePropFieldData).split(',') : []
            let rowLength = questTemplatePropFieldData.length
            for (let t = 0; t < rowLength; t++) {
              const obj = cloneDeep(TableRowObj)
              this.resData[elm.questTemplatePropGroupCode].push(obj)
            }

            elm.fieldInfoList.forEach(item => {
              let fieldData = item.questTemplatePropFieldData ? (item.questTemplatePropFieldData).split(',') : []
              fieldData.forEach((val, index) => {
                this.resData[elm.questTemplatePropGroupCode][index][item.questTemplatePropField] = val // 初始字段值
              })
            })
          }
        })
      }
      this.$forceUpdate()
      console.log(resArr)
      console.log('resData')
      console.log(this.resData)
      return resArr
    },
    addFiledItem (code, rowObj) {
      let row = cloneDeep(rowObj)
      for (let i in row) {
        row[i] = ''
      }
      this.resData[code].push(row)
      console.log(this.resData)
      this.$forceUpdate()
    },
    // 提交给后台数据转化
    formateResData (data) {
      let fData = {
        questSupId: this.questSupId, // 供应商ID
        questTemplateId: this.questTemplateId, // 模板ID
        groupInfoList: [] // 配置数据
      }
      if (data && data.questTemplateTabArr) {
        data.questTemplateTabArr.forEach(elm => {
          let fieldInfo = []
          elm.questTemplatePropArr.forEach(item => {
            fieldInfo.push(
              {
                questTemplatePropId: item.questTemplatePropId, // 字段名
                questTemplatePropField: item.questTemplatePropField, // 字段描述
                questTemplatePropFieldData: null, // 字段数据
                questTemplatePropFieldLable: item.questTemplatePropFieldLable // 字段额外字段
              }
            )
          })
          fData.groupInfoList.push({
            questTemplatePropGroupId: elm.questTemplatePropGroupId,
            questTemplatePropGroupCode: elm.questTemplatePropGroupCode,
            questTemplatePropGroupType: elm.questTemplatePropGroupType,
            fieldInfoList: fieldInfo // 配置信息
          })
        })
      }
      return fData
    },
    // 表单文件上传
    formUploadSuccess (file, code, fieldId) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        this.resData[code][fieldId] = fileId.toString() + '#' + fileName
      } else {
        this.resData[code][fieldId] = '' // 文件删除
      }
    },
    // 表格文件上传
    tableUploadSuccess (file, row, fieldId) {
      const { fileId = '', fileName = '' } = file || {}
      if (fileId) {
        row[fieldId] = fileId.toString() + '#' + fileName
      } else {
        row[fieldId] = '' // 表格文件删除
      }
    },
    getData () {
      let resFormateData = this.resFormateData // 预设格式
      let resData = this.resData // 填写对应组件数据
      if (resFormateData.groupInfoList && resFormateData.groupInfoList.length > 0) {
        resFormateData.groupInfoList.forEach(elm => {
          if (elm.fieldInfoList.length > 0) {
            elm.fieldInfoList.forEach(i => {
              if (elm.questTemplatePropGroupType === 'form') { // 表单配置
                // 取对应值
                let propValue = resData[elm.questTemplatePropGroupCode][i.questTemplatePropField] // 表单取值
                i.questTemplatePropFieldData = propValue
              } else { // 表格
                let propArr = resData[elm.questTemplatePropGroupCode].map(row => (row[i.questTemplatePropField]))
                i.questTemplatePropFieldData = (propArr).toString()
              }
            })
          }
        })
      }
      return resFormateData
    }
  }
}
</script>
<style scoped lang="scss">
.optDiv{
  padding:5px 0;
}
.fileInfoDiv{
  padding: 10px 0;
}
.toRequired{
  color: red;
  margin-right: 3px;
}
.formRenderFile{
  padding-top: 28px;
  .c-upload-file{
    justify-content: left !important;
  }
}
</style>
