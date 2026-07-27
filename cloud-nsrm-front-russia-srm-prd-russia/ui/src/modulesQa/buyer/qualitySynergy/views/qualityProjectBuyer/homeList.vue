<template>
  <el-container class="flex-container the_quick_list__outter_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="preArr" @getFormData="getQuerydata" />
      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="addSpc"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            :disabled="!selectShow"
            @click="deleteOne"
          >
            {{ $t('common.delete') }}
          </el-button>
          <!-- <el-button
            type="primary"
            size="mini"
            :disabled="selectList.length!==1"
            @click="editRow"
          >
            {{ $t('common.edit') }}
          </el-button>
          <el-button
            v-if="selectList.length==1"
            @click="cancelEdit"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-if="selectList.length==1"
            @click="saveRow"
          >
            {{ $t('common.save') }}
          </el-button> -->
          <ExportExcel
            page-url="/api-qc/api-ql/spcMonitorListBuyer/query"
            :filter-params="computedQueryParam"
            :table-header="computeTableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :checkbox="true"
        :current-change="handleCurrentChange"
        :check-change="selectionChange"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/spcMonitorListBuyer/query"
      />
      <!--弹框区域-->
      <standardDialog ref="standardDialog" :visible.sync="standardDialogVisible" />
      <srm-dialog
        :visible.sync="addDialogVisible"
        :destroyOnClose="true"
        :close-on-click-modal="false"
        class="c-form-dialog"
        width="70%"
        :title="$t('quality.project.addSpcPro')"
      >
        <el-form
          ref="addMonitorsForm"
          :model="addDialogForm"
          :rules="addDialogRules"
        >
          <srm-row :gutter="24">
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.customerOrgName')"
                prop="customerOrgId"
              >
                <OrganizationSelector
                  ref="orgSelector"
                  v-model="addDialogForm.customerOrgId"
                  :placeholder="$t('common.pleaseSelect')"
                  :parent-id="-1"
                  node-type="OU"
                  @select="invSelectHandler"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.monitorListNo')"
                prop="monitorListNo"
              >
                <el-input v-model="addDialogForm.monitorListNo" disabled />
              </el-form-item>
            </srm-col>

            <srm-col :initCol="3">
              <el-form-item
                :label="$t('common.vendorCode')"
                prop="vendorCode"
              >
                <QuickSearch
                  :show-input="addDialogForm.vendorCode"
                  show-key="companyCode"
                  :scope-data="addDialogForm"
                  auto-query
                  name="scc_sup_company_info_display"
                  :pre-query-data="{'t.STATUS': 'APPROVED'}"
                  @close-quicksearch="getCompany"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('common.vendorName')"
                prop="vendorName"
              >
                <el-input v-model="addDialogForm.vendorName" disabled />
              </el-form-item>
            </srm-col>

            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.project.vendorQuantityLinkman')"
                prop="vendorQuantityLinkman"
              >
                <QuickSearch
                  :show-input="addDialogForm.vendorQuantityLinkman"
                  show-key="contactName"
                  :scope-data="addDialogForm"
                  auto-query
                  name="scc_sup_contact_info"
                  @close-quicksearch="getContact"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.project.vendorPhone')"
                prop="vendorPhone"
              >
                <el-input v-model="addDialogForm.vendorPhone" disabled />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.project.vendorEmail')"
                prop="vendorEmail"
              >
                <el-input v-model="addDialogForm.vendorEmail" disabled />
              </el-form-item>
            </srm-col>

            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.lineCode')"
                prop="lineCode"
              >
                <el-input v-model="addDialogForm.lineCode" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.workCenter')"
                prop="workCenter"
              >
                <el-input v-model="addDialogForm.workCenter" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.monitoringFeature')"
                prop="monitoringFeature"
              >
                <el-input v-model="addDialogForm.monitoringFeature" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.characterUnit')"
                prop="characterUnit"
              >
                <el-input v-model="addDialogForm.characterUnit" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.onLineEquipment')"
                prop="onLineEquipment"
              >
                <el-input v-model="addDialogForm.onLineEquipment" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('qualitySynergy.unit')"
                prop="spUnit"
              >
                <el-input v-model="addDialogForm.spUnit" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="addDialogVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="handleAddDialogSaveClick">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
      <srm-dialog
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        class="c-form-dialog"
        width="70%"
        :v-cloak="$t('quality.project.jiankongweihu')"
      >
        <el-form
          ref="monitorsForm"
          :model="dialogModle.dialogForm"
          :rules="dialogModle.rules"
        >
          <srm-row :gutter="24">
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.orgBu')"
                required
              >
                <el-select v-model="dialogModle.dialogForm.customerOrgName" disabled>
                  <el-option
                    v-for="subitem in dialogModle.dialogForm.customerOrgNameList"
                    :key="subitem.value + '.' + Math.random()"
                    :value="subitem.value"
                    :label="subitem.label"
                  />
                </el-select>
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.lineCode')"
                prop="lineCode"
                required
              >
                <el-input v-model="dialogModle.dialogForm.lineCode" clearable />
                <!--<el-select v-model="dialogModle.dialogForm.lineCode" clearable>
                  <el-option
                    v-for="subitem in dialogModle.dialogForm.lineCodeList"
                    :key="subitem.value + '.' + Math.random()"
                    :value="subitem.value"
                    :label="subitem.label"
                  />
                </el-select>-->
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.project.workCenter')"
                prop="workCenter"
                required
              >
                <el-input v-model="dialogModle.dialogForm.workCenter" clearable />
                <!--<el-select v-model="dialogModle.dialogForm.workCenter" clearable>
                  <el-option
                    v-for="subitem in dialogModle.dialogForm.workCenterList"
                    :key="subitem.value + '.' + Math.random()"
                    :value="subitem.value"
                    :label="subitem.label"
                  />
                </el-select>-->
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.onLineEquipment')"
                prop="onLineEquipment"
                required
              >
                <el-input v-model="dialogModle.dialogForm.onLineEquipment" clearable />
                <!--<el-select v-model="dialogModle.dialogForm.onLineEquipment" clearable>
                  <el-option
                    v-for="subitem in dialogModle.dialogForm.onLineEquipmentList"
                    :key="subitem.value + '.' + Math.random()"
                    :value="subitem.value"
                    :label="subitem.label"
                  />
                </el-select>-->
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.monitoringFeature')"
                prop="monitoringFeature"
                required
              >
                <el-input v-model="dialogModle.dialogForm.monitoringFeature" clearable />
                <!--<el-select v-model="dialogModle.dialogForm.monitoringFeature" clearable>
                  <el-option
                    v-for="subitem in dialogModle.dialogForm.monitoringFeatureList"
                    :key="subitem.value + '.' + Math.random()"
                    :value="subitem.value"
                    :label="subitem.label"
                  />
                </el-select>-->
              </el-form-item>
            </srm-col>
            <!--<srm-col :initCol="3">
              <el-form-item
                label="单位"
                prop="companyName"
                required
              >
                <el-input v-model="dialogModle.dialogForm.monitoringFeature" />
              </el-form-item>
            </srm-col>-->
            <srm-col :initCol="3">
              <el-form-item
                :label="$t('quality.project.cpksize')"
                prop="subgroupSize"
                required
              >
                <el-input v-model="dialogModle.dialogForm.subgroupSize" clearable />
              </el-form-item>
            </srm-col>
            <!--<srm-col :initCol="3">
              <el-form-item
                label="数据来源"
                prop="companyName"
                required
              >
                <el-input v-model="dialogModle.dialogForm.searchWithoutModelFlag" />
              </el-form-item>
            </srm-col>-->
            <srm-col :span="24">
              <el-form-item :label="$t('quality.project.cpkjiankong')">
                <el-checkbox-group v-model="checkList" @change="checkBoxChange">
                  <el-checkbox
                    v-for="pub in tradeType"
                    :key="pub.id"
                    :label="pub.dictItemCode"
                    name="type"
                  >
                    {{ pub.dictItemName }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="handleDialogSaveClick">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import CommonRules from './commonRules'
import { qualityProject, spcStandard } from '@/modulesQa/buyer/qualitySynergy/api'
import { parseTime, deepClone } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from '@/library/utils/util'
import { mapGetters } from 'vuex'
import ExportExcel from 'lib@/components/export-excel'
import standardDialog from './standardDialog'

const { pageCondition, listMyMonitors, addMonitors, monitorModify, deleteMonitors, stopMonitors } = qualityProject
const { spcStandardAdd } = spcStandard

export default {
  name: 'QualityProjectBuyerList',
  components: {
    TableView, MainHeader, FormWrapper, ExportExcel, OrganizationSelector, QuickSearch, standardDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      gridId: 'list',
      selectList: [],
      selectDel: [],
      editTabTxt: '',
      dictCodes: {
        status: 'SPC_PROJECT_STATE'
      },
      // SPC标准维护  start
      standardDialogVisible: false,
      // SPC标准维护  end
      addDialogVisible: false,
      addDialogForm: {
        monitorListNo: null,
        customerOrgId: null,
        vendorCode: null,
        vendorName: null,
        vendorQuantityLinkman: null,
        vendorPhone: null,
        vendorEmail: null,
        lineCode: null,
        workCenter: null,
        monitoringFeature: null,
        characterUnit: null,
        onLineEquipment: null,
        spUnit: null,
        state: 'CEASE'
      },
      addDialogRules: {
        customerOrgId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        vendorCode: [{ required: true, message: this.$t('common.pleaseSelect') }],
        vendorQuantityLinkman: [{ required: true, message: this.$t('common.pleaseSelect') }],
        lineCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        workCenter: [{ required: true, message: this.$t('common.pleaseInput') }],
        monitoringFeature: [{ required: true, message: this.$t('common.pleaseInput') }],
        characterUnit: [{ required: true, message: this.$t('common.pleaseInput') }],
        onLineEquipment: [{ required: true, message: this.$t('common.pleaseInput') }],
        spUnit: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      tableHeader: [
        {
          prop: 'state',
          label: () => this.$t('common.status'),
          dataType: 'dict',
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'dictSelect',
          code: 'SPC_PROJECT_STATE',
          formattor: val => this.$getDictLabel('SPC_PROJECT_STATE', val)
        },
        { prop: 'monitorListNo',
          label: this.$t('quality.monitorListNo'),
          minWidth: 160
        },
        { prop: 'customerOrgName',
          label: this.$t('quality.customerOrgName'),
          minWidth: 120
        },
        { prop: 'lineCode',
          label: this.$t('quality.lineCode')
        },
        { prop: 'workCenter',
          label: this.$t('quality.workCenter'),
          minWidth: 120
        },
        { prop: 'onLineEquipment',
          label: this.$t('quality.onLineEquipment')
        },
        { prop: 'monitoringFeature',
          label: this.$t('quality.monitoringFeature')
        },
        { prop: 'characterUnit',
          label: this.$t('quality.characterUnit')
        },
        // 特性单位
        { prop: 'createdFullName',
          label: this.$t('common.creator'),
          minWidth: 90
        },
        { prop: 'spUnit',
          label: this.$t('quality.spUnit'),
          minWidth: 80
        },
        { prop: 'creationDate',
          label: this.$t('common.creationTime'),
          minWidth: 130,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : '--'
          }
        },
        // { prop: 'lastUpdatedFullName',
        //   label: this.$t('common.lastUpdatedFullName'),
        //   minWidth: 90
        // },
        // { prop: 'lastUpdateDate',
        //   label: this.$t('common.lastUpdateDate'),
        //   minWidth: 130,
        //   formattor (val) {
        //     return val ? parseTime(val, '{y}-{m}-{d}') : '--'
        //   }
        // },
        { prop: 'existStandard',
          label: this.$t('quality.spcStandard'),
          minWidth: 100,
          showType: 'button',
          btnStyle: 'text',
          fixed: 'right',
          callback: function (row) {
            this.editStandard(row)
          }.bind(this),
          formattor: (val) => {
            return val === 'Y' ? this.$t('quality.project.yiweihu') : this.$t('quality.project.weiweihu')
          }
        },
        { prop: 'existRule',
          label: this.$t('quality.existRule'),
          minWidth: 100,
          showType: 'button',
          btnStyle: 'text',
          fixed: 'right',
          // disabled: function (row) {
          //   return row.existRule === 'N'
          // },
          callback: function (row) {
            var param = {
              'customerOrgId': row.customerOrgId,
              'workCenter': row.workCenter,
              'onLineEquipment': row.onLineEquipment,
              'monitoringFeature': row.monitoringFeature,
              'monitorListNo': row.monitorListNo,
              'existRule': row.existRule
            }
            this.editTab('common', param)
          }.bind(this),
          formattor: (val) => {
            return val === 'Y' ? this.$t('quality.project.yiweihu') : this.$t('quality.project.weiweihu')
          }
        }
        // {
        //   prop: 'operation',
        //   label: '操作',
        //   showType: 'buttons',
        //   btnStyle: 'text',
        //   fixed: 'right',
        //   width: 100,
        //   buttons: [
        //     {
        //       callback: (row) => this.stopHandle(row),
        //       // code: "pr:requirementApply:edit",
        //       show: row => row.state === 'ACTIVATION',
        //       formattor: () => {
        //         return this.$t('quality.stop')
        //       }
        //     }
        //   ]
        // }
      ],
      tableData: [],
      dialogVisible: false,
      dialogModle: {
        dialogForm: { // 基础信息
          customerOrgName: '',
          'customerCompanyId': 1,
          'customerOrgId': 585,
          'erpSupplierId': 'A001688',
          'lineCode': '',
          'workCenter': '',
          'onLineEquipment': '',
          'monitoringFeature': '',
          'monitoringOnline': '',
          'monitoringOffline': '',
          'monitoringCpk': 'Y',
          'subgroupSize': null,
          'spcThresholdId': 2253848521,
          'spcDatasourceId': 1,
          'searchWithoutModelFlag': 'Y',
          'cpkCalculatedDatetime': 1582525522000,
          'testRequest': ''
        },
        rules: {
          lineCode: [{ required: true, message: this.$t('common.pleaseInput') }],
          workCenter: [{ required: true, message: this.$t('common.pleaseInput')}],
          onLineEquipment: [{ required: true, message: this.$t('common.pleaseInput') }],
          monitoringFeature: [{ required: true, message: this.$t('common.pleaseInput') }],
          subgroupSize: [{ required: true, message: this.$t('common.pleaseInput')}]
        },
        customerOrgNameList: [],
        lineCodeList: [],
        workCenterList: [],
        onLineEquipmentList: [],
        monitoringFeatureList: []
      },
      tradeType: [
        {
          id: 1,
          dictItemCode: '是否CPK预警',
          dictItemName: '是否CPK预警'
        },
        {
          id: 2,
          dictItemCode: '与物料无关',
          dictItemName: '与物料无关'
        }
      ],
      checkList: ['是否CPK预警', '与物料无关'],
      queryParam: {
        'customerCompanyId': '',
        'customerOrgId': '',
        'onLineEquipment': '',
        'lineCode': '',
        'erpSupplierId': '',
        'workCenter': '',
        'monitoringFeature': ''
      },
      preArr: [
        { prop: 'monitorListNo',
          label: this.$t('quality.monitorListNo')
        },
        { prop: 'customerOrgId',
          label: this.$t('quality.customerOrgName'),
          type: 'OUorganizationSelector'
        },
        { prop: 'lineCode',
          label: this.$t('quality.lineCode'),
          type: 'select',
          options: []
        },
        { prop: 'workCenter',
          label: this.$t('quality.workCenter'),
          type: 'select',
          options: []
        },
        { prop: 'onLineEquipment',
          label: this.$t('quality.onLineEquipment'),
          type: 'select',
          options: []
        },
        { prop: 'monitoringFeature',
          label: this.$t('quality.monitoringFeature'),
          type: 'select',
          options: []
        },
        {
          prop: 'state',
          label: this.$t('common.status'),
          width: 180,
          type: 'dict',
          code: 'SPC_PROJECT_STATE'
        },
        {
          prop: 'createdBy',
          label: this.$t('common.creator'),
          type: 'quicksearch',
          showKey: 'username',
          name: 'scc_rbac_user_display'
        }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    selectShow () {
      return this.selectList.length > 0
    },
    standardSelectShow () {
      return this.standardSelectList.length > 0
    },
    computeTableHeader () {
      return this.tableHeader.filter(item => !['name', 'existRule'].includes(item.prop))
    },
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },
  created () {
    this.getPageCondition()
    this.getQuerydata(this.queryParam)
  },
  methods: {

    saveRow () {
      let formData = transformMQL.save('spcMonitorListBuyer', [this.selectList[0]], 'save')
      monitorModify(formData).then(response => {
        const { data } = response
        if (response) {
          this.$message.success(this.$t('common.success'))
          this.$refs[this.gridId].query()
        }
      })
    },
    cancelEdit () {
      this.$set(this.selectList[0], 'editable', false)
    },
    editRow () {
      this.$set(this.selectList[0], 'editable', true)
    },
    getContact (val, scope) {
      scope.vendorQuantityLinkman = val ? val.contactName : null
      scope.vendorPhone = val ? val.ceeaContactMethod : ''
      scope.vendorEmail = val ? val.email : ''
    },
    getCompany (val, scope) {
      scope.vendorId = val ? val.companyId : null
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    invSelectHandler (node, value, scope) {
      this.addDialogForm.customerOrgId = node ? node.organizationId : null
      this.addDialogForm.customerOrgCode = node ? node.organizationCode : null
      this.addDialogForm.customerOrgName = node ? node.organizationName : null
    },
    handleAddDialogSaveClick () {
      this.$refs.addMonitorsForm.validate((valid) => {
        if (valid) {
          let formData = transformMQL.save('spcMonitorListBuyer', [this.addDialogForm], 'save')
          addMonitors(formData).then(response => {
            const { data } = response
            if (response) {
              this.$message.success(this.$t('common.successUpdate'))
              this.addDialogVisible = false
              this.$refs[this.gridId].query()
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 初始化条件查询
    getPageCondition () {
      let transformParams = transformMQL.save('spcMonitorListBuyer', {}, 'pageCondition')
      pageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        let lineCodeList = []; let workCenterList = []; let onLineEquipmentList = []; let monitoringFeatureList = []
        // 产线下拉
        Object.keys(data.lineCodeMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.lineCodeMap[item]
          lineCodeList.push(newObj)
        })
        // 工作中心下拉
        Object.keys(data.workCenterMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workCenterMap[item]
          workCenterList.push(newObj)
        })
        // 联机设备下拉
        Object.keys(data.onLineEquipmentMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.onLineEquipmentMap[item]
          onLineEquipmentList.push(newObj)
        })
        // 监控特性下拉
        Object.keys(data.monitoringFeatureMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.monitoringFeatureMap[item]
          monitoringFeatureList.push(newObj)
        })
        for (let item of this.preArr) {
          switch (item.prop) {
          case 'lineCode':
            item.options = lineCodeList
            break
          case 'workCenter':
            item.options = workCenterList
            break
          case 'onLineEquipment':
            item.options = onLineEquipmentList
            break
          case 'monitoringFeature':
            item.options = monitoringFeatureList
            break
          }
        }
      })
    },
    checkBoxChange (val) {
      if (val.indexOf('是否CPK预警') > -1) {
        this.dialogModle.dialogForm.monitoringCpk = 'Y'
      } else {
        this.dialogModle.dialogForm.monitoringCpk = 'N'
      }
      if (val.indexOf('与物料无关') > -1) {
        this.dialogModle.dialogForm.searchWithoutModelFlag = 'Y'
      } else {
        this.dialogModle.dialogForm.searchWithoutModelFlag = 'N'
      }
    },
    // 新增
    addNew () {
      this.dialogVisible = true
      this.editTabTxt = 'add'
      this.$nextTick(() => {
        this.$refs.monitorsForm.resetFields()
      })
    },
    // 新增spc项目
    addSpc () {
      this.addDialogVisible = true

      this.$nextTick(() => {
        this.addDialogForm = {
          monitorListNo: null,
          customerOrgId: null,
          vendorCode: null,
          vendorName: null,
          vendorQuantityLinkman: null,
          vendorPhone: null,
          vendorEmail: null,
          lineCode: null,
          workCenter: null,
          monitoringFeature: null,
          characterUnit: null,
          onLineEquipment: null,
          state: 'CEASE',
          spUnit: null
        }
      })
    },
    // 特性维护弹窗保存按钮
    handleDialogSaveClick () {
      this.$refs.monitorsForm.validate((valid) => {
        if (valid) {
          if (this.editTabTxt === 'add') { // 新增
            addMonitors(this.dialogModle.dialogForm).then(response => {
              const { data } = response
              if (response.success) {
                this.dialogVisible = false
                this.$refs[this.gridId].query()
              }
            })
          }
          if (this.editTabTxt === 'change') { // 修改
            monitorModify(this.dialogModle.dialogForm).then(response => {
              const { data } = response
              if (response.success) {
                this.dialogVisible = false
                this.$refs[this.gridId].query()
              }
            })
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    // 导出
    leadingOut () {
      let params = ''
      Object.keys(this.queryParam).forEach(item => {
        if (this.queryParam[item]) params += `${item}=${encodeURIComponent(this.queryParam[item])}&`
      })
      let href = `/quality/monitor/exportMonitor?${params}`
      var a = document.createElement('a')
      a.href = href
      a.download()
      a.click()
    },
    // 查询列表
    getQuerydata (params = {}) {
      console.log('params:::', params)
      this.queryParam = transformMQL.listGetData('spcMonitorListBuyer', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    refresh () {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editStandard (row) {
      this.standardDialogVisible = true
      this.$refs.standardDialog.init(deepClone(row))
    },
    // 编辑tab
    editTab (type, param) {
      console.log('!!!!!!!!!  param  :', param)
      let tab = {}
      if (type == 'SPCStandard') {
        // SPC标准维护
        this.$router.push({ name: 'spcStandard' })
      } else if (type == 'common') {
        // 判异规则维护
        tab = {
          component: CommonRules,
          params: { flag: 'common', param: param, tabName: param.monitorListNo },
          title: () => this.$t('quality.project.monitorListEdit'),
          name: param.monitorListNo

        }
        this.$emit('tab-add', tab)
      } else if (type == 'change') {
        this.dialogModle.dialogForm = JSON.parse(JSON.stringify(this.selectList[0]))
        this.dialogVisible = true
        this.editTabTxt = 'change'
      }
    },
    // 删除数据
    deleteOne () {
      this.$confirm(this.$t('common.confirmDeleteRow'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let formData = transformMQL.save('spcMonitorListBuyer', this.selectDel, 'delete')
        deleteMonitors(formData).then(response => {
          const { data } = response
          if (response) {
            this.$message.success(this.$t('common.successUpdate'))
            this.$refs[this.gridId].query()
          }
        })
      })
    },
    stopHandle (row) {
      this.$confirm(this.$t('quality.project.stopHandle'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        let formData = transformMQL.save('spcMonitorListBuyer', [row.id], 'inValidMonitorList')
        stopMonitors(formData).then(response => {
          const { data } = response
          if (response) {
            this.$message.success(this.$t('common.success'))
            this.$refs[this.gridId].query()
          }
        })
      })
    },
    handleCurrentChange (val) {
    },
    selectionChange (val) {
      this.selectList = val
      this.selectDel = []
      this.selectList.map(item => {
        this.selectDel.push(item.id)
      })
    },
    // 双击行
    rowDblclick (row, event, column) {
      this.editTab('edit')
    }
  }
}
</script>
<style scoped lang="scss">
</style>
