<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <srm-dialog
        ref="drawer"
        :title="row.propertyName"
        :visible.sync="drawer"
      >
        <div class="demo-drawer__content">
          <el-form :inline="true" class="demo-form-inline" label-width="80px">
            <el-form-item :label="$t('components.viewConfig.columnWidth')">
              <el-input v-model="row.labelWidth" class="edit-input" />
            </el-form-item>
            <el-form-item :label="$t('dataConfMod.editCondition')">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('dataConfMod.editConditionDes')"
                placement="top"
              >
                <el-input v-model="row.editCondition" type="textarea" class="edit-input" />
              </el-tooltip>
            </el-form-item>
            <el-form-item :label="$t('dataConfMod.showCondition')">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('dataConfMod.showConditionDes')"
                placement="top"
              >
                <el-input v-model="row.showCondition" type="textarea" class="edit-input" />
              </el-tooltip>
            </el-form-item>
            <el-form-item :label="$t('dataConfMod.changeFontColor')" style="display:block">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('dataConfMod.fontColorDes')"
                placement="top"
              >
                <el-color-picker v-model="row.fontColor" />
              </el-tooltip>
            </el-form-item>
            <el-form-item :label="$t('dataConfMod.tableFixedData')" style="display:block">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('dataConfMod.fixedDataDes')"
                placement="top"
              >
                <el-input v-model="row.rowAttribute" type="textarea" class="edit-input" />
              </el-tooltip>
            </el-form-item>
          </el-form>
          <div class="demo-drawer__footer">
            <el-button @click="cancelForm">
              {{ $t('vendorMod.relegation.abolish') }}
            </el-button>
            <el-button type="primary" @click="handleClose">
              {{ $t('vendorMod.relegation.sure') }}
            </el-button>
          </div>
        </div>
      </srm-dialog>
      <el-form
        ref="indicatorsForm"
        class="form-fill-style"
        :model="configData"
        :rules="configDataRules"
      >
        <el-row :gutter="32">
          <el-col :span="6">
            <el-form-item
              prop="pageCode"
              :label="$t('dataConfMod.menuCode')"
            >
              <el-input v-model="configData.pageCode" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              prop="pageName"
              :label="$t('dataConfMod.menuName')"
            >
              <el-input v-model="configData.pageName" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="tabItem">
        <el-tabs
          v-model="activeTab"
          type="card"
          @tab-remove="tabRemove"
          @tab-click="tabClick(index)"
        >
          <el-tab-pane
            v-for="(item, index) in configData.formDimVOList"
            :key="item.dimCode"
            :label="item.dimName"
            :name="item.dimCode"
            :closable="curOpt !== 'view'"
            :lazy="true"
          >
            <div class="optDiv">
              <el-button
                v-if="curOpt !== 'view'"
                type="primary"
                @click="addTabItem"
              >
                {{ $t('dataConfMod.addDim') }} </el-button>
              <el-button
                v-if="curOpt !== 'view'"
                @click="editDim(index)"
              >
                {{ $t('dataConfMod.editDim') }}
              </el-button>
              <el-button
                v-if="curOpt !== 'view'"
                @click="addFiledItem(index)"
              >
                {{ $t('reportSetting.addColumn') }} </el-button>
            </div>
            <el-table
              :data="item.formAttributeList"
              style="width: 100%"
              border
              max-height="350px"
            >
              <el-table-column
                align="center"
                prop="sort"
                :label="$t('dataConfMod.sortFill')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.sort"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="columnName"
                :label="$t('dataConfMod.columnName')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.columnName"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="propertyName"
                :label="$t('dataConfMod.questTemplatePropFieldDesc')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.propertyName"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <!--      多语言参数        -->
              <el-table-column
                min-width="100px"
                :label="$t('dataConfMod.languageCode')"
                align="center"
                prop="languageCode"
              >
                <template slot-scope="{ row }">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('dataConfMod.languageCodeDes')"
                    placement="top"
                  >
                    <el-input v-model="row.languageCode" class="edit-input" />
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="componentType"
                :label="$t('dataConfMod.modeComponentType')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.componentType"
                    code="VENDOR_ATTRIBUTE_TYPE"
                    clearable
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="dictCode"
                :label="$t('dataConfMod.dictionary')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.dictCode"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="emptyFlag"
                :label="$t('dataConfMod.isRequested')"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.emptyFlag"
                    true-label="Y"
                    false-label="N"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="operation"
                :label="$t('formula.handle')"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="curOpt !== 'view'"
                    type="text"
                    @click="more(scope)"
                  >
                    {{ $t('dataConfMod.moreSetting') }} </el-button>
                  <el-button
                    v-if="curOpt !== 'view'"
                    type="text"
                    @click="deleteRowField(scope.$index, item.formAttributeList)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            @click="stagingHandle"
          >
            <!-- 保存 -->
            {{ $t('common.save') }}
          </el-button>
          <el-button @click="previewTemp">
            {{ $t('dataConfMod.previewTemplate') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <el-dialog
      :title="$t('bidMod.dimension')"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="ratingTabForm"
        :model="tabBaseInfo"
        :rules="tabBaseInfoRules"
      >
        <el-row :gutter="32">
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.modeDimCode')"
              prop="dimCode"
            >
              <el-input v-model="tabBaseInfo.dimCode" :disabled="editDimFlag" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('perfMod.dimensionName')"
              prop="dimName"
            >
              <el-input v-model="tabBaseInfo.dimName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('perfMod.dimensionType')"
              prop="dimType"
            >
              <DictSelect
                v-model="tabBaseInfo.dimType"
                code="QUEST_TEMPLATE_PROP_GROUP_TYPE"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('components.viewConfig.seq')"
              prop="orderNum"
            >
              <el-input v-model="tabBaseInfo.orderNum" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.originalDimFlag')"
              prop="originalDimFlag"
            >
              <DictSelect
                v-model="tabBaseInfo.originalDimFlag"
                code="YES_OR_NO"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              :label="this.$t('dataConfMod.dimTitleShowFlag')"
              prop="dimTitleShowFlag"
            >
              <DictSelect
                v-model="tabBaseInfo.dimTitleShowFlag"
                code="YES_OR_NO"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="tabBaseInfo.dimType == 'table'" :span="8">
            <el-form-item
              :label="$t('dataConfMod.addButtonFlag')"
              prop="addButtonFlag"
            >
              <DictSelect
                v-model="tabBaseInfo.addButtonFlag"
                code="YES_OR_NO"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmAdd"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
    <srm-dialog
      :title="$t('dataConfMod.templatePreview')"
      size="large"
      :destroy-on-close="true"
      :visible.sync="previewVisible"
      :close-on-click-modal="false"
    >
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <template v-for="(item,index) in previewModel.formDimVOList">
          <el-collapse-item
            :ref="item.dimName"
            :key="item.dimCode"
            :title="item.dimName"
            :name="item.dimCode"
            :class="showTileFlag(item.dimTitleShowFlag)"
          >
            <!-- 如果是表单的话显示 -->
            <ModelConfigForm
              v-if="item.dimType === 'form'"
              :ref="item.dimCode"
              :dim-config="previewModel.dimConfigMap[item.dimCode]"
              :form-value="dimDataValue"
            />
            <!-- 如果是表格的话显示 -->
            <ModelConfigTable
              v-if="item.dimType === 'table'"
              :ref="item.dimCode"
              :dim-config="previewModel.dimConfigMap[item.dimCode]"
              :index="index"
              :table-value="dimDataValue"
            />
          </el-collapse-item>
        </template>
      </el-collapse>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="previewVisible = false"
        >
          {{
            $t('common.confirm')
          }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'ModelConfigEdit',
  components: { CToolbar, ModelConfigForm, ModelConfigTable },
  mixins: [tabTodoMixin],
  data () {
    return {
      drawer: false,
      index: 0,
      drawerTitle: '',
      indexF: 0,
      row: {
        propertyName: '',
        rowAttribute: ''
      },
      previewModel: {},
      activeDims: [],
      configDataRules: {
        pageCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        pageName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      templatePropTypeList: [],
      templateGroupTypeList: [],
      mulSelection: '',
      previewVisible: false,
      isSizeChanged: false,
      displayTemplateItem: [],
      filterForm: {},
      formLabelWidth: '120px',
      dialogFormVisible: false,
      editDimFlag: false,
      dataCount: 0,
      queryTotal: -1, // 共几条 -1就是问号
      viewIndex: 1,
      viewSize: 10,
      queryParam: {
        pageNum: 1,
        pageSize: 10
      },
      questTemplateTypeList: [],
      tabBaseInfo: {
        dimTitleShowFlag: '', // 是否展示维度标题
        originalDimFlag: '', // 是否固定维度
        orderNum: '', // 页签名称
        dimName: '', // 页签名称
        dimCode: '', // 页签编码
        dimType: '', // 页签类型
        addButtonFlag: 'Y' // 是否显示新增按钮
      },
      tabBaseInfoRules: {
        dimTitleShowFlag: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        originalDimFlag: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        dimName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        orderNum: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        dimCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        dimType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      tempList: [
        { value: 'ds', label: this.$t('dataConfMod.production') },
        { value: 'ddd', label: this.$t('dataConfMod.nonProduction') }
      ],
      activeTab: 'a1',
      configData: {
        pageCode: '',
        pageName: '',
        formDimVOList: [{
          dimCode: 'a1'
        }
        ]
      },
      curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
      curOpt: 'view'
    }
  },
  // 页面创建
  created () {
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      let pageCode = this.$attrs.params.row.pageCode
      this.configData.pageCode = pageCode
      this.getDetail(pageCode)
    }
  },
  // 页面挂载完成
  mounted () {
    // 设置tab默认激活的页签
    if (this.configData.formDimVOList.length > 0) {
      this.activeTab = this.configData.formDimVOList[0].dimCode
    }
  },
  methods: {
    tabClick (index) {
      this.indexF = index
    },
    handleClose () {
      this.configData.formDimVOList[this.indexF][this.index] = this.row
      this.drawer = false
    },
    cancelForm () {
      this.drawer = false
    },
    more (scope) {
      this.row = scope.row
      this.index = scope.$index
      this.drawer = true
    },
    showTileFlag (dimTitleShowFlag) {
      if (dimTitleShowFlag === 'N') {
        return 'noShow'
      }
      return ''
    },
    formatData (row, column, cellValue) {
      if (!this.questTemplateTypeList.filter((v) => v.value === cellValue)[0]) {
        return null
      }
      return cellValue
        ? this.questTemplateTypeList.filter((v) => v.value === cellValue)[0].label
        : ''
    },
    reset () {
      this.filterForm = {}
    },
    handleSelectionChange (selection) {
      this.mulSelection = selection
    },
    // 调查模板类型切换
    getQuestTemplateType (val) {
      if (val) {
        let row = this.questTemplateTypeList.find((item) => {
          return item.value === val
        })
        if (row) {
          this.configData.questTemplateTypeName = row.label
        }
      }
    },
    // 新增标签
    addTabItem () {
      this.tabBaseInfo.dimName = '' // 页签名称
      this.tabBaseInfo.dimCode = '' // 页签编码
      this.tabBaseInfo.dimType = '' // 页签类型
      this.tabBaseInfo.orderNum = '' // 顺序
      this.tabBaseInfo.originalDimFlag = '' // 是否固定维度
      this.tabBaseInfo.dimTitleShowFlag = '' // 是否展示维度标题
      this.dialogFormVisible = true
      this.editDimFlag = false
    },
    // 新增维度确定
    confirmAdd () {
      // 编辑维度
      this.$refs.ratingTabForm.validate((valid) => {
        if (valid) {
          // 判断,现在的dimCode不能与其他dimCode重复,但不包含自己
          if (!this.editDimFlag) {
            if (this.configData.formDimVOList.some(item => item.dimCode === this.tabBaseInfo.dimCode)) {
              this.$message.error(this.$t('dataConfMod.dimCodeRepeat'))
              return
            }
            this.configData.formDimVOList.push({
              dimName: this.tabBaseInfo.dimName, // 页签名称
              dimCode: this.tabBaseInfo.dimCode, // 页签编码
              dimType: this.tabBaseInfo.dimType, // 页签类型
              orderNum: this.tabBaseInfo.orderNum, // 页签类型
              originalDimFlag: this.tabBaseInfo.originalDimFlag, // 是否固定维度
              dimTitleShowFlag: this.tabBaseInfo.dimTitleShowFlag, // 是否展示维度标题
              addButtonFlag: this.tabBaseInfo.addButtonFlag,
              deleteFlag: true, // 页签可删除属性
              showFlag: 'Y', // 是否显示该页签
              fillOneLineFlag: 'N', // 是否必填一行(类型为明细表类型的显示)
              formAttributeList: []
            })
          } else {
            // 找到  赋值
            this.configData.formDimVOList.forEach((elm) => {
              // 改了code?
              if (elm.dimCode === this.tabBaseInfo.dimCode) {
                elm.dimName = this.tabBaseInfo.dimName // 页签名称
                elm.dimCode = this.tabBaseInfo.dimCode // 页签编码
                elm.dimType = this.tabBaseInfo.dimType // 页签类型
                elm.orderNum = this.tabBaseInfo.orderNum // 顺序
                elm.originalDimFlag = this.tabBaseInfo.originalDimFlag // 是否固定维度
                elm.dimTitleShowFlag = this.tabBaseInfo.dimTitleShowFlag // 是否展示维度标题
                elm.addButtonFlag = this.tabBaseInfo.addButtonFlag
              }
            })
            this.editDimFlag = false
          }
          this.dialogFormVisible = false
          this.activeTab = this.tabBaseInfo.dimCode
        } else {
          this.$message.error(this.$t('dataConfMod.fillDimInfo'))
        }
      })
    },
    // 删除标签
    tabRemove (dimCode) {
      if (dimCode) {
        let delIndex = this.configData.formDimVOList.findIndex(
          (i) => i.dimCode == dimCode
        )
        this.configData.formDimVOList.splice(delIndex, 1)
        let arrayLength = this.configData.formDimVOList.length - 1
        if (arrayLength !== -1) {
          this.activeTab =
            this.configData.formDimVOList[arrayLength].dimCode
        }
      }
    },
    // 编辑维度信息
    editDim (index) {
      this.dialogFormVisible = true
      this.editDimFlag = true
      this.tabBaseInfo.dimName = this.configData.formDimVOList[index].dimName
      this.tabBaseInfo.dimCode = this.configData.formDimVOList[index].dimCode
      this.tabBaseInfo.dimType = this.configData.formDimVOList[index].dimType
      this.tabBaseInfo.orderNum = this.configData.formDimVOList[index].orderNum
      this.tabBaseInfo.originalDimFlag = this.configData.formDimVOList[index].originalDimFlag
      this.tabBaseInfo.dimTitleShowFlag = this.configData.formDimVOList[index].dimTitleShowFlag
    },
    // 新增当前标签下面的字段配置信息
    addFiledItem (index) {
      this.configData.formDimVOList[index].formAttributeList.push({
        sort: '', // 排序号
        columnName: '', // 字段编码
        propertyName: '', // 字段描述
        componentType: '', // 组件类型
        dictCode: '', // 字典
        emptyFlag: 'N' // 是否必输
      })
    },
    // 通过id查询模板数据
    getDetail (pageCode) {
      this.$http({
        url: '/api-base/base/form_page/getModelConfig',
        method: 'GET',
        params: { pageCode: pageCode },
        loading: true
      })
        .then((res) => {
          if (res.data) {
            this.configData = res.data
          }
          this.activeTab = this.configData.formDimVOList[0].dimCode
        })
    },
    stagingHandle () {
      this.$refs.indicatorsForm.validate((valid) => {
        if (valid) {
          // 判断下拉的设置要维护对应字典
          let vlCount = 0
          if (this.configData.formDimVOList.length > 0) {
            for (let i = 0; i < this.configData.formDimVOList.length; i++) {
              let item = this.configData.formDimVOList[i]
              let dimName = item.dimName // 标签名称
              let formAttributeList = item.formAttributeList
              if (formAttributeList.length > 0) {
                for (let j = 0; j < formAttributeList.length; j++) {
                  let elm = formAttributeList[j]
                  let sort = elm.sort
                  if (!sort) {
                    this.$message.error(dimName + this.$t('dataConfMod.tabNoEmpty'))
                    return false
                  }
                  let columnName = elm.columnName
                  if (!columnName) {
                    this.$message.error(dimName + this.$t('dataConfMod.codeNoEmpty'))
                    return false
                  }
                  let propertyName = elm.propertyName
                  let componentType = elm.componentType // 字段类型
                  if (!componentType) {
                    this.$message.error(dimName + this.$t('dataConfMod.typeNoEmpty'))
                    return false
                  }
                  if (componentType === 'select') {
                    if (!elm.dictCode) {
                      this.$message.error(
                        this.$t('dataConfMod.dropdownRequired') +
                        dimName +
                        this.$t('dataConfMod.tabDown') +
                        propertyName +
                        this.$t('dataConfMod.dictInfo')
                      )
                      vlCount += 1
                      return false
                    }
                  }
                }
              }
              if (vlCount > 0) {
                return false
              }
            }
          } else {
            this.$message.error(this.$t('dataConfMod.leastOneConfig'))
            return false
          }
          if (vlCount > 0) {
            return false
          }

          this.$http({
            url: '/api-base/base/form_page/saveOrUpdateModel',
            method: 'POST',
            data: this.configData,
            loading: true,
            check: 'Y'
          })
            .then(() => {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('modelConfig.getQuerydata')
            })
        } else {
          this.$message.error(this.$t('dataConfMod.notEmpty'))
        }
      })
    },
    deleteRowField (index, propArr) {
      propArr.splice(index, 1)
    },
    // 预览模板
    previewTemp () {
      if (this.configData.formPageId) {
        this.previewVisible = true
        this.configData.formDimVOList.forEach(item => {
          // 默认展示所有动态维度
          this.activeDims.push(item.dimCode)
        })
        this.previewModel = this.configData
      } else {
        this.$message({
          type: 'success',
          message: this.$t('dataConfMod.saveAndPreview')
        }) // 提交成功
      }
    }
  }
}
</script>
<style scoped lang="scss">
.demo-drawer__content{
  padding: 10px 15px;
  .edit-input{
    width: 300px;
  }
}

.demo-drawer__footer{
  margin-top: 20px;
  text-align: center;
}

.form-fill-style {
  padding: 15px;
}

.tabItem {
  padding: 10px;
  position: relative;
  margin-bottom: 50px;

  .optDiv {
    padding: 0px 0 16px;

    .el-switch,
    .el-button {
      // margin-right: 10px;
    }
  }

  .addTabBtn {
    position: absolute;
    left: 10px;
    top: -24px;
    z-index: 100;
  }
}

.page-bar {
  height: auto !important;
}
.noShow > :first-child {
  display: none;
}
</style>
