<template>
  <el-container
    class="osmaterialrequisitionEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          :disabled="
            readOnly || (form.handleStatus !== 'CREATE' && form.handleStatus !== 'VSUBMIT')
          "
        >
          <el-row :gutter="32">
            <!-- 业务实体 -->
            <el-col :span="6">
              <el-form-item
                :label="$t('purchaseDemand.businessEntity')"
                prop="orgId"
              >
                <template>
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    @select="selectHandler"
                  />
                </template>
              </el-form-item>
            </el-col>
            <!-- 库存组织 -->
            <el-col :span="6">
              <el-form-item
                :label="$t('purchaseDemand.invOrg')"
                prop="organizationId"
              >
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="form.organizationId"
                  :parent-id="form.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler2"
                />
              </el-form-item>
            </el-col>
            <!-- 单据类型 -->
            <el-col :span="6">
              <el-form-item
                :label="$t('mould.mouldFlowType')"
                prop="type"
              >
                <DictSelect
                  v-model="form.type"
                  code="OS_MATERIAL_REQ_TYPE"
                />
              </el-form-item>
            </el-col>
            <!-- 供应商 -->
            <el-col :span="6">
              <el-form-item
                :label="$t('common.vendor')"
                prop="vendorName"
              >
                <QuickSearch
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info_all"
                  @close-quicksearch="getVendorObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 委外领料单号 -->
              <el-form-item
                prop="materialRequisitionNumber"
                :label="$t('outsource.materialReqNum')"
              >
                <el-input
                  v-model="form.materialRequisitionNumber"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 创建人 -->
              <el-form-item
                prop="createdUserName"
                :label="$t('common.creator')"
              >
                <el-input
                  v-model="form.createdUserName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 创建日期 -->
              <el-form-item
                prop="creationDate"
                :label="$t('common.creationDate')"
              >
                <el-date-picker
                  v-model="form.creationDate"
                  :format="$formatDatePickerTime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 委外领料单处理状态 -->
              <el-form-item
                prop="handleStatus"
                :label="$t('cusEntry.supplement20250211.outsourcedMaterialRequisitionProcessingStatus')"
              >
                <!-- <el-input v-model="form.handleStatus" /> -->
                <!-- 请选择 -->
                <el-select
                  v-model="form.handleStatus"
                  disabled
                  :placeholder="$t('components.approvalHead.headers.selectNode')"
                >
                  <el-option
                    v-for="item in statusList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 采购方委外时间 -->
              <el-form-item
                prop="buyerOutsourcingDate"
                :label="$t('cusEntry.supplement20250211.purchaseOutsourcingTime')"
              >
              <!-- 选择日期 -->
                <el-date-picker
                  v-model="form.buyerOutsourcingDate"
                  type="date"
                  :format="$formatDatePicker"
                  :placeholder="$t('vendorMod.relegation.optionDate')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <!-- 采购方领料地址信息和联系人 -->
              <el-form-item
                prop="buyerReceiveAddressContact"
                :label="$t('cusEntry.supplement20250211.purchaseAddressInfoAndContactPerson')"
              >
                <el-input
                  v-model="form.buyerReceiveAddressContact"
                  type="textarea"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <!-- 采购方特殊说明 -->
              <el-form-item
                prop="buyerSpecialRemarks"
                :label="$t('cusEntry.supplement20250211.purchaseSpecialInstructions')"
              >
                <el-input
                  v-model="form.buyerSpecialRemarks"
                  type="textarea"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse v-model="activeLine">
        <!-- 委外领料单明细 -->
        <el-collapse-item
          :title="$t('outsource.materialReqItem')"
          name="0"
        >
          <div
            v-if="!readOnly"
            style="padding-bottom: 10px;"
          >
            <el-button
              class="detail-pbtn"
              type="primary"
              @click="OsMaterialRequisitionDetailAddLine"
            >
              <!-- 新增 -->
              {{ $t("common.add") }}
            </el-button>
          </div>
          <el-table
            :data="currentRows"
            style="width: 100%;height:100%"
            border
            height="200"
          >
            <!-- 业务实体 -->
            <el-table-column
              prop="orgName"
              :label="$t('components.organization.ORG')"
              min-width="120"
              align="center"
            />
            <!-- 库存组织 -->
            <el-table-column
              prop="organizationName"
              :label="$t('components.organization.INV')"
              min-width="120"
              align="center"
            />
            <!-- 采购申请号 -->
            <el-table-column
              prop="requirementHeadNum"
              :label="$t('bid_mod.purchaseRequest')"
              min-width="120"
              align="center"
            />
            <!-- 采购订单号 -->
            <el-table-column
              prop="orderNumber"
              :label="$t('orderMod.orderNumber')"
              min-width="120"
              align="center"
            />
            <!-- 物料编码 -->
            <el-table-column
              prop="materialCode"
              :label="$t('common.materialCode')"
              min-width="120"
              align="center"
            />
            <!-- 物料名称 -->
            <el-table-column
              prop="materialName"
              :label="$t('common.materialName')"
              min-width="120"
              align="center"
            />
            <!-- 采购订单数量 -->
            <el-table-column
              prop="orderNum"
              :label="$t('bidMod.purchaseOrderQuantity')"
              min-width="120"
              align="center"
            />
            <!-- 未交货数量 -->
            <el-table-column
              prop="notReceivedQuantity"
              :label="$t('cusEntry.supplement20250211.unDeliveredQuantity')"
              min-width="120"
              align="center"
            />
            <!-- 供应商编码 -->
            <el-table-column
              prop="vendorCode"
              :label="$t('common.vendorCode')"
              min-width="120"
              align="center"
            />
            <!-- 供应商名称 -->
            <el-table-column
              prop="vendorName"
              :label="$t('common.companyName')"
              min-width="120"
              align="center"
            />
            <!-- 委外组件编码 -->
            <el-table-column
              prop="baseMaterialCode"
              :label="$t('outsourcingBomNew.materialCode')"
              min-width="120"
              align="center"
            />
            <!-- 委外组件名称 -->
            <el-table-column
              prop="baseMaterialName"
              :label="$t('outsourcingBomNew.materialName')"
              min-width="120"
              align="center"
            />
            <!-- 委外组价总数 -->
            <el-table-column
              prop="sumOutsourcingQuantity"
              :label="$t('cusEntry.supplement20250211.outsourceGroupPriceTotal')"
              min-width="120"
              align="center"
            />
            <!-- 库存地点 -->
            <el-table-column
              prop="storageLocation"
              :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.storageLocation"
                  :disabled="!['add', 'edit'].includes($attrs.params.flag)"
                  code="STORAGE_LOCATION"
                  @change="setStorageInfo(scope.row.storageLocation, scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 现有库存数量 -->
            <el-table-column
              prop="storageQuantity"
              :label="$t('cusEntry.supplement20250211.currentInventoryQuantity')"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.storageQuantity"
                  :disabled="!['add', 'edit'].includes($attrs.params.flag)"
                  @input="value => quantityChangeHandler(value, scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 供应商需求数量 -->
            <el-table-column
              prop="vendorRequireQuantity"
              :label="$t('cusEntry.supplement20250211.supplierDemandQuantity')"
              min-width="180"
              align="center"
            />
            <!-- 采购商委外组件数量 -->
            <el-table-column
              prop="buyerOutsourcingQuantity"
              :label="$t('cusEntry.supplement20250211.purchaseCommissionedComponentsCount')"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.buyerOutsourcingQuantity"
                  :disabled="
                    (readOnly && $attrs.params.flag !== 'reply') ||
                      !['REFUSE', 'CREATE', 'VSUBMIT'].includes(form.handleStatus)
                  "
                  @input="value => quantityChangeHandler(value, scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 供应商签收数量 -->
            <el-table-column
              prop="vendorReceiptQuantity"
              :label="$t('outsource.vendorReceiptQty')"
              min-width="180"
              align="center"
            />
            <!-- 供方签收确认时间 -->
            <el-table-column
              prop="vendorConfirmDate"
              :label="$t('cusEntry.supplement20250211.supplierSignConfirmationTime')"
              min-width="180"
              align="center"
            />
            <!-- 差异 -->
            <el-table-column
              prop="vendorDiffQuantity"
              :label="$t('outsource.difference')"
              min-width="180"
              align="center"
            />
            <!-- 物料行备注 -->
            <el-table-column
              prop="buyerRemark"
              :label="$t('cusEntry.supplement20250211.materialRowRemark')"
              min-width="120"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.buyerRemark"
                  type="textarea"
                  :disabled="
                    (readOnly && $attrs.params.flag !== 'reply') ||
                      !['REFUSE', 'CREATE', 'VSUBMIT'].includes(form.handleStatus)
                  "
                />
              </template>
            </el-table-column>
            <el-table-column
              v-if="!readOnly || $attrs.params.flag === 'reply'"
              :label="$t('common.operation')"
              width="60"
              fixed="right"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="deleteOneContent(scope.$index, scope.row)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="padding: 10px 0;">
            <div>
              <!-- 供应商差异说明 -->
              {{ $t("outsource.vendorDiffDescription") }}
            </div>
            <el-input
              v-model="form.vendorDiffDescription"
              disabled
              type="textarea"
              rows="3"
            />
          </div>
          <div
            v-if="replyDescVisible"
            style="padding: 10px 0;"
          >
            <div>
              <!-- 采购商补充说明 -->
              {{ $t("outsource.buyerAdditionalRemarks") }}
            </div>
            <el-input
              v-model="form.buyerAdditionalRemarks"
              type="textarea"
              :disabled="readOnly"
              rows="3"
            />
          </div>
        </el-collapse-item>
        <!-- 上传附件 -->
        <!-- 相关附件上传 -->
        <el-collapse-item
          :title="$t('outsource.materialReqFile')"
          name="1"
        >
          <div
            v-if="!readOnly"
            style="padding-bottom: 10px;"
          >
            <el-button
              class="detail-pbtn"
              type="primary"
              @click="OsMaterialRequisitionAttachAddLine"
            >
              <!-- 新增 -->
              {{ $t("common.add") }}
            </el-button>
          </div>
          <el-table
            :data="requirementAttaches"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 附件 -->
            <el-table-column
              align="center"
              prop="attachName"
              :label="$t('purchaseDemand.attachment')"
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.fileuploadId,
                    fileName: scope.row.attachName
                  }"
                  :readonly="readOnly"
                  @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 上传人 -->
            <el-table-column
              align="center"
              prop="createdUserName"
              :label="$t('purchaseDemand.attachmentCreatedBy')"
              :show-overflow-tooltip="true"
            />
            <!-- 上传时间 -->
            <el-table-column
              align="center"
              prop="creationDate"
              :label="$t('purchaseDemand.attachmentCreatedDate')"
              :show-overflow-tooltip="true"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
            <el-table-column
              v-if="!readOnly"
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="handleDelClick(scope.$index, scope.row)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <srm-dialog
        :title="dialogTitle"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <FormWrapper
          :form-array="filterConfig"
          @getFormData="getQuerydata"
        />
        <div style="height:300px">
          <TableView
            :ref="gridId"
            :table-header="tableHeader"
            :check-change="handleCurrentChange"
            :page-size="pageSize"
            :pre-query-data="addListParam"
            :open-custom-table="false"
            :checkbox="true"
            :source="osMaterialRequisitionApi.getDtoByParam"
          />
        </div>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="saveData"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            <!-- 取消 -->
            {{ $t("components.common.cancel") }}
          </el-button>
          <el-button
            v-if="!readOnly && form.handleStatus === 'CREATE'"
            @click="save"
          >
            <!-- 暂存 -->
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="!readOnly"
            type="primary"
            @click="submit"
          >
            <!-- 提交 -->
            {{ $t("common.submit") }}
          </el-button>
          <el-button
            v-if="['COMFIRM'].includes(form.handleStatus)"
            type="primary"
            @click="printBill"
          >
            {{ $t('route.pdfPrint') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import QuickSearch from 'lib@/components/QuickSearch'
import TableView from 'lib@/components/Table/TableView'
import { getDictItem } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import { osMaterialRequisitionApi } from 'modb@/outsourcingManagement/api'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'OsmaterialrequisitionEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    QuickSearch,
    FormWrapper,
    TableView,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      osMaterialRequisitionApi: osMaterialRequisitionApi,
      currentRowsIn: [],
      statusList: [],
      currentRows: [],
      requirementAttaches: [],
      formLabelWidth: '100px',
      gridId: 'list',
      pageSize: 15,
      dialogTitle: this.$t('cusEntry.supplement20250211.outsourceDetailSelection'),  // '委外明细选择'
      dialogFormVisible: false,
      addListParam: {}, // 添加列表
      fileInfo: {
        fileModular: 'workFlow',
        fileFunction: 'workflowReport',
        fileType: 'images'
      },
      activeLine: ['0', '1'],
      tableHeader: [
        {
          prop: 'orgName',
          label: this.$t('components.organization.ORG')  // '业务实体'
        },
        {
          prop: 'organizationName',
          label: this.$t('components.organization.INV')  // '库存组织'
        },
        {
          prop: 'requirementHeadNum',
          label: this.$t('bid_mod.purchaseRequest')  // '采购申请号'
        },
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.orderNumber')  // '采购订单号'
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode')  // '物料编码'
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName')  // '物料名称'
        },
        {
          prop: 'orderNum',
          label: this.$t('bidMod.purchaseOrderQuantity')  // '采购订单数量'
        },
        {
          prop: 'notReceivedQuantity',
          label: this.$t('cusEntry.supplement20250211.unDeliveredQuantity')  // '未交货数量'
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode')  // '供应商编码'
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName')  // '供应商名称'
        },
        {
          prop: 'baseMaterialCode',
          label: this.$t('outsourcingBomNew.materialCode')  // '委外组件编码'
        },
        {
          prop: 'baseMaterialName',
          label: this.$t('outsourcingBomNew.materialName')  // '委外组件名称'
        },
        {
          prop: 'sumOutsourcingQuantity',
          label: this.$t('cusEntry.supplement20250211.outsourceGroupPriceTotal')  //'委外组价总数'
        }
      ],
      filterConfig: [
        {
          prop: 'materialId',
          label: () => this.$t('common.materialCode'), // '物料编码'
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        { prop: 'requirementHeadNum', label: this.$t('orderMod.requirementHeadNum') }, // '采购申请编号'
        { prop: 'orderNumber', label: this.$t('orderMod.orderNumber') },  // '采购订单号'
        // {
        //   prop: 'vendorId',
        //   label: () => this.$t('common.vendorName'), // '供应商名称'
        //   type: 'quicksearch',
        //   showKey: 'companyName',
        //   propKey: 'companyId',
        //   name: 'scc_sup_company_info_all'
        // },
        { prop: 'orderCreatedBy', label: '采购订单创建人' },
        // { prop: 'orgId', label: '业务实体', type: 'OUorganizationSelector' },
        {
          prop: 'orderCreationStartDate',
          label: this.$t('supplierRating.creationStartTime'),  // '创建开始时间'
          type: 'date'
        },
        {
          prop: 'orderCreationEndDate',
          label: this.$t('supplierRating.creationEndTime'),  // '创建结束时间'
          type: 'date'
        }
      ],
      form: {
        orgId: '',
        orgCode: '',
        orgName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        materialRequisitionNumber: null,
        handleStatus: 'CREATE',
        buyerOutsourcingDate: null,
        buyerReceiveAddressContact: null,
        buyerSpecialRemarks: null,
        buyerAdditionalRemarks: null,
        vendorDiffDescription: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      rules: {},
      readOnly: false
    }
  },
  computed: {
    replyDescVisible () {
      return this.form.handleStatus === 'REFUSE'
    },
    disabled () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {},
  created () {
    // 单据状态
    getDictItem('MATERIAL_REQUISITION_STATUS').then(res => {
      this.statusList = adaptDictData(res.data, 'dict')
      console.log(this.statusList)
    })
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag !== 'add') {
      this.getDetail()
    }
  },
  methods: {
    setStorageInfo (code, row) {
      this.$set(row, 'storageLocation', code)
      this.$set(row, 'storageLocationName', this.$getDictLabel('STORAGE_LOCATION', code))
    },
    // 打印
    printBill () {
      // const xml = encodeURIComponent('database:database:委外物料管理.ureport.xml')
      const xml = encodeURIComponent(this.$t('cusEntry.supplement20250211.databaseDatabaseOutsourceMaterialManagementUreportXml'))
      const params = encodeURIComponent(
        `id=${this.form.materialRequisitionId}&type=${this.$getDictLabel(
          'OS_MATERIAL_REQ_TYPE',
          this.form.type
        )}`
      )
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    selectHandler (node, value, scope) {
      // 每次切换先清空库存组织
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null

      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    quantityChangeHandler (value, row) {
      row.vendorDiffQuantity = Math.abs(+value - +(row.vendorReceiptQuantity || 0))
    },
    // 行删除
    handleDelClick (index, row) {
      this.requirementAttaches.splice(index, 1)
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    deleteOneContent (index, row) {
      this.currentRows.splice(index, 1)
      // console.log(this.currentRows)
    },
    handleCurrentChange (val) {
      this.currentRowsIn = val.map(i => ({ ...i, vendorDiffQuantity: '' }))
      // console.log(this.currentRows)
    },
    saveData () {
      this.dialogFormVisible = false
      this.currentRowsIn.forEach(i => {
        console.log(i)
        this.currentRows.push(i)
      })
    },
    getQuerydata (params) {
      // console.log(params)
      this.addListParam = {
        ...params,
        orgId: this.form.orgId,
        organizationId: this.form.organizationId,
        vendorId: this.form.vendorId
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getDetail () {
      osMaterialRequisitionApi.getById(this.$attrs.params.row.materialRequisitionId)
        .then(res => {
          const {
            osMaterialRequisitionDetailList,
            osMaterialRequisitionAttachList,
            osMaterialRequisition
          } = res.data
          this.form = osMaterialRequisition
          // console.log(osMaterialRequisitionDetailList)
          this.currentRows = osMaterialRequisitionDetailList
          this.requirementAttaches = osMaterialRequisitionAttachList
        })
    },
    OsMaterialRequisitionDetailDownloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionDetailExcelTemplate',
        // '导入模板.xlsx'
        this.$t('logisticsMod.importTemplateXLSX')
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    OsMaterialRequisitionDetailExportExcel () {
      let menuInfo = getMenuInfo()
      axios({
        method: 'POST',
        url: `${sysPrefix()}/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionDetailExcel`,
        timeout: this.timeout,
        headers: {
          Authorization: 'Bearer ' + getToken(),
          'X-Fun-Info': menuInfo.secretKey
        },
        data: { id: this.$attrs.params.row.quotaHeadId },
        responseType: 'arraybuffer'
      })
        .then(response => {
          console.log(response)
          const { data } = response
          if (response.headers['content-type'].startsWith('application/json')) {
            let enc = new TextDecoder('utf-8')
            let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
            throw new Error(res.message)
          }
          const blob = new Blob([data])
          const disposition = response.headers['content-disposition'] || ''
          const filename = decodeURIComponent(disposition.split('=')[1])
          const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
          let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
          dom.style.display = 'none'
          dom.href = url
          dom.rel = 'noopener'
          dom.setAttribute('download', filename || `${this.fileName}.xlsx`) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
        })
        .catch(error => {
          console.log(error)
        })
    },
    OsMaterialRequisitionDetailAddLine () {
      if (!this.form.orgId || !this.form.organizationId || !this.form.vendorId) {
        this.$message.warning('请先选择业务实体、库存组织和供应商名称！')
        return
      }
      this.dialogFormVisible = true
    },
    OsMaterialRequisitionAttachDownloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionAttachExcelTemplate',
        // '导入模板.xlsx'
        this.$t('logisticsMod.importTemplateXLSX')
      ).catch(() => {
        // '下载失败'
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    OsMaterialRequisitionAttachExportExcel () {
      axios({
        method: 'POST',
        url: `${sysPrefix()}/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionAttachExcel`,
        timeout: this.timeout,
        headers: {
          Authorization: 'Bearer ' + getToken()
        },
        data: { id: this.$attrs.params.row.quotaHeadId },
        responseType: 'arraybuffer'
      })
        .then(response => {
          console.log(response)
          const { data } = response
          if (response.headers['content-type'].startsWith('application/json')) {
            let enc = new TextDecoder('utf-8')
            let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
            throw new Error(res.message)
          }
          const blob = new Blob([data])
          const disposition = response.headers['content-disposition'] || ''
          const filename = decodeURIComponent(disposition.split('=')[1])
          const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
          let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
          dom.style.display = 'none'
          dom.href = url
          dom.rel = 'noopener'
          dom.setAttribute('download', filename || `${this.fileName}.xlsx`) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
        })
        .catch(error => {
          console.log(error)
        })
    },
    OsMaterialRequisitionAttachAddLine () {
      this.requirementAttaches.push({
        attachId: null,
        fileuploadId: null,
        attachName: ''
      })
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },

    handleSuccess () {
      this.getDetail()
    },
    submit () {
      // 提交
      var materialRequisitionId = this.form.materialRequisitionId
      var handleStatus = this.form.handleStatus
      if (this.currentRows.length == 0) {
        // '委外领料单明细不能为空'
        this.$message({ type: 'error', message: this.$t('cusEntry.supplement20250211.outsourcedMaterialReceiptDetailsNotEmpty') })
        return false
      }
      this.form.handleStatus = 'SUBMIT'
      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      if (materialRequisitionId) {
        this.$http({
          method: 'POST',
          url: '/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisition',
          timeout: this.timeout,
          headers: {
            Authorization: 'Bearer ' + getToken()
          },
          data: {
            osMaterialRequisition: this.form,
            osMaterialRequisitionDetailList: this.currentRows,
            osMaterialRequisitionAttachList: this.requirementAttaches
          }
        })
          .then(response => {
            let datas = response
            if (datas.code == '0') {
              this.$message({ type: 'success', message: datas.message })
              this.$emit('tab-remove', 'osmaterialrequisitionEdit' + materialRequisitionId)
              this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
            } else {
              this.form.handleStatus = handleStatus
              this.$message({ type: 'error', message: datas.message })
            }
          })
          .catch(error => {
            console.log(error)
          })
      } else {
        this.$http({
          method: 'POST',
          url: '/api-sup-ce/sup/osmaterialrequisition/addOsMaterialRequisition',
          timeout: this.timeout,
          headers: {
            Authorization: 'Bearer ' + getToken()
          },
          data: {
            osMaterialRequisition: this.form,
            osMaterialRequisitionDetailList: this.currentRows,
            osMaterialRequisitionAttachList: this.requirementAttaches
          }
        })
          .then(response => {
            let datas = response
            if (datas.code == '0') {
              this.$message({ type: 'success', message: datas.message })
              this.$emit('tab-remove', 'osmaterialrequisitionEdit')
              this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
            } else {
              this.$message({ type: 'error', message: datas.message })
            }
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    save () {
      var materialRequisitionId = this.form.materialRequisitionId
      if (this.currentRows.length == 0) {
        // '委外领料单明细不能为空'
        this.$message({ type: 'error', message: this.$t('cusEntry.supplement20250211.outsourcedMaterialReceiptDetailsNotEmpty') })
        return false
      }
      this.form.handleStatus = 'CREATE';
      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      if (materialRequisitionId) {
        this.$http({
          method: 'POST',
          url: '/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisition',
          timeout: this.timeout,
          headers: {
            Authorization: 'Bearer ' + getToken()
          },
          data: {
            osMaterialRequisition: this.form,
            osMaterialRequisitionDetailList: this.currentRows,
            osMaterialRequisitionAttachList: this.requirementAttaches
          }
        })
          .then(response => {
            this.$message({ type: 'success', message: response.message })
            this.$emit('tab-remove', 'osmaterialrequisitionEdit' + materialRequisitionId)
            this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
          })
          .catch(error => {
            console.log(error)
          })
      } else {
        this.$http({
          method: 'POST',
          url: '/api-sup-ce/sup/osmaterialrequisition/addOsMaterialRequisition',
          timeout: this.timeout,
          headers: {
            Authorization: 'Bearer ' + getToken()
          },
          data: {
            osMaterialRequisition: this.form,
            osMaterialRequisitionDetailList: this.currentRows,
            osMaterialRequisitionAttachList: this.requirementAttaches
          }
        })
          .then(response => {
            let datas = response
            this.$message({ type: 'success', message: datas.message })
            this.$emit('tab-remove', 'osmaterialrequisitionEdit')
            this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'osmaterialrequisitionEdit')
      } else {
        this.$emit('tab-remove', 'osmaterialrequisitionEdit' + row.materialRequisitionId)
      }
      this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.osmaterialrequisitionEdit {
  height: 100%;
  padding-bottom: 50px;
  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
