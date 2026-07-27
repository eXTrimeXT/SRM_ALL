<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="indicatorsForm"
        class="form-fill-style"
        :model="configData"
        :rules="configDataRules"
      >
        <srm-row :gutter="32">
          <srm-col :span="6">
            <el-form-item
              :label="$t('quest.questTemplateCode')"
              prop="questTemplateCode"
            >
              <el-input
                v-model="configData.questTemplateCode"
                disabled
              />
            </el-form-item>
          </srm-col>
          <srm-col :span="6">
            <el-form-item
              :label="$t('dataConfMod.questTemplateName')"
              :prop="questTemplateName"
            >
              <el-input
                v-model="configData.questTemplateName"
                :disabled="curOpt === 'view'"
              />
            </el-form-item>
          </srm-col>
          <srm-col :span="6">
            <el-form-item
              :label="$t('vendorMod.questTemplateType')"
              prop="questTemplateType"
            >
              <DictSelect
                v-model="configData.questTemplateType"
                code="QUEST_TEMPLATE_TYPE"
                :disabled="curOpt === 'view'"
                @change="getQuestTemplateType"
              />
            </el-form-item>
          </srm-col>
          <srm-col :span="6">
            <el-form-item
              :label="$t('dataConfMod.quoteQuestTemplateName')"
              prop="quoteQuestTemplateName"
            >
              <div class="inputSelect mt24">
                <el-input
                  v-model="configData.quoteQuestTemplateName"
                  disabled
                />
                <el-button
                  icon="el-icon-search"
                  class="selectBtn"
                  :disabled="curOpt === 'view'"
                  @click="openDialog('click', $event)"
                />
              </div>
            </el-form-item>
          </srm-col>
          <srm-col :span="6">
            <el-form-item
              :label="$t('dataConfMod.orgId')"
              prop="organizationIds"
            >
              <OrganizationSelector
                ref="organizationSelector"
                v-model="configData.organizationIds"
                :parent-id="-1"
                :placeholder="$t('common.pleaseSelect')"
                node-type="OU"
                multiple="true"
                :disabled="curOpt === 'view'"
                @select="selectHandler"
              />
            </el-form-item>
          </srm-col>
          <srm-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.remark')"
              prop="questTemplateRemark"
            >
              <el-input
                v-model="configData.questTemplateRemark"
                type="textarea"
                :rows="2"
                :disabled="curOpt === 'view'"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <div class="tabItem">
        <el-button
          v-if="curOpt !== 'view'"
          class="addTabBtn"
          type="primary"
          @click="addTabItem"
        >
          {{ $t('common.add') }}
        </el-button>
        <el-tabs
          v-model="activeTab"
          type="card"
          @tab-remove="tabRemove"
        >
          <el-tab-pane
            v-for="(item, index) in configData.questTemplateTabArr"
            :key="item.questTemplatePropGroupCode"
            :label="item.questTemplatePropGroupName"
            :name="item.questTemplatePropGroupCode"
            :closable="curOpt !== 'view'"
            :lazy="true"
          >
            <div class="optDiv">
              <el-button
                v-if="curOpt !== 'view'"
                type="primary"
                @click="addFiledItem(index)"
              >
                {{ $t('common.add') }}
              </el-button>
              <el-switch
                v-if="curOpt !== 'view'"
                v-model="item.showFlag"
                active-value="Y"
                inactive-value="N"
                active-text="是否显示该页签"
              />
              <!-- 显示类型为表格的时候显示 -->
              <el-switch
                v-if="item.questTemplatePropGroupType === 'table'"
                v-model="item.fillOneLineFlag"
                active-value="Y"
                inactive-value="N"
                active-text="是否必填一行"
              />
            </div>
            <el-table
              :data="item.questTemplatePropArr"
              style="width: 100%"
              border
              max-height="350px"
            >
              <el-table-column
                align="center"
                prop="questTemplatePropSort"
                :label="`*${$t('dataConfMod.sorting')}`"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.questTemplatePropSort"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="questTemplatePropField"
                :label="`*${$t('contract_mod.fieldCode')}`"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.questTemplatePropField"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="questTemplatePropFieldDesc"
                :label="$t('dataConfMod.questTemplatePropFieldDesc')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.questTemplatePropFieldDesc"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="questTemplatePropType"
                :label="`*${$t('contract_mod.fieldType')}`"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.questTemplatePropType"
                    code="QUEST_TEMPLATE_PROP_TYPE"
                    clearable
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="questTemplatePropDict"
                :label="$t('dataConfMod.dictionary')"
              >
                <template slot-scope="scope">
                  <QuickSearch
                    :show-input="scope.row.questTemplatePropDict"
                    show-key="dictName"
                    :scope-data="scope.row"
                    :disabled="curOpt === 'view' || scope.row.questTemplatePropType !== 'select'"
                    name="scc_base_dict"
                    @close-quicksearch="getDictObj"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="questTemplatePropComponent"
                :label="$t('dataConfMod.componentProperty')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.questTemplatePropComponent"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="enabledFlag"
                :label="$t('dataConfMod.enabledUse')"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.enabledFlag"
                    true-label="Y"
                    false-label="N"
                    :disabled="curOpt === 'view'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="emptyFlag"
                :label="$t('dataConfMod.emptyFlag')"
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
                :label="$t('common.operation')"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="curOpt !== 'view'"
                    type="text"
                    @click="deleteRowField(scope.$index, item.questTemplatePropArr)"
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
      :title="$t('dataConfMod.addNewTag')"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="ratingTabForm"
        :model="tabBaseInfo"
        :rules="tabBaseInfoRules"
      >
        <srm-row :gutter="32">
          <!--          <srm-col :span="8">-->
          <!--            <el-form-item label="页签编码" prop="tabCode">-->
          <!--              <el-input v-model="tabBaseInfo.tabCode"/>-->
          <!--            </el-form-item>-->
          <!--          </srm-col>-->
          <srm-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.tabName')"
              prop="tabName"
            >
              <el-input v-model="tabBaseInfo.tabName" />
            </el-form-item>
          </srm-col>
          <srm-col :span="8">
            <el-form-item
              :label="$t('dataConfMod.tabType')"
              prop="tabType"
            >
              <!--              <el-select v-model="tabBaseInfo.tabType" clearable>-->
              <!--                <el-option label="表单" value="form"/>-->
              <!--                <el-option label="表格" value="table"/>-->
              <!--              </el-select>-->
              <DictSelect
                v-model="tabBaseInfo.tabType"
                code="QUEST_TEMPLATE_PROP_GROUP_TYPE"
                clearable
              />
            </el-form-item>
          </srm-col>
        </srm-row>
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

    <!-- 弹框区域-->
    <srm-dialog
      :title="$t('vendorMod.questTemplateId')"
      width="90%"
      size="large"
      :visible.sync="dialogTemplateVisible"
      :close-on-click-modal="false"
    >
      <div>
        <el-form
          ref="filterForm"
          :model="filterForm"
          label-width="80px"
          class="the_filter_form"
        >
          <srm-row>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('dataConfMod.templateName')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="filterForm.questTemplateName" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('contractMod.templType')"
                prop="questTemplateType"
              >
                <DictSelect
                  v-model="filterForm.questTemplateType"
                  code="QUEST_TEMPLATE_TYPE"
                  @change="getQuestTemplateType"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-button
                type="primary"
                @click="queryItemList(1)"
              >
                {{ $t('common.search') }}
              </el-button>
              <el-button
                @click="reset('form')"
              >
                {{ $t('common.reset') }}
              </el-button>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <div>
        <div style="padding-top: 10px">
          <el-table
            ref="multipleTable"
            :data="displayTemplateItem"
            style="width: 100%"
            border
            height="251px"
            highlight-current-row
            @selection-change="handleSelectionChange"
          >
            <el-table-column
              type="selection"
              width="50"
              fixed="left"
            />
            <el-table-column
              align="center"
              type="index"
              width="55"
              :label="$t('common.sort')"
            />
            <el-table-column
              align="center"
              prop="questTemplateCode"
              :label="$t('dataConfMod.templateCode')"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="questTemplateName"
              :label="$t('dataConfMod.templateName')"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="questTemplateType"
              :label="$t('contractMod.templType')"
              :formatter="formatData"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              prop="questTemplateRemark"
              :label="$t('dataConfMod.remark')"
              show-overflow-tooltip
            />
          </el-table>
        </div>
        <el-footer class="page-bar">
          <PagerBar
            ref="pager"
            :data-count="dataCount"
            :query-total="queryTotal"
            :page-index="viewIndex"
            :page-size="viewSize"
            :page-count="pageCount"
            :page-query="pageQuery"
          />
        </el-footer>
      </div>

      <template #footer>
        <el-button
          @click="cancelDialog"
        >
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="closeDialog"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </srm-dialog>
    <srm-dialog
      :title="$t('dataConfMod.templatePreview')"
      size="large"
      :destroy-on-close="true"
      :visible.sync="previewVisible"
      :close-on-click-modal="false"
    >
      <!-- 根据模板配置渲染相关组件 -->
      <renderForm
        v-if="previewVisible"
        :quest-template-id="configData.questTemplateId"
        :disabled="true"
        opt-type="edit"
      />
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
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { tabTodoMixin } from '@/utils/mixins'
import http from '@/utils/axios/http'
import PagerBar from 'lib@/components/Pager'
import { parseTime, adaptDictData } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import renderForm from './renderForm'

export default {
  name: 'QuestTemplateDetail',
  components: { CToolbar, http, OrganizationSelector, PagerBar, renderForm, QuickSearch },
  mixins: [tabTodoMixin],
  data () {
    return {
      configDataRules: {
        questTemplateName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        questTemplateType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        organizationIds: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      templatePropTypeList: [],
      templateGroupTypeList: [],
      mulSelection: '',
      previewVisible: false,
      isSizeChanged: false,
      dialogTemplateVisible: false,
      displayTemplateItem: [],
      filterForm: {},
      formLabelWidth: '120px',
      dialogFormVisible: false,
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
        tabName: '', // 页签名称
        tabCode: '', // 页签编码
        tabType: '' // 页签类型
      },
      tabBaseInfoRules: {
        tabName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        // tabCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
        tabType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
      },
      tempList: [
        { value: 'ds', label: this.$t('dataConfMod.production') },
        { value: 'ddd', label: this.$t('dataConfMod.nonProduction') }
      ],
      activeTab: '',
      configData: {
        questTemplateId: '', // 调查模板编码
        questTemplateCode: '', // 调查模板编码
        quoteQuestTemplateId: null,
        quoteQuestTemplateName: '',
        questTemplateName: '', // 调查模板名称
        questTemplateType: '', // 调查模板类型
        questTemplateTypeName: '', // 调查模板类型名称
        otherTemplate: '', // 引用其他调查模板
        questTemplateRemark: '', // 备注
        organizationIds: null,
        questTemplateOrgArr: [],
        questTemplateTabArr: [
          {
            questTemplatePropGroupName: this.$t('priceModel.costElement.baseInfo'), // 页签名称
            questTemplatePropGroupCode: 'baseInfo', // 页签编码
            questTemplatePropGroupType: 'form', // 页签类型
            deleteFlag: true, // 页签可删除属性
            showFlag: 'Y', // 是否显示该页签
            fillOneLineFlag: 'N', // 是否必填一行(类型为明细表类型的显示)
            questTemplatePropArr: [
              // 字段配置信息
              {
                questTemplatePropSort: '', // 排序号
                questTemplatePropField: '', // 字段编码
                questTemplatePropFieldDesc: '', // 字段描述
                questTemplatePropType: '', // 组件类型
                questTemplatePropDict: '', // 字典
                questTemplatePropComponent: '', // 组件属性
                enabledFlag: 'Y', // 是否启动
                emptyFlag: 'N' // 是否必输
              }
            ]
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
      // this.curOrderId = this.$attrs.params.row.reviewFormId
      let questTemplateId = this.$attrs.params.row.questTemplateId
      this.configData.questTemplateId = questTemplateId
      this.getDetail(questTemplateId)
    }
  },
  // 页面挂载完成
  mounted () {
    // 设置tab默认激活的页签
    if (this.configData.questTemplateTabArr.length > 0) {
      this.activeTab = this.configData.questTemplateTabArr[0].questTemplatePropGroupCode
    }
  },
  methods: {
    getDictObj (val, data) {
      console.log(val)
      data.questTemplatePropDict = val.dictCode
    },
    formatData (row, column, cellValue, index) {
      return this.$getDictLabel('QUEST_TEMPLATE_TYPE', cellValue)
    },
    pageCount () {
      // 查询共几条的方法
      // this.query('getCount');
    },
    pageQuery (opr, size) {
      // 查询数据的方法
      let allCount = Math.ceil(this.queryTotal / this.viewSize) // 总页数---作为判断依据
      if (opr == 'prev' && this.queryParam.pageNum == 1) return
      if (opr == 'next' && this.queryParam.pageNum == allCount) return

      switch (opr) {
        case 'prev':
          this.queryParam.pageNum -= 1
          this.viewIndex -= 1
          break
        case 'next':
          this.queryParam.pageNum += 1
          this.viewIndex += 1
          break
        default:
          this.viewSize = size
          this.viewIndex = 1
          this.queryParam.pageSize = size
          this.queryParam.pageNum = 1
      }
      this.isSizeChanged = true

      this.queryItemList(opr)
    },
    reset (type) {
      this.filterForm = {}
    },
    openDialog (type) {
      this.queryItemList()
    },
    closeDialog () {
      console.log(this.mulSelection)
      if (!this.mulSelection) {
        this.$message({
          type: 'error',
          message: this.$t('common.pleaseSelectOne') // 请选择一条数据！
        })
        return
      }
      this.dialogTemplateVisible = false
      let questTemplateId = this.mulSelection[0].questTemplateId
      // this.configData.quoteQuestTemplateId = questTemplateId
      this.configData.quoteQuestTemplateName = this.mulSelection[0].questTemplateName
      this.getDetail(questTemplateId, 'selectDialog')
    },
    cancelDialog () {
      this.dialogTemplateVisible = false
    },
    queryItemList (num) {
      const data = { ...this.queryParam, ...this.filterForm }
      if (num === 1) {
        data.pageNum = 1
        this.viewIndex = 1
        this.queryParam.pageNum = 1
      }
      this.$http({
        url: '/api-sup/quest/questTemplate/listPageByParm',
        method: 'POST',
        data: data,
        loading: true
      }).then((res) => {
        this.dialogTemplateVisible = true
        this.displayTemplateItem = res.data.list
        this.queryTotal = res.data.total
        this.dataCount = res.data.list.length
      })
    },
    handleSelectionChange (selection) {
      if (selection.length > 1) {
        this.$refs.multipleTable.clearSelection()
        this.$refs.multipleTable.toggleRowSelection(selection.pop())
        return
      }
      if (selection[0]) {
        this.mulSelection = selection
      }
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
    selectHandler (node, value, scope) {
      this.configData.questTemplateOrgArr = []
      if (node) {
        node.forEach((elm) => {
          this.configData.questTemplateOrgArr.push({
            questTemplateId: this.configData.questTemplateId,
            orgId: elm.organizationId,
            orgCode: elm.organizationCode,
            orgName: elm.organizationName
          })
        })
      }
    },
    // 新增标签
    addTabItem () {
      this.tabBaseInfo.tabName = '' // 页签名称
      this.tabBaseInfo.tabCode = '' // 页签编码
      this.tabBaseInfo.tabType = '' // 页签类型
      this.dialogFormVisible = true
    },
    confirmAdd () {
      this.$refs.ratingTabForm.validate((valid) => {
        if (valid) {
          this.tabBaseInfo.tabCode = 'code' + this.configData.questTemplateTabArr.length + 1
          this.configData.questTemplateTabArr.push({
            questTemplatePropGroupName: this.tabBaseInfo.tabName, // 页签名称
            questTemplatePropGroupCode: this.tabBaseInfo.tabCode, // 页签编码
            questTemplatePropGroupType: this.tabBaseInfo.tabType, // 页签类型
            deleteFlag: true, // 页签可删除属性
            showFlag: 'Y', // 是否显示该页签
            fillOneLineFlag: 'N', // 是否必填一行(类型为明细表类型的显示)
            questTemplatePropArr: []
          })
          this.dialogFormVisible = false
          this.activeTab = this.tabBaseInfo.tabCode
        } else {
          this.$message.error(this.$t('dataConfMod.maintainInformation'))
        }
      })
    },
    // 删除标签
    tabRemove (tabName) {
      if (tabName) {
        let delIndex = this.configData.questTemplateTabArr.findIndex(
          (i) => i.questTemplatePropGroupCode == tabName
        )
        if (delIndex > -1) {
          this.configData.questTemplateTabArr.splice(delIndex, 1)
        }
        let arrayLength = this.configData.questTemplateTabArr.length - 1
        if (arrayLength !== -1) {
          this.activeTab =
            this.configData.questTemplateTabArr[arrayLength].questTemplatePropGroupCode
        }
      }
    },
    // 新增当前标签下面的字段配置信息
    addFiledItem (index) {
      this.configData.questTemplateTabArr[index].questTemplatePropArr.push({
        questTemplatePropSort: '', // 排序号
        questTemplatePropField: '', // 字段编码
        questTemplatePropFieldDesc: '', // 字段描述
        questTemplatePropType: '', // 组件类型
        questTemplatePropDict: '', // 字典
        questTemplatePropComponent: '', // 组件属性
        enabledFlag: 'Y', // 是否启动
        emptyFlag: 'N' // 是否必输
      })
    },
    // 通过id查询模板数据
    getDetail (questTemplateId, type) {
      this.$http({
        url: '/api-sup/quest/questTemplate/questTemplateData',
        method: 'GET',
        params: { questTemplateId: questTemplateId },
        loading: true
      })
        .then((res) => {
          // 引用其他模板，只需要模板的组和字段，不用模板ID，名称，组织等
          if (type === 'selectDialog') {
            this.configData.questTemplateTabArr = res.data.questTemplateTabArr
            this.activeTab = res.data.questTemplateTabArr[0].questTemplatePropGroupCode
          } else {
            if (res.data) {
              this.configData = res.data
              let templateOrgArr = res.data.questTemplateOrgArr
              if (templateOrgArr) {
                this.configData.questTemplateOrgArr = []
                templateOrgArr.forEach((elm) => {
                  this.configData.questTemplateOrgArr.push({
                    questTemplateId: this.configData.questTemplateId,
                    orgId: elm.orgId,
                    orgCode: elm.orgCode,
                    orgName: elm.orgName
                  })
                })
              }
            }
            this.activeTab = this.configData.questTemplateTabArr[0].questTemplatePropGroupCode
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    stagingHandle () {
      this.$refs.indicatorsForm.validate((valid) => {
        if (valid) {
          // 判断下拉的设置要维护对应字典
          let vlCount = 0
          if (this.configData.questTemplateTabArr.length > 0) {
            for (let i = 0; i < this.configData.questTemplateTabArr.length; i++) {
              let item = this.configData.questTemplateTabArr[i]
              let questTemplatePropGroupName = item.questTemplatePropGroupName // 标签名称
              let questTemplatePropArr = item.questTemplatePropArr
              if (questTemplatePropArr.length > 0) {
                for (let j = 0; j < questTemplatePropArr.length; j++) {
                  let elm = questTemplatePropArr[j]
                  let questTemplatePropSort = elm.questTemplatePropSort
                  if (!questTemplatePropSort) {
                    this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.tabNoEmpty'))
                    return false
                  }
                  let questTemplatePropField = elm.questTemplatePropField
                  if (!questTemplatePropField) {
                    this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.codeNoEmpty'))
                    return false
                  }
                  let questTemplatePropFieldDesc = elm.questTemplatePropFieldDesc
                  let questTemplatePropType = elm.questTemplatePropType // 字段类型
                  if (!questTemplatePropType) {
                    this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.typeNoEmpty'))
                    return false
                  }
                  if (questTemplatePropType === 'select') {
                    if (!elm.questTemplatePropDict) {
                      this.$message.error(
                        this.$t('dataConfMod.dropdownRequired') +
                          questTemplatePropGroupName +
                          this.$t('dataConfMod.tabDown') +
                          questTemplatePropFieldDesc +
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
            url: '/api-sup/quest/questTemplate/saveQuestTemplateData',
            method: 'POST',
            data: this.configData,
            loading: true,
            check: 'Y'
          })
            .then((res) => {
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('questTemplateList.getQueryData')
            })
            .catch((err) => {
              console.log(err)
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
      if (this.configData.questTemplateId) {
        this.previewVisible = true
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
.form-fill-style {
  padding: 15px;
}

.tabItem {
  padding: 10px;
  position: relative;
  margin-bottom: 50px;

  .optDiv {
    padding: 10px 0;

    .el-switch,
    .el-button {
      margin-right: 10px;
    }
  }

  .addTabBtn {
    position: absolute;
    right: 10px;
    top: 9px;
    z-index: 100;
  }
}

.page-bar {
  height: auto !important;
}

</style>
