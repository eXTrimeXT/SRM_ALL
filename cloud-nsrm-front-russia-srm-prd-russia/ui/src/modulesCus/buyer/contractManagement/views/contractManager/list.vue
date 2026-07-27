<template>
  <!-- 定级规则 -->
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template v-if="curRole === 'BUYER'" slot="left">
          <AuthorityButton
            code="cm:contractManager:add"
            type="primary"
            @click="addOne('MIAN_CONTRACT_ADD')"
          >
            {{ $t('contractMod.addContract') }}
          </AuthorityButton>
          <AuthorityButton
            code="cm:contractManager:alter"
            @click="addOne('MIAN_CONTRACT_ALTER')"
          >
            {{ $t('contractMod.contractChange') }}
          </AuthorityButton>
          <el-button
            @click="addOne('SUPPLEMENTAL_AGREEMENT')"
          >
            {{ $t('contractMod.contractChange2') }}
          </el-button>
          <MImport
            ref="import"
            type="default"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!--导出-->
          <ExportExcel
            type="default"
            page-url="/api-cm/contract/contractHead/listPageByParam"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
          />

          <el-button
            @click="openDialog"
          >
            {{
              $t('bidMod.bulkMaintainFwAgreement')
            }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cm/contract/contractHead/listPageByParam"
      />
    </el-main>
    <srm-dialog
      :title="$t('bidMod.bulkMaintainFwAgreement')"
      size="middle"
      :visible.sync="sumFormVisible2"
      :append-to-body="true"
    >
      <el-form
        ref="sumForm2"
        :model="sumForm2"
        label-width="80px"
      >
        <srm-row>
          <srm-col :initCol="2">
            <el-form-item :label="$t('common.vendor')">
              <el-input
                v-model="sumForm2.vendorName"
                disabled
              />
            </el-form-item>
          </srm-col>
          <srm-col
            :initCol="3"
            style="padding-left: 22px"
          >
            <el-form-item :label="$t('bidMod.isFrameworkAgreement')">
              <el-checkbox
                v-model="sumForm2.isFrameworkAgreement"
                true-label="Y"
                false-label="N"
                disabled
              />
            </el-form-item>
          </srm-col>
          <srm-col
            :initCol="6"
            style="text-align: right"
          >
            <el-button
              type="primary"
              @click="queryContractData2"
            >
              {{
                $t('common.search')
              }}
            </el-button>
          </srm-col>
        </srm-row>
      </el-form>
      <el-table
        ref="catSelector2"
        style="width: 100%"
        height="311px"
        border
        highlight-current-row
        :data="contractDataList2"
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <el-table-column
          prop="contractCode"
          min-width="200"
          align="center"
          :label="$t('contractMod.contractCode')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          prop="contractName"
          min-width="200"
          align="center"
          :label="$t('contractMod.contractName')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          :label="$t('common.operation')"
          width="60"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="saveContractCode(scope.$index, scope.row)"
            >
              {{
                $t('common.save')
              }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </srm-dialog>
    <srm-dialog
      :title="$t('contractMod.initiatePaymentRequest')"
      size="large"
      :visible.sync="launchPaymentRequestVisible"
    >
      <pay-plan
        :visible="true"
        :show-plus="false"
        :pay-plans="payPlanList"
        :show-launch-payment-request="true"
        @launchPaymentRequest="launchPaymentRequestHandle"
      />
    </srm-dialog>
    <srm-dialog
      :title="$t('contractMod.contractFiling')"
      size="large"
      :visible.sync="contractFilingVisible"
    >
      <el-form
        :inline="true"
        :model="contractCloseForm"
      >
        <!-- 双方盖章协议附件上传 -->
        <el-form-item :label="$t('contractMod.bothPartiesUpload')">
          <div>
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: contractCloseForm.fileuploadId,
                fileName: contractCloseForm.fileSourceName
              }"
              :readonly="false"
              @on-change="({file}) => handleUploadSuccess(file)"
            />
          </div>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="contractFilingVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button
          type="primary"
          @click="paperArchiveConfirm"
        >{{ $t('common.confirm') }}</el-button>
      </span>
    </srm-dialog>
    <srm-dialog
      :title="$t('contractMod.contractClosure')"
      :visible.sync="closeDialogVisible"
      size="small"
    >
      <el-form
        :inline="true"
        :model="contractCloseForm"
      >
        <!-- 解除协议附件上传 -->
        <el-form-item :label="$t('contractMod.cancelAgreementUpload')">
          <div>
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: contractCloseForm.fileuploadId,
                fileName: contractCloseForm.fileSourceName
              }"
              :readonly="false"
              @on-change="({file}) => handleUploadSuccess(file)"
            />
          </div>
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="closeDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button
          type="primary"
          @click="closeConfirm"
        >{{ $t('common.confirm') }}</el-button>
      </span>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import payPlan from './pay-plan'
import contractInformation from './edit'
import termination from './termination'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { contractManagement } from 'modb@/contractManagement/api/index'

export default {
  name: 'ContractList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    contractInformation,
    termination,
    payPlan,
    OrganizationSelector,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      contractFilingVisible: false,
      contractHeadId: 0,
      extraData: {
        fileModular: 'cm',
        fileFunction: 'contractMaintainList',
        fileType: 'excel'
      },
      dictCodes: {
        contractStatus: 'CONTRACT_STATUS',
        contractType: 'CONTRACT_TYPE',
        contractClass: 'ELEM_CONTRACT_TYPE',
        formal: 'CONTRACT_FORM2'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-cm/contract/contractHead/importExcel'
      },
      queryformData: {
        buId: null
      },
      closeDialogVisible: false,
      contractCloseForm: {
        contractHeadId: '',
        fileSourceName: '',
        fileuploadId: ''
      },
      fileInfo: {
        fileModular: 'cm',
        fileFunction: 'contractManager',
        fileType: 'excel/word'
      },
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'contractTemplateTable',
      reviewFormNumber: '',
      gridData: [],
      launchPaymentRequestVisible: false,
      pageSize: 15,
      gridId: 'list',
      payPlanList: [],
      selectList: [],
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModiusersAccessfy: false,
      preArr: [
        { prop: 'contractNo', label: this.$t('contractMod.contractNo') },
        { prop: 'sourceNumber', label: this.$t('bidMod.approvalNo') },
        {
          prop: 'contractType',
          label: this.$t('contractMod.operationType'),
          type: 'dict',
          code: 'CONTRACT_TYPE'
        },
        {
          prop: 'contractStatus',
          label: this.$t('orderMod.buyerOrderSynergy.contractStatus'),
          type: 'dict',
          code: 'CONTRACT_STATUS'
        },
        {
          prop: 'buId',
          label: this.$t('contractMod.buId'),
          type: 'OUorganizationSelector',
          multiple: false
        },
        {
          prop: 'contractClass',
          label: this.$t('contractMod.contractType'),
          type: 'dict',
          code: 'ELEM_CONTRACT_TYPE'
        },
        { prop: 'contractName', label: this.$t('contractMod.contractName') },
        {
          prop: 'vendorId',
          label: this.$t('contractMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'frameworkAgreementCode',
          label: this.$t('contractMod.frameworkAgreementCode')
        },
        {
          prop: 'frameworkAgreementName',
          label: this.$t('contractMod.frameworkAgreementName')
        },
        { prop: 'createdBy', label: this.$t('common.creator') }
      ],
      queryParam: {},
      globalNickname: null,
      templTypeList: [],
      preFormObj: {},
      contractDataList2: [],
      sumFormVisible2: false,
      sumForm2: {
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        isFrameworkAgreement: 'Y',
        pageSize: 9999,
        pageNum: 1
      }
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'contractMaintainList'
        ) {
          // 供应商 工作台跳转
          this.queryParam.contractStatus = this.$route.params.contractStatus
          this.preFormObj = Object.assign({}, { contractStatus: this.$route.params.contractStatus })
        } else if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'contractMaintainList'
        ) {
          let contractHeadId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo //
          // 首页跳转进来没有 contractType 值,跳转可能会报错
          let row = {
            ...this.$route.params,
            contractHeadId,
            contractName: formNo // tab 标题显示
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    let _this = this
    let userType = this.$store.getters.userType
    if (userType != 'BUYER') {
      this.preArr = this.preArr.filter(item => item.prop !== 'vendorId' && item.prop !== 'sourceNumber')
    }
    this.tableHeader = [
      {
        prop: 'contractNo',
        label: this.$t('contractMod.contractNo'),
        minWidth: 200
      },
      {
        prop: 'contractName',
        label: this.$t('contractMod.contractName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      { prop: 'sourceNumber', label: this.$t('bidMod.approvalNo'), width: 170 },
      {
        prop: 'contractStatus',
        label: this.$t('common.status'),
        width: 100,
        dataType: 'dict',
        code: 'CONTRACT_STATUS'
      },
      // 操作类型
      {
        prop: 'contractType',
        label: () => this.$t('contractMod.operationType'),
        width: 120,
        dataType: 'dict',
        code: 'CONTRACT_TYPE'
      },
      // 合同类型
      {
        prop: 'contractClass',
        label: this.$t('contractMod.contractType'),
        width: 120,
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE'
      },
      // 签署方式
      {
        prop: 'formal',
        label: this.$t('签署方式'),
        width: 120,
        dataType: 'dict',
        code: 'CONTRACT_FORM2'
      },
      // 业务实体
      { prop: 'buName', label: this.$t('contractMod.buId'), width: 150 },
      // 供应商编码
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 130
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 150
      },
      // 框架协议编号
      {
        prop: 'frameworkAgreementCode',
        label: this.$t('contractMod.frameworkAgreementCode'),
        width: 150
      },
      // 框架协议名称
      {
        prop: 'frameworkAgreementName',
        label: this.$t('contractMod.frameworkAgreementName'),
        width: 150
      },
      // 原合同号
      {
        prop: 'contractOldCode',
        label: this.$t('contractMod.contractOldCode'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this)
      },
      {
        prop: 'contractChangeCode',
        label: this.$t('contractMod.contractChangeCode'),
        width: 150
      },
      {
        prop: 'contractAgreementCode',
        label: this.$t('contractMod.annexId'),
        width: 150
      },
      {
        prop: 'modelName',
        label: this.$t('contractMod.templHeadId'),
        width: 150
      },
      {
        prop: 'effectiveDateFrom',
        label: this.$t('contractMod.constartDate'),
        width: 130,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'contractTerminationCode',
        label: this.$t('contractMod.terminationId'),
        width: 150
      },
      {
        prop: 'endDate',
        label: this.$t('qualitySynergy.endDate2'),
        width: 150
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('contractMod.lastUpdatedBy'),
        width: 130
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('contractMod.lastUpdateDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'vendorConfirmDate',
        label: this.$t('contractMod.vendorConfirmDate'),
        width: 140
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            code: 'cm:contractManager:edit',
            formattor (val) {
              return _this.$t('bidMod.management') // 编辑
            },
            show: function (row) {
              if (
                ((_this.curRole === 'BUYER' &&
                  (row.contractStatus === 'DRAFT' ||
                    row.contractStatus === 'REJECTED' ||
                    row.contractStatus === 'REFUSED' ||
                    row.contractStatus === 'WITHDRAW' ||
                    row.contractStatus === 'SUPPLIER_REJECTED') &&
                  row.createdBy === _this.globalNickname) ||
                (_this.curRole === 'VENDOR' && row.contractStatus === 'SUPPLIER_CONFIRMING')) && row.contractType != 'TERMINATION'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          { // 合同终止编辑
            callback: function (row) {
              this.termination(row, 1)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.management') // 编辑
            },
            show: function (row) {
              if (
                ((_this.curRole === 'BUYER' &&
                  ((row.contractStatus === 'DRAFT' ||
                    row.contractStatus === 'REJECTED' ||
                    row.contractStatus === 'REFUSED' ||
                    row.contractStatus === 'WITHDRAW') && row.contractType == 'TERMINATION') &&
                  row.createdBy === _this.globalNickname) ||
                (_this.curRole === 'VENDOR' && row.contractStatus === 'SUPPLIER_CONFIRMING')) && row.contractType == 'TERMINATION'
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.publishOne(row)
            }.bind(this),
            code: 'cm:contractManager:publish',
            formattor (val) {
              return _this.$t('common.publish')
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                row.contractStatus === 'UNPUBLISHED' &&
                row.createdBy === _this.globalNickname
              ) {
                return true
              } else {
                return false
              }
            }
          },
          { // 终止合同
            callback: function (row) {
              this.termination(row, 2)
            }.bind(this),
            formattor (val) {
              return _this.$t('contractMod.terminationContract')
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                ['ARCHIVED'].includes(row.contractStatus)
              ) {
                return true
              } else {
                return false
              }
            }
          },
          { // 发布签章
            callback: function (row) {
              this.readOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('contractMod.releaseSignPlatform')
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                ['APPROVAL'].includes(row.contractStatus) &&
                ['MIAN_CONTRACT_ADD', 'MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT'].includes(row.contractType) &&
                ['ELECTRONIC_CONTRACT'].includes(row.formal)
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.$emit('tab-add', {
                component: contractInformation,
                params: {
                  flag: 'edit',
                  row: row,
                  isReadOnly: true
                },
                title: row.contractName,
                name: 'contractInformation' + row.contractName
              })
            }.bind(this),
            formattor (val) {
              return _this.$t('common.approve')
            },
            code: 'cm:contractManager:approvalOne',
            show: function (row) {
              if (_this.curRole === 'BUYER' && ['UNDER_REVIEW', 'SUPPLIER_CONFIRMED'].includes(row.contractStatus)) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.approvalOneNot(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('bidMod.approvalRefuse')
            },
            code: 'cm:contractManager:approvalOneNot',
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.contractStatus === 'UNDER_REVIEW') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.archiveBillPop(row)
            }.bind(this),
            code: 'cm:contractManager:archiveBill',
            formattor (val) {
              return _this.$t('contractMod.archive') // [供应商已确认]  归档
            },
            show: function (row) {
              if (
                _this.curRole === 'BUYER' &&
                row.contractStatus === 'UN_ARCHIVED' &&
                row.createdBy === _this.globalNickname
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            code: 'cm:contractManager:delete',
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            show: function (row) {
              if (
                ['DRAFT', 'ABANDONED'].includes(row.contractStatus) &&
                row.createdBy === _this.globalNickname
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.adandonOne(row)
            }.bind(this),
            code: 'cm:contractManager:adandon',
            formattor (val) {
              return _this.$t('common.abandon') // 废弃
            },
            show: (row) =>
              ['REJECTED', 'REFUSED'].includes(row.contractStatus) &&
              row.createdBy === _this.globalNickname
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.getTemplTypeList()

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 终止合同
    termination (row, num) { // num 编辑为1
      let str = num == 1 ? 'edit' : 'termination'
      this.$emit('tab-add', {
        component: termination,
        params: {
          flag: str,
          row: row,
          isReadOnly: false
        },
        title: row.contractName,
        name: 'termination' + row.contractName
      })
    },
    adandonOne (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/abandon',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.successAbandon')) // 废弃成功
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      // "合同管理导入模板.xlsx"
      downloadFileLink(
        '/api-cm/contract/contractHead/importModelDownload',
        this.$t('contractMod.contractManageImp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    close (row) {
      this.contractCloseForm.fileSourceName = ''
      this.contractCloseForm.fileuploadId = ''
      this.contractCloseForm.contractHeadId = row.contractHeadId
      this.closeDialogVisible = true
    },
    paperArchiveConfirm () {
      if (this.contractCloseForm.fileuploadId) {
        // 保存附件
        this.contractCloseForm.contractHeadId = this.contractHeadId
        contractManagement.contract
          .paperArchiveConfirm(this.contractCloseForm)
          .then((data) => {
            this.$message.success(this.$t('contractMod.archiveConfirmSuccess'))
            this.getQuerydata()
            this.contractFilingVisible = false
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.$message.error(this.$t('contractMod.msgUploadFile')) // 请上传文件
      }
    },
    // 确认关闭
    closeConfirm () {
      if (this.contractCloseForm.fileuploadId) {
        // 保存附件
        contractManagement.contract
          .uploadCloseAnnex(this.contractCloseForm)
          .then((data) => {
            this.$message.success(this.$t('contractMod.closeSuccess')) // 关闭成功
            this.getQuerydata()
            this.closeDialogVisible = false
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.$message.error(this.$t('contractMod.msgUploadFile')) // 请上传文件
      }
    },
    getQuerydata (v) {
      let query = v || this.preFormObj
      this.queryParam = query
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      console.log(val)
      this.currentRows = val
    },
    getTemplTypeList () {
      this.$http({
        url: '/api-cm/template/templHead/listPageByParm',
        method: 'POST',
        data: {},
        loading: true
      })
        .then((data) => {
          if (data.data && data.data.list) {
            this.templTypeList = data.data.list.map((v) => {
              return {
                value: v['templHeadId'],
                label: v['templName']
              }
            })
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    exportOne () {
      let contractHeadIdArray = []
      if (this.currentRows.length != 0) {
        contractHeadIdArray = this.currentRows.map((v) => v.contractHeadId)
        // "合同明细导出.xlsx"
        downloadFileLinkByPost(
          '/api-cm/contract/contractHead/importContractMaterialDownload',
          parseTime(new Date()) + this.$t('contractMod.contractDetailExp'),
          contractHeadIdArray
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[6]')
        }) // 请先勾选行信息
      }
    },
    async addOne (contractType) {
      let rowId = null
      let contractOldCode = null
      let mainContractNo = null
      let isReadOnly = false
      if (contractType !== 'MIAN_CONTRACT_ADD') {
        const name =
          contractType === 'MIAN_CONTRACT_ALTER'
            ? this.$t('contractMod.changeInContract')
            : this.$t('contractMod.supplementalAgreement')
        if (!this.currentRows.length) {
          this.$message.warning(this.$t('contractMod.msgContractManage[0]') + `${name}`) // 请选择要以哪个合同为基础创建
          return
        }
        if (this.currentRows.length > 1) {
          this.$message.warning(
            this.$t('contractMod.msgContractManage[1]') +
              `${name}` +
              this.$t('contractMod.msgContractManage[2]')
          ) // 只能选择一个合同作为创建${name}的基础
          return
        }
        if (this.currentRows[0].contractStatus !== 'ARCHIVED') {
          this.$message.warning(this.$t('contractMod.msgContractManage[3]') + `${name}`) // 只能已归档的合同才能创建
          return
        }
        rowId = this.currentRows[0].contractHeadId
        contractOldCode = this.currentRows[0].contractCode
        mainContractNo = this.currentRows[0].contractNo
        let dataV = await contractManagement.changePreCheck({ ceeaContractOldId: rowId })
        if (dataV.code !== '0') {
          return false
        }
      }
      this.$emit('tab-add', {
        component: contractInformation,
        params: {
          flag: 'add',
          rowId,
          contractType,
          contractOldCode,
          mainContractNo,
          isReadOnly: isReadOnly
        },
        title: this.$t('contractMod.createContract'), // 创建合同
        name: 'contractInformation'
      })
    },
    launchPaymentRequestHandle (scope) {
      // 发起付款申请
      this.$http({
        url: '/api-cm/contract/payPlan/startPayApplication',
        method: 'GET',
        params: { payPlanId: scope.row.payPlanId },
        loading: true
      }).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.launchPaymentRequestVisible = false
      })
    },
    // 打开批量维护框架协议弹框
    openDialog () {
      if (this.currentRows.length <= 0) {
        this.$message({
          type: 'error',
          message: this.$t('contractMod.msgSelData')
        }) // 请选择数据!
        return
      } else {
        for (let i = 0; i < this.currentRows.length; i++) {
          if (this.currentRows[i].contractStatus != 'ARCHIVED') {
            this.$message({
              type: 'error',
              message: this.$t('bidMod.selSameVendor2') // 请选择已归档的数据!
            })
            return
          }
        }
      }
      console.log(this.currentRows)
      if (this.currentRows.length > 1) {
        for (let i = 1; i < this.currentRows.length; i++) {
          if (this.currentRows[i].vendorId !== this.currentRows[0].vendorId) {
            this.$message({
              type: 'error',
              message: this.$t('bidMod.selSameVendor') // 请选择相同供应商的数据!
            })
            return
          }
        }
      }
      this.sumForm2.vendorId = this.currentRows[0].vendorId
      this.sumForm2.vendorName = this.currentRows[0].vendorName
      this.globalcontractIds = this.currentRows.map((v) => v.contractHeadId)
      this.queryContractData2()
      this.sumFormVisible2 = true
    },
    // 查询数据
    queryContractData2 () {
      this.$http({
        url: '/api-cm/contract/contractHead/listContractHeadByIsMainAndVendorId',
        method: 'POST',
        data: this.sumForm2,
        loading: true
      })
        .then((res) => {
          this.contractDataList2 = res.data.list
          this.sumFormVisible2 = true
        })
        .catch((err) => {
          console.log(err)
        })
    },
    saveContractCode (index, row) {
      this.$http({
        url: '/api-cm/contract/contractHead/bulkMaintenanceFramework',
        method: 'POST',
        data: {
          contractHeadId: row.contractHeadId,
          contractCode: row.contractCode,
          contractName: row.contractName,
          contractIds: this.globalcontractIds
        },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.success')) // 操作成功!
          this.sumFormVisible2 = false
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    launchPaymentRequest (row) {
      contractManagement.contract.getInfoById(row.contractHeadId).then((res) => {
        const { modelLines } = res.data
        const target = modelLines.find((i) => i.modelKey === 'payPlan')
        if (target) {
          this.payPlanList = JSON.parse(target.modelValue)
          this.launchPaymentRequestVisible = true
        }
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: contractInformation,
        params: {
          flag: 'edit',
          row: row,
          isReadOnly: false
        },
        contractType: row.contractType,
        title: row.contractName,
        name: 'contractInformation' + row.contractName
      })
    },
    readOne (row) {
      console.log(row)
      if (row.contractType == 'TERMINATION') {
        this.$emit('tab-add', {
          component: termination,
          params: {
            flag: 'edit',
            row: row,
            isReadOnly: true
          },
          title: row.contractName,
          name: 'contractInformation' + row.contractName
        })
      } else {
        if (((this.curRole === 'BUYER' &&
            (row.contractStatus === 'DRAFT' ||
              row.contractStatus === 'REJECTED' ||
              row.contractStatus === 'REFUSED' ||
              row.contractStatus === 'WITHDRAW' ||
              row.contractStatus === 'SUPPLIER_REJECTED') &&
            row.createdBy === this.globalNickname) ||
          (this.curRole === 'VENDOR' && row.contractStatus === 'SUPPLIER_CONFIRMING')) && row.contractType != 'TERMINATION') {
          this.$emit('tab-add', {
            component: contractInformation,
            params: {
              flag: 'edit',
              row: row,
              isReadOnly: false
            },
            title: row.contractName,
            name: 'contractInformation' + row.contractName
          })
        } else {
          this.$emit('tab-add', {
            component: contractInformation,
            params: {
              flag: 'edit',
              row: row,
              isReadOnly: true
            },
            title: row.contractName,
            name: 'contractInformation' + row.contractName
          })
        }
      }
    },
    archiveBillPop (row) {
      this.contractCloseForm.fileuploadId = ''
      this.contractCloseForm.fileSourceName = ''
      this.contractFilingVisible = true
      this.contractHeadId = row.contractHeadId
    },
    archiveBill (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/buyerArchive',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    enableOne () {},
    publishOne (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/buyerPublish',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.successPublish')) // 发布成功
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    approvalOne (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/buyerApprove',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('vendorMod.approvalSuccess')) // 审批成功
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    approvalOneNot (row) {
      // 请输入拒绝原因     拒绝原因
      this.$prompt(
        this.$t('contractMod.msgRefuseReason'),
        this.$t('oneStopShopping.refusedReason'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )
        .then(({ value }) => {
          this.$http({
            url: '/api-cm/contract/contractHead/buyerRefused',
            method: 'POST',
            data: {
              contractHeadId: row.contractHeadId,
              approvalAdvice: value
            },
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.success')) // 操作成功
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    deleteOne (row) {
      // 当前操将永久删除这条数据，确认删除这条数据？
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/contract/contractHead/deleteContract/' + row.contractHeadId,
            method: 'DELETE',
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.successDelete')) // 删除成功
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      this.contractCloseForm.fileuploadId = fileId.toString()
      this.contractCloseForm.fileSourceName = fileName
      this.contractCloseForm.fileType = fileType
    },
    // 附件删除
    handleRemove (fileId) {},
    // 删除文件
    handleAttachmentRemove () {
      this.contractCloseForm.fileuploadId = ''
      this.contractCloseForm.fileSourceName = ''
    }
  }
}
</script>
<style scoped lang="scss"></style>
