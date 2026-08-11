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
          <srm-col>
            <!-- 调查模板编码 -->
            <el-form-item :label="$t('quest.questTemplateCode')" prop="questTemplateCode">
              <el-input v-model="configData.questTemplateCode" disabled />
            </el-form-item>
          </srm-col>
          <srm-col>
            <!-- 调查模板名称 -->
            <el-form-item :label="$t('dataConfMod.questTemplateName')" prop="questTemplateName">
              <el-input v-model="configData.questTemplateName" :disabled="curOpt === 'view'" />
            </el-form-item>
          </srm-col>
          <srm-col>
            <!-- 调查模板类型 -->
            <el-form-item
              :label="$t('vendorMod.questTemplateType')"
              prop="questTemplateType"
            >
              <dict-select
                v-model="configData.questTemplateType"
                code="QUEST_TEMPLATE_TYPE"
                :disabled="curOpt === 'view'"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <!-- 模板引用 -->
            <el-form-item
              :label="$t('dataConfMod.quoteQuestTemplateName')"
              prop="quoteQuestTemplateName"
            >
              <div class="inputSelect mt24">
                <!-- 点击右侧图标选择模板 -->
                <el-input v-model="configData.quoteQuestTemplateName" :placeholder="$t('productionPrepare.questTemplateManageTip1')" disabled/>
                <el-button icon="el-icon-search" class="selectBtn" :disabled="curOpt === 'view'" @click="openDialog('click', $event)" />
              </div>
            </el-form-item>
          </srm-col>
          <srm-col>
            <!-- 业务实体 -->
            <el-form-item
              :label="$t('components.organization.ORG')"
              prop="organizationIds"
            >
              <OrganizationSelector
                ref="organizationSelector"
                v-model="configData.organizationIds"
                :parentId="-1"
                :placeholder="$t('common.pleaseSelect')"
                nodeType="OU"
                :multiple="true"
                :disabled="curOpt === 'view'"
                @select="selectHandler"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 备注 -->
            <el-form-item :label="$t('components.eio.headers.remark')" prop="questTemplateRemark">
              <el-input v-model="configData.questTemplateRemark" type="textarea" :rows="2" :disabled="curOpt === 'view'" />
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
          <!-- 新增 -->
          {{ $t("common.add") }}
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
                <!-- 新增 -->
                {{ $t("common.add") }}
              </el-button>
              <!-- 是否显示该页签 -->
              <el-switch
                v-if="curOpt !== 'view'"
                v-model="item.showFlag"
                active-value="Y"
                inactive-value="N"
                :active-text="$t('vendorMod.displayTab')"
              />
              <!-- 显示类型为表格的时候显示 -->
              <!-- 是否必填一行 -->
              <el-switch
                v-if="item.questTemplatePropGroupType==='table'"
                v-model="item.fillOneLineFlag"
                :disabled="curOpt === 'view'"
                active-value="Y"
                inactive-value="N"
                :active-text="$t('vendorMod.aLineRequired')"
              />
            </div>
            <el-table
              :data="item.questTemplatePropArr"
              style="width: 100%;"
              border
              max-height="350px"
            >
            <!-- *排序 -->
              <el-table-column align="center" prop="questTemplatePropSort" :label="$t('dataConfMod.sortFill')">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.questTemplatePropSort" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- *字段编码 -->
              <el-table-column align="center" prop="questTemplatePropField" label="$t('dataConfMod.columnName')">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.questTemplatePropField" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 字段描述 -->
              <el-table-column align="center" prop="questTemplatePropFieldDesc" :label="$t('dataConfMod.questTemplatePropFieldDesc')">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.questTemplatePropFieldDesc" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- *字段类型 -->
              <el-table-column align="center" prop="questTemplatePropType" :label="$t('dataConfMod.modeComponentType')">
                <template slot-scope="scope">
                  <dict-select
                    v-model="scope.row.questTemplatePropType"
                    :disabled="curOpt === 'view'"
                    code="QUEST_TEMPLATE_PROP_TYPE"
                    @change="value => changePropType(value,scope)"
                  />
                </template>
              </el-table-column>
              <!-- 字典 -->
              <el-table-column align="center" prop="questTemplatePropDict" :label="$t('dataConfMod.dictionary')">
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.questTemplatePropDict"
                    :disabled="curOpt === 'view' || scope.row.questTemplatePropType!=='select'"
                  />
                </template>
              </el-table-column>
              <!-- 组件属性 -->
              <el-table-column align="center" prop="questTemplatePropComponent" :label="$t('dataConfMod.componentProperty')">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.questTemplatePropComponent" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 燕豪新增 -->
              <!-- 条件 -->
              <el-table-column align="center" prop="conditionType" :label="$t('bidMod.common.condition')">
                <template slot-scope="scope">
                  <dict-select
                    v-model="scope.row.conditionType"
                    :disabled="curOpt === 'view'"
                    code="QUEST_TEMPLATE_CONDITION_TYPE"
                    @change="value => conditionChange(value,scope)"
                  />
                </template>
              </el-table-column>
              <!-- 条件值 -->
              <el-table-column align="center" prop="conditionValue" :label="$t('productionPrepare.conditionValue')" min-width="150">
                <template slot-scope="scope">
                  <el-date-picker
                    v-if="scope.row.questTemplatePropType === 'date'"
                    v-model="scope.row.conditionValue"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :disabled="curOpt === 'view' || !scope.row.conditionType"
                  />
                  <el-input v-else v-model="scope.row.conditionValue" :disabled="curOpt === 'view' || !scope.row.conditionType" />
                </template>
              </el-table-column>
              <!-- 是否启用 -->
              <el-table-column align="center" prop="enabledFlag" :label="$t('components.bank.isActiveOrNot')">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.enabledFlag" true-label="Y" false-label="N" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 是否必填 -->
              <el-table-column align="center" prop="emptyFlag" :label="$t('dataConfMod.isRequested')">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.emptyFlag" true-label="Y" false-label="N" :disabled="curOpt === 'view'" />
                </template>
              </el-table-column>
              <!-- 是否提醒 -->
              <el-table-column align="center" prop="warningFlag" :label="$t('productionPrepare.warningFlag')">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.warningFlag" true-label="Y" false-label="N" :disabled="curOpt === 'view' || !scope.row.conditionType" />
                </template>
              </el-table-column>
              <!-- 是否预警 -->
              <el-table-column align="center" prop="earlyWarningFlag" :label="$t('productionPrepare.earlyWarningFlag')">
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.earlyWarningFlag" true-label="Y" false-label="N" :disabled="curOpt === 'view' || !scope.row.conditionType" />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                align="center"
                prop="operation"
                :label="$t('components.headers.operation')"
                minWidth="100"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button v-if="curOpt !== 'view'" type="text" @click="addRowField(scope.$index,item.questTemplatePropArr)">
                    {{ $t('common.add') }}
                  </el-button>
                  <el-button v-if="curOpt !== 'view'" type="text" @click="deleteRowField(scope.$index,item.questTemplatePropArr)">
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
          <el-button @click="previewTemp">
            <!-- 预览模板 -->
            {{ $t("dataConfMod.previewTemplate") }}
          </el-button>
          <el-button v-if="curOpt !== 'view'" @click="stagingHandle">
            <!-- 保存 -->
            {{ $t('common.save') }}
          </el-button>
          <el-button v-if="curOpt !== 'view'" type="primary" @click="stagingAndValidHandle">
            <!-- 保存并生效 -->
            {{ $t("productionPrepare.saveAndValid") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <!-- 新增标签 -->
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
          <srm-col :init-col="3">
            <!-- 页签名称 -->
            <el-form-item :label="$t('dataConfMod.tabName')" prop="tabName">
              <el-input v-model="tabBaseInfo.tabName" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <!-- 页签类型 -->
            <el-form-item
              :label="$t('dataConfMod.tabType')"
              prop="tabType"
            >
              <dict-select
                v-model="tabBaseInfo.tabType"
                code="QUEST_TEMPLATE_PROP_GROUP_TYPE"
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
    <!-- 调查表模板 -->
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
          class="form-incontainer the_filter_form"
        >
          <srm-row type="flex">
            <srm-col :init-col="3">
              <!-- 模板名称 -->
              <el-form-item :label="$t('dataConfMod.templateName')" :label-width="formLabelWidth">
                <el-input v-model="filterForm.questTemplateName" />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="3">
              <!-- 模板类型 -->
              <el-form-item :label="$t('contractMod.templType')" prop="questTemplateType">
                <dict-select
                  v-model="filterForm.questTemplateType"
                  code="QUEST_TEMPLATE_TYPE"
                  @change-value="getQuestTemplateType"
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="3">
              <el-button type="primary" @click="queryItemList(1)">
                {{
                  $t('common.search')
                }}
              </el-button>
              <el-button type="primary" @click="reset('form')">
                {{
                  $t('common.reset')
                }}
              </el-button>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <div>
        <div style="padding-top: 10px;">
          <el-table
            :data="displayTemplateItem"
            style="width: 100%"
            border
            height="251px"
            highlight-current-row
            @current-change="handleCurrentChange"
          >
            <!-- <el-table-column type="selection" width="55" fixed="left" /> -->
            <el-table-column label width="60">
              <template slot-scope="scope">
                  <el-radio :label="scope.row" v-model="mulSelection"><span></span>&nbsp;</el-radio>
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              type="index"
              :label="$t('common.sort')"
            />
            <!-- 模板编码 -->
            <el-table-column
              align="center"
              prop="questTemplateCode"
              :label="$t('dataConfMod.templateCode')"
              show-overflow-tooltip
            />
            <!-- 模板名称 -->
            <el-table-column
              align="center"
              prop="questTemplateName"
              :label="$t('dataConfMod.templateName')"
              show-overflow-tooltip
            />
            <!-- 模板类型 -->
            <el-table-column
              align="center"
              :label="$t('contractMod.templType')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                {{ dictClass.getDictLabel('QUEST_TEMPLATE_TYPE',scope.row.questTemplateType) }}
              </template>
            </el-table-column>
            <!-- 备注 -->
            <el-table-column
              align="center"
              prop="questTemplateRemark"
              :label="$t('components.eio.headers.remark')"
              show-overflow-tooltip
            />
          </el-table>
        </div>

        <el-footer class="page-bar">
          <PagerBar
            ref="pager"
            :dataCount="dataCount"
            :queryTotal="queryTotal"
            :pageIndex="viewIndex"
            :pageSize="viewSize"
            :pageCount="pageCount"
            :pageQuery="pageQuery"
          />
        </el-footer>
      </div>

      <template #footer>
        <el-button @click="cancelDialog">
          {{
            $t('common.cancel')
          }}
        </el-button>
        <el-button type="primary" @click="closeDialog">
          {{
            $t('common.confirm')
          }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 模板预览 -->
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
        :questTemplateId="configData.questTemplateId"
        :showEnabledFlagArr="showEnabledFlagArr"
        :disabled="true"
        optType="view"
      />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="previewVisible=false">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
  import CToolbar from 'lib@/components/c-toolbar'
  import { tabTodoMixin } from '@/utils/mixins'
  import http from '@/utils/axios/http'
  import PagerBar from 'lib@/components/Pager'
  import OrganizationSelector from 'lib@/components/organization-selector'
  import renderForm from './renderForm'
  import { createDictClass } from '@/library/utils/dict/dict-utils'
  import { questTemplate } from 'modb@/productionPrepare/api'

  export default {
    name: 'QuestTemplateDetail',
    components: { CToolbar, http, OrganizationSelector, PagerBar, renderForm },
    mixins: [tabTodoMixin],
    data () {
      return {
        dictClass: createDictClass({
          QUEST_TEMPLATE_TYPE: []
        }),
        showEnabledFlagArr: [],
        configDataRules: {
          questTemplateName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
          questTemplateType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
          organizationIds: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
        },
        mulSelection: null,
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
        tabBaseInfo: {
          tabName: '', // 页签名称
          tabCode: '', // 页签编码
          tabType: '' // 页签类型 默认值为表格
        },
        tabBaseInfoRules: {
          tabName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
          // tabCode: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }],
          tabType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }]
        },
        tempList: [
          { value: 'ds', label: this.$t('dataConfMod.production') },  // '生产'
          { value: 'ddd', label: this.$t('dataConfMod.nonProduction') }  //'非生产'
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
            // {
            //   questTemplatePropGroupName: '基础信息',//页签名称
            //   questTemplatePropGroupCode: 'baseInfo',//页签编码
            //   questTemplatePropGroupType: 'form',//页签类型
            //   deleteFlag: true, // 页签可删除属性
            //   showFlag: 'Y',//是否显示该页签
            //   fillOneLineFlag: 'N',//是否必填一行(类型为明细表类型的显示)
            //   questTemplatePropArr: [ // 字段配置信息
            //     {
            //       questTemplatePropSort: '',//排序号
            //       questTemplatePropField: '',//字段编码
            //       questTemplatePropFieldDesc: '',//字段描述
            //       questTemplatePropType: '',//组件类型
            //       questTemplatePropDict: '',//字典
            //       questTemplatePropComponent: '',//组件属性
            //       conditionType:'', //条件类型
            //       conditionValue:'', //条件值
            //       warningFlag:'N',  //是否提醒
            //       earlyWarningFlag:'N', //是否预警
            //       enabledFlag: 'Y',//是否启动
            //       emptyFlag: 'N'//是否必填
            //     }
            //   ]
            // }
          ]
        },
        curRole: this.$store.getters.userType, // 用户类型 BUYER || VENDOR
        curOpt: 'view'
      }
    },
    // 页面创建
    created () {
      this.curOpt = this.$attrs.params.flag
      if (this.$attrs.params.flag === 'edit' ||
        this.$attrs.params.flag === 'view') {
        // this.curOrderId = this.$attrs.params.row.reviewFormId
        let questTemplateId = this.$attrs.params.row.questTemplateId
        this.configData.questTemplateId = questTemplateId
        this.getDetail(questTemplateId)
      } else if (this.$attrs.params.flag === 'add') {
        if (this.$attrs.params.row) {
          console.log(this.$attrs.params.row)
          let questTemplateId = this.$attrs.params.row.questTemplateId
          this.getDetail(questTemplateId, 'selectDialog')
        }
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
      changePropType (value, scope) {
        scope.row.conditionValue = ''
      },
      conditionChange (value, scope) {
        if (!value) {
          scope.row.conditionValue = ''
          scope.row.warningFlag = ''
          scope.row.earlyWarningFlag = ''
        }
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
        if (!this.mulSelection ) {
          this.$message({
            type: 'error',
            message: this.$t('common.pleaseSelectOne') // 请选择一条数据！
          })
          return
        }
        this.dialogTemplateVisible = false
        let questTemplateId = this.mulSelection.questTemplateId
        this.configData.questTemplateType = this.mulSelection.questTemplateType
        // this.configData.quoteQuestTemplateId = questTemplateId
        this.configData.quoteQuestTemplateName = this.mulSelection.questTemplateName
        this.getDetail(questTemplateId, 'selectDialog')
      },
      cancelDialog () {
        this.dialogTemplateVisible = false
      },
      queryItemList (num) {
        this.filterForm.questTemplateType = this.configData.questTemplateType
        const data = { ...this.queryParam, ...this.filterForm }
        if (num === 1) {
          data.pageNum = 1
          this.viewIndex = 1
          this.queryParam.pageNum = 1
        }
        questTemplate.listPageByParm(data).then((res) => {
          this.dialogTemplateVisible = true
          this.displayTemplateItem = res.data.list
          this.queryTotal = res.data.total
          this.dataCount = res.data.list.length
        })
      },
      handleCurrentChange (selection) {
        if(selection){
          this.mulSelection = selection
        }
      },
      // 调查模板类型切换
      getQuestTemplateType (val, dictItem) {
        if (val) {
          this.configData.questTemplateTypeName = dictItem.label
        }
      },
      selectHandler (node, value, scope) {
        this.configData.questTemplateOrgArr = []
        if (node) {
          node.forEach(elm => {
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
        this.tabBaseInfo.tabName = ''// 页签名称
        this.tabBaseInfo.tabCode = ''// 页签编码
        this.tabBaseInfo.tabType = 'table'// 页签类型
        this.dialogFormVisible = true
      },
      confirmAdd () {
        this.$refs.ratingTabForm.validate(valid => {
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
            this.$message.error('请维护标签信息!')
          }
        })
      },
      // 删除标签
      tabRemove (tabName) {
        if (tabName) {
          let delIndex = this.configData.questTemplateTabArr.findIndex(i => i.questTemplatePropGroupCode == tabName)
          if (delIndex > -1) {
            this.configData.questTemplateTabArr.splice(delIndex, 1)
          }
          let arrayLength = this.configData.questTemplateTabArr.length - 1
          if (arrayLength !== -1) {
            this.activeTab = this.configData.questTemplateTabArr[arrayLength].questTemplatePropGroupCode
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
          conditionType: '', // 条件
          conditionValue: '', // 条件值
          enabledFlag: 'Y', // 是否启用
          emptyFlag: 'N', // 是否必填
          warningFlag: 'N', // 是否提醒
          earlyWarningFlag: 'N' // 是否预警
        })
      },
      // 通过id查询模板数据
      getDetail (questTemplateId, type) {
        questTemplate.questTemplateData({ questTemplateId }).then(res => {
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
                templateOrgArr.forEach(elm => {
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
        }).catch(err => {
          console.log(err)
        })
      },
      templateStatus (questTemplateId, questTemplateStatus) {
        // this.$set(this.obj, 'b', 'obj.b')
        let updateData = {
          questTemplateId,
          questTemplateStatus
        }
        return new Promise(resolve => {
          questTemplate.modify(updateData).then(res => {
            resolve()
          }).catch(err => {
            console.log(err)
          })
        })
      },
      stagingAndValidHandle () {
        this.$refs.indicatorsForm.validate(valid => {
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
                      // '页签下【排序】不能为空'
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.tabNoEmpty'))
                      return false
                    }
                    let questTemplatePropField = elm.questTemplatePropField
                    if (!questTemplatePropField) {
                      // '页签下【字段编码】不能为空'
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.codeNoEmpty'))
                      return false
                    }
                    let questTemplatePropFieldDesc = elm.questTemplatePropFieldDesc
                    let questTemplatePropType = elm.questTemplatePropType // 字段类型
                    if (!questTemplatePropType) {
                      // '页签下【字段类型】不能为空'
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.typeNoEmpty'))
                      return false
                    }
                    if (questTemplatePropType === 'select') {
                      if (!elm.questTemplatePropDict) {
                        // '下拉字段字典值为必填,请填写“'
                        this.$message.error(this.$t('dataConfMod.dropdownRequired') + questTemplatePropGroupName + this.$t('dataConfMod.tabDown') + questTemplatePropFieldDesc + this.$t('dataConfMod.dictInfo'))
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
              // '页签下至少要有一条字段配置'
              this.$message.error(this.$t('dataConfMod.leastOneConfig'))
              return false
            }
            if (vlCount > 0) {
              return false
            }
            questTemplate.saveQuestTemplateData(this.configData).then(res => {
              this.templateStatus(res.data, 'Y').then(() => {
                this.$message.success(res.message)
                this.$emit('tab-remove', this.$attrs.params.tabName)
                this.__setTabTodo('QuestTemplateList.getQueryData')
              })
            }).catch(err => {
              console.log(err)
            })
          } else {
            // '必填项不能为空!'
            this.$message.error(this.$t('dataConfMod.notEmpty'))
          }
        })
      },
      stagingHandle () {
        this.$refs.indicatorsForm.validate(valid => {
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
                      // this.$message.error(questTemplatePropGroupName + '页签下【排序】不能为空')
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.tabNoEmpty'))
                      return false
                    }
                    let questTemplatePropField = elm.questTemplatePropField
                    if (!questTemplatePropField) {
                      // this.$message.error(questTemplatePropGroupName + '页签下【字段编码】不能为空')
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.codeNoEmpty'))
                      return false
                    }
                    let questTemplatePropFieldDesc = elm.questTemplatePropFieldDesc
                    let questTemplatePropType = elm.questTemplatePropType // 字段类型
                    if (!questTemplatePropType) {
                      // this.$message.error(questTemplatePropGroupName + '页签下【字段类型】不能为空')
                      this.$message.error(questTemplatePropGroupName + this.$t('dataConfMod.typeNoEmpty'))
                      return false
                    }
                    if (questTemplatePropType === 'select') {
                      if (!elm.questTemplatePropDict) {
                        // this.$message.error('下拉字段字典值为必填,请填写“' + questTemplatePropGroupName + '”页签下面“' + questTemplatePropFieldDesc + '”的字典信息')
                        this.$message.error(this.$t('dataConfMod.dropdownRequired') + questTemplatePropGroupName + this.$t('dataConfMod.tabDown') + questTemplatePropFieldDesc + this.$t('dataConfMod.dictInfo'))
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
              this.$message.error('页签下至少要有一条字段配置')
              return false
            }
            if (vlCount > 0) {
              return false
            }
            questTemplate.saveQuestTemplateData(this.configData).then(res => {
              this.$message.success(res.message)
              if (res.code === '0') {
                this.getDetail(res.data)
              }
            }).catch(err => {
              console.log(err)
            })
          } else {
            this.$message.error('必填项不能为空!')
          }
        })
      },
      addRowField (index, propArr) {
        propArr.splice(index + 1, 0, {
          questTemplatePropSort: '', // 排序号
          questTemplatePropField: '', // 字段编码
          questTemplatePropFieldDesc: '', // 字段描述
          questTemplatePropType: '', // 组件类型
          questTemplatePropDict: '', // 字典
          questTemplatePropComponent: '', // 组件属性
          enabledFlag: 'Y', // 是否启动
          emptyFlag: 'N'// 是否必填
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
            message: this.$t('dataConfMod.saveAndPreview')  // '请先保存模板后预览！'
          }) // 提交成功
        }
      },
      showData () {
         console.log(this.$refs.renderFormDom)
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
    .el-switch, .el-button {
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
