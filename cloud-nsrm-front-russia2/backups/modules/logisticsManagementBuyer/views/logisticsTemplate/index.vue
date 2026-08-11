<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="initActive"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            style="float:left;"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            @click="delMore"
          >
            {{
              $t("common.delete")
            }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        checkbox
        :check-change="checkChange"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-pd/logistics/logistics-template-head/listPageByParam"
      />
      <!-- 弹框区域-->
      <srm-dialog
        :title="$t('logisticsMod.logisticsPurAppTemp')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :disabled="readOnly"
          :model="templateHead"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col :span="6">
              <!-- 模板名称 -->
              <el-form-item
                :label="$t('sourcingTemplate.name')"
                :label-width="formLabelWidth"
                prop="templateName"
              >
                <el-input v-model="templateHead.templateName" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 业务模式 -->
              <el-form-item
                :label="$t('logisticsMod.businessMode')"
                :label-width="formLabelWidth"
                prop="businessModeCode"
              >
                <DictSelect
                  v-model="templateHead.businessModeCode"
                  code="BUSINESS_MODE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 运输方式 -->
              <el-form-item
                :label="$t('bid_mod.transportType')"
                :label-width="formLabelWidth"
                prop="transportModeCode"
              >
                <DictSelect
                  v-model="templateHead.transportModeCode"
                  code="TRANSPORT_MODE"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 是否有技术标 -->
              <el-form-item
                :label="$t('logisticsMod.ifTechnicaclBId')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="templateHead.vendorIfSubmitShip"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <!-- 是否需要供方确认 -->
              <el-form-item
                :label="$t('logisticsMod.ifNeedVendorConfirm')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="templateHead.ifNeedVendorSubmit"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 附件模板 -->
              <el-form-item
                prop="templateFileId"
                :label="$t('dataConfMod.attachTemplate')"
              >
                <div>
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: templateHead.templateFileId,
                      fileName: templateHead.templateFileName
                    }"
                    :readonly="false"
                    @on-change="({file}) => handleUploadSuccess(file)"
                  />
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 备注 -->
              <el-form-item
                :label="$t('common.remark')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="templateHead.comments" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 模板编码 -->
              <el-form-item
                :label="$t('dataConfMod.templateCode')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="templateHead.templateCode"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 状态 -->
              <el-form-item
                :label="$t('common.status')"
                :label-width="formLabelWidth"
              >
                <DictSelect
                  v-model="templateHead.status"
                  code="LOGISTICS_STATUS"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                :label="$t('common.creator')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="templateHead.createdUserName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                :label="$t('common.creationTime')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="templateHead.creationDate"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                :label="$t('contractMod.lastUpdatedBy')"
                :label-width="formLabelWidth"
              >
                <el-input
                  v-model="templateHead.lastUpdatedBy"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-table
            :data="templateLines"
            style="width: 100%"
            max-height="351px"
            border
          >
            <el-table-column
              fixed="left"
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 字段名称 -->
            <el-table-column
              fixed="left"
              align="center"
              prop="fieldName"
              :label="$t('logisticsMod.fieldName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 申请是否可显示 -->
            <el-table-column
              align="center"
              prop="applyVisibleFlag"
              :label="$t('logisticsMod.applyVisibleFlag')"
              width="130"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.applyVisibleFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 申请可操作 -->
            <el-table-column
              align="center"
              prop="applyOperateFlag"
              :label="$t('logisticsMod.applyOperateFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.applyOperateFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 申请必填 -->
            <el-table-column
              align="center"
              prop="applyNotEmptyFlag"
              :label="$t('logisticsMod.applyNotEmptyFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.applyNotEmptyFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 采购是否可显示 -->
            <el-table-column
              align="center"
              prop="purchaseVisibleFlag"
              :label="$t('logisticsMod.purchaseVisibleFlag')"
              width="130"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.purchaseVisibleFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 采购可操作 -->
            <el-table-column
              align="center"
              prop="purchaseOperateFlag"
              :label="$t('logisticsMod.purchaseOperateFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.purchaseOperateFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 采购方必填 -->
            <el-table-column
              align="center"
              prop="purchaseNotEmptyFlag"
              :label="$t('logisticsMod.purchaseNotEmptyFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.purchaseNotEmptyFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 供应商是否可显示 -->
            <el-table-column
              align="center"
              prop="vendorVisibleFlag"
              :label="$t('logisticsMod.vendorVisibleFlag')"
              width="140"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.vendorVisibleFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 供应商可操作 -->
            <el-table-column
              align="center"
              prop="vendorOperateFlag"
              :label="$t('logisticsMod.vendorOperateFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.vendorOperateFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 供应商必填 -->
            <el-table-column
              align="center"
              prop="vendorNotEmptyFlag"
              :label="$t('logisticsMod.vendorNotEmptyFlag')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.vendorNotEmptyFlag"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>
            <!-- 备注 -->
            <el-table-column
              align="center"
              prop="comments"
              :label="$t('common.remark')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.comments" />
              </template>
            </el-table-column>
          </el-table>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            v-if="!readOnly"
            @click="dialogFormVisible = false"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="!readOnly"
            type="primary"
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
          </el-button>
          <el-button
            v-else-if="readOnly"
            @click="dialogFormVisible = false"
          >
            {{ $t("common.close") }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { getDictItem } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'LogisticsTemplate',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch,
    MImport
  },
  // mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'categoryAssignRuleTable',
      tableName: 'logisticsTemplate',
      iModal: {
        title: this.$t('common.import'),
        extraData: {
          sourceType: 'WEB_APP',
          uploadType: 'FASTDFS',
          fileModular: 'base',
          fileFunction: 'accountAccess',
          fileType: 'excel'
        },
        upLoadUrl: '/api-base/businessType/importExcel'
      },
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'companyInfoMaintain', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      initActive: true,
      // collapseTagsBool: true,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'templateName', label: this.$t('sourcingTemplate.name') }, // 模板名称
        {
          prop: 'businessModeCode',
          label: this.$t('logisticsMod.businessMode'),
          type: 'dict',
          code: 'BUSINESS_MODE'

        }, // 业务模式
        { prop: 'transportModeCode',
          label: this.$t('bid_mod.transportType'),
          type: 'dict',
          code: 'TRANSPORT_MODE' }, // 运输方式
        { prop: 'templateCode', label: this.$t('dataConfMod.templateCode') }, // 模板编码
        { prop: 'createdBy', label: this.$t('common.creator') },
        {
          prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_STATUS'
        }
      ],
      templateHead: {
        templateHeadId: null,
        templateCode: null,
        templateName: null,
        businessModeCode: null,
        transportModeCode: null,
        status: null,
        vendorIfSubmitShip: null,
        ifNeedVendorSubmit: null,
        comments: null,
        createdBy: null,
        createdUserName: '',
        templateFileId: null,
        templateFileName: null,
        creationDate: null
      },
      templateLines: [],
      globalTemplateLines: [],
      rules: {
        templateName: [
          { required: true, message: this.$t('logisticsMod.msgTemplateName') }
        ], // 请输入模板名称
        businessModeCode: [
          { required: true, message: this.$t('logisticsMod.msgBusinessMode') }
        ],
        transportModeCode: [
          { required: true, message: this.$t('logisticsMod.msgTransportWay') }
        ]
      },
      queryParam: {},
      dutyList: [],
      pubRangeList: [],
      paymentType: [],
      projectTypeList: [],
      paymentDocumentTypeList: [],
      vendorSiteList: [],
      vendorSiteList2: [],
      readOnly: false
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'templateCode',
        label: this.$t('dataConfMod.templateCode'), // 模板编码
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('read', row)
        }.bind(this)
      },
      {
        prop: 'templateName',
        label: this.$t('dataConfMod.templateName'), // 模板名称
        minWidth: 150
      },
      {
        prop: 'businessModeCode',
        label: this.$t('logisticsMod.businessMode'), ///  业务模式
        width: 100,
        dataType: 'dict',
        code: 'BUSINESS_MODE'
      },
      {
        prop: 'transportModeCode',
        label: this.$t('bid_mod.transportType'), // 运输方式
        width: 100,
        dataType: 'dict',
        code: 'TRANSPORT_MODE'
      },
      { prop: 'comments', label: this.$t('common.remark'), minWidth: 150 }, // 备注
      {
        prop: 'status',
        label: this.$t('common.status'),
        width: 100,
        dataType: 'dict',
        code: 'LOGISTICS_STATUS'
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            },
            show: row => row.status === 'DRAFT'
          },
          {
            callback: function (row) {
              this.doEffect(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.active') // 生效
            },
            show: row => ['DRAFT', 'INEFFECTIVE'].includes(row.status)
          },
          {
            callback: function (row) {
              this.doIneffect(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.inactive') // 失效
            },
            show: row => row.status === 'EFFECTIVE'
          },
          {
            callback: function (row) {
              this.deleteOne(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.delete') // 删除
            },
            show: row => row.status === 'DRAFT'
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    getDictItem('LOGISTICS_CONFIG_COLUMNS').then(res => {
      this.globalTemplateLines = adaptDictData(res.data, 'dict').map(val => ({
        fieldCode: val.value,
        fieldName: val.label,
        applyVisibleFlag: 'N',
        purchaseVisibleFlag: 'N',
        vendorVisibleFlag: 'N',
        applyNotEmptyFlag: 'N',
        applyOperateFlag: 'N',
        comments: null,
        purchaseNotEmptyFlag: 'N',
        purchaseOperateFlag: 'N',
        templateLineId: null,
        vendorNotEmptyFlag: 'N',
        vendorOperateFlag: 'N'
      }))
    })
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    checkChange (val) {
      this.currentRows = val
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        for (let i in this.templateHead) {
          this.templateHead[i] = null
        }
        this.templateLines = JSON.parse(
          JSON.stringify(this.globalTemplateLines)
        )
        this.templateHead.status = 'DRAFT'
        this.dialogFormVisible = true
        this.readOnly = false
      } else {
        if (type === 'read') {
          this.readOnly = true
        } else {
          this.readOnly = false
        }
        this.$http({
          url:
            '/api-pd/logistics/logistics-template-head/listTemplateLinesByHeadId',
          method: 'GET',
          params: { headId: row.templateHeadId },
          loading: true
        })
          .then(data => {
            if (data.data) {
              this.templateHead = data.data.templateHead
              this.templateLines = data.data.templateLines
              this.dialogFormVisible = true
            }
          })
          .catch(err => {
            console.log(err)
          })
      }
    },
    downloadTemplate () {
      // 下载模板
      downloadFileLink(
        '/api-base/businessType/importModelDownload',
        this.$t('dataConfMod.importTemplate') + `${new Date().getTime()}.xlsx`
      )
    },
    uploadSuccess (val) {
      if (val && val.code === '0') this.getQuerydata()
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    doEffect (row) {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/effectiveByHeadId',
        method: 'GET',
        params: { headId: row.templateHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    doIneffect (row) {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/inEffectiveByHeadId',
        method: 'GET',
        params: { headId: row.templateHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url:
              '/api-pd/logistics/logistics-template-head/saveTemporary',
            method: 'POST',
            data: {
              templateHead: this.templateHead,
              templateLines: this.templateLines
            },
            loading: true
          })
            .then(data => {
              this.dialogFormVisible = false
              this.$message.success(this.$t('common.successSave'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    addUploadOne () {
      // this.templateLines.push({});
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getUserObj2 (val, scope) {
      scope.strategyUserId = val ? val.userId : ''
      scope.strategyUserName = val ? val.username : ''
      scope.strategyUserNickname = val ? val.nickname : ''
    },
    getUserObj3 (val, scope) {
      scope.personInChargeUserId = val ? val.userId : ''
      scope.personInChargeUsername = val ? val.username : ''
      scope.personInChargeNickname = val ? val.nickname : ''
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url:
              '/api-pd/logistics/logistics-template-head/deleteByHeadId',
            method: 'GET',
            params: { headId: row.templateHeadId },
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    delMore (row) {
      if (!this.currentRows.length) {
        this.$message.error(this.$t('logisticsMod.msgSelOneDataDel')) // 请至少选择一条数据删除
        return
      }
      if (this.currentRows.some(i => i.status != 'DRAFT')) {
        this.$message.error(this.$t('logisticsMod.msgDraftDataDel')) // 只有拟定的数据可以删除
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url:
              '/api-pd/logistics/logistics-template-head/batchDelete',
            method: 'post',
            data: this.currentRows.map(i => i.templateHeadId),
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    // 上传附件成功
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.templateHead.templateFileId = fileId.toString()
      this.templateHead.templateFileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
.form-incontainer /deep/ {
  .c-upload-file {
    justify-content: left;
  }
}
</style>
