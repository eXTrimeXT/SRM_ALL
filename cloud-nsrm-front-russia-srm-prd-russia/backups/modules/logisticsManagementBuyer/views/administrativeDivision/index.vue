<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        :init-active="initActive"
        :select-dictionary="selectDictionary"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <!--<div style="padding-left:11px;margin:3px;">
        <el-button type="primary" @click="editTab('add')" style="float:left;">{{$t('common.add')}}</el-button>
          <div style="padding: 0 11px;float: left;">
                <m-import
                  ref="import"
                  :title="iModal.title"
                  :extraData="iModal.extraData"
                  :upLoadUrl="iModal.upLoadUrl"
                  @downloadTemplate="downloadTemplate"
                  @handleSuccess="uploadSuccess"
                  :showSuccessDeal="true"
                ></m-import>
            </div>
      </div>-->

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-pd/logistics/region/listPageByParam"
      />
      <!-- 行政区域配置 -->
      <srm-dialog
        v-el-drag-dialog
        :title="$t('logisticsMod.adAreaConf')"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col>
              <!-- 行政区域代码 -->
              <el-form-item
                :label="$t('logisticsMod.regionCode')"
                :label-width="formLabelWidth"
                prop="vendorSiteCode"
              >
                <DictSelect
                  v-model="form.vendorSiteCode"
                  code="VENDOR_SITE_CODE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 事业部 -->
              <el-form-item
                :label="$t('vendorMod.buName')"
                :label-width="formLabelWidth"
                prop="divisionId"
              >
                <DictSelect
                  v-model="form.divisionId"
                  code="DIVISION"
                  filterable
                  clearable
                  @change="setDivision"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 单据类型 -->
              <el-form-item
                :label="$t('bidMod.billType')"
                :label-width="formLabelWidth"
                prop="paymentDocumentType"
              >
                <DictSelect
                  v-model="form.paymentDocumentType"
                  code="REGION_TYPE"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 行政区域名称 -->
              <el-form-item
                :label="$t('logisticsMod.regionName')"
                :label-width="formLabelWidth"
                prop="businessType"
              >
                <DictSelect
                  v-model="form.businessType"
                  code="BUSINESS_TYPE"
                  filterable
                  clearable
                  @change="setBusinessType"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 是否代付 -->
              <el-form-item
                :label="$t('dataConfMod.ifPayAgent')"
                :label-width="formLabelWidth"
              >
                <el-checkbox
                  v-model="form.ifPayAgent"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 是否启用 -->
              <el-form-item
                :label="$t('dataConfMod.enabledUse')"
                :label-width="formLabelWidth"
                prop="duty"
              >
                <el-checkbox
                  v-model="form.enabled"
                  true-label="Y"
                  false-label="N"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col>
              <!-- 备注 -->
              <el-form-item
                :label="$t('common.remark')"
                :label-width="formLabelWidth"
              >
                <el-input v-model="form.remarks" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
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
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'AdministrativeDivision',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector
  },
  // mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'categoryAssignRuleTable',
      tableName: 'administrativeDivision',
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
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      selectDictionary: {
        paymentDocumentType: {}
      },
      initActive: true,
      // collapseTagsBool: true,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'regionCode', label: this.$t('logisticsMod.regionCode') }, // 行政区域代码
        { prop: 'regionName', label: this.$t('logisticsMod.regionName') }, // 行政区域名称
        {
          prop: 'regionLevelCode',
          label: this.$t('logisticsMod.regionLevelCode'),
          type: 'dict',
          code: 'REGION_TYPE'

        }, // 行政区域层级
        {
          prop: 'parentRegionName',
          label: this.$t('logisticsMod.parentRegionName')
        }, // 上级区域名称
        {
          prop: 'regionFullName',
          label: this.$t('logisticsMod.regionFullName')
        }, // 行政区域全称
        { prop: 'status',
          label: this.$t('common.status'),
          type: 'dict',
          code: 'LOGISTICS_STATUS' },
         /// 状态
        {
          prop: 'ifBase',
          label: this.$t('logisticsMod.ifBase'), // 是否基地
          type: 'dict',
          code: 'YES_OR_NO'

        }
      ],
      form: {
        bussinessTypeId: null,
        vendorSiteCode: null,
        paymentDocumentType: null,
        businessType: null,
        businessTypeName: null,
        division: null,
        divisionId: null,
        ifPayAgent: 'N',
        enabled: 'Y',
        remarks: null
      },
      rules: {
        vendorSiteCode: [
          {
            required: true,
            message: this.$t('dataConfMod.msgSelectVendorSite')
          }
        ], // 请选择供应商地点名称
        divisionId: [
          { required: true, message: this.$t('dataConfMod.msgSelDivision') }
        ], // 请选择事业部
        paymentDocumentType: [
          {
            required: true,
            message: this.$t('dataConfMod.msgSelPaymentDocType')
          }
        ], // 请选择单据类型
        businessType: [
          { required: true, message: this.$t('dataConfMod.msgBusinessType') }
        ] // 请选择业务类型
      },
      queryParam: {},
      dutyList: [],
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      pubRangeList: [],
      paymentType: [],
      projectTypeList: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'regionCode',
        label: this.$t('logisticsMod.regionCode'), // 行政区域代码
        width: 130
      },
      {
        prop: 'regionName',
        label: this.$t('logisticsMod.regionName1'), // 行政区域名称(中文)
        minWidth: 170
      },
      {
        prop: 'regionNameEn',
        label: this.$t('logisticsMod.regionName2'), // 行政区域名称(英文)
        minWidth: 170
      },
      {
        prop: 'regionLevelCode',
        label: this.$t('logisticsMod.regionLevelCode'), // 行政区域层级
        width: 140,
        dataType: 'dict',
        code: 'REGION_TYPE'

      },
      {
        prop: 'parentRegionCode',
        label: this.$t('logisticsMod.parentRegionCode'),
        width: 140
      }, // 上级区域代码
      {
        prop: 'parentRegionName',
        label: this.$t('logisticsMod.parentRegionName'),
        width: 140
      }, // 上级区域名称
      {
        prop: 'regionFullName',
        label: this.$t('logisticsMod.regionFullName'),
        width: 140
      }, // 行政区域全称
      {
        prop: 'shoppingCode',
        label: this.$t('logisticsMod.shoppingCode'),
        width: 120
      }, // 商城编码
      {
        prop: 'shoppingName',
        label: this.$t('logisticsMod.shoppingName'),
        width: 120
      }, // 商城名称
      {
        prop: 'ifBase',
        label: this.$t('logisticsMod.ifBase'), // 是否基地
        width: 100,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
        }
      },
      {
        prop: 'ifBidByVehicle',
        label: this.$t('logisticsMod.ifBidByVehicle'), // 是否按决标车型决标
        width: 170,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
        }
      },
      {
        prop: 'status',
        label: this.$t('common.status'), // 状态
        width: 120,
        dataType: 'dict',
        code: 'LOGISTICS_STATUS'
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'), // 更新时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      }
      /* {
        prop: "operation",
        label: _this.$t('common.operation'),
        width: 100,
        showType: "buttons",
        fixed: "right",
        btnStyle: "text",
        buttons: [
          {
            callback: function(row) {
              this.editTab("edit", row);
            }.bind(this),
            formattor(val) {
              return "编辑";
            }
          },
          {
            callback: function(row) {
              this.deleteOne(row);
            }.bind(this),
            formattor(val) {
              return "删除";
            }
          }
        ]
      } */
    ]
    this.defaultTableHeader = this.tableHeader

    this.$nextTick(() => {
      // this.getQuerydata();
    })
  },
  methods: {
    getQuerydata (v) {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = null
        }
        this.form.enabled = 'Y'
        this.form.ifPayAgent = 'N'
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = row[i]
        }
      }
      this.dialogFormVisible = true
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
    setDivision (val) {
      let label = this.$getDictLabel('DIVISION', val)
      this.form.division = label
    },
    setBusinessType (val) {
      let label = this.$getDictLabel('BUSINESS_TYPE', val)
      this.form.businessTypeName = label
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      // 清空库存组织
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          /* this.form.startDate = this.form.startDate
            ? parseTime(this.form.startDate, "{y}-{m}-{d}")
            : null;
          this.form.endDate = this.form.endDate
            ? parseTime(this.form.endDate, "{y}-{m}-{d}")
            : null; */
          this.$http({
            url: '/api-base/businessType/saveOrUpdateBussinessType',
            method: 'POST',
            data: this.form,
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
            url: '/api-base/businessType/batchDelete',
            method: 'POST',
            data: [row.bussinessTypeId],
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
    }
  }
}
</script>
<style scoped lang="scss"></style>
