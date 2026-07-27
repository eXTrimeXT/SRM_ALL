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
          disabled
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
              <el-form-item
                prop="materialRequisitionNumber"
                label="委外领料单号"
              >
                <el-input
                  v-model="form.materialRequisitionNumber"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="createdBy"
                label="创建人"
              >
                <el-input
                  v-model="form.createdBy"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="creationDate"
                label="创建日期"
              >
                <el-input
                  v-model="form.creationDate"
                  :disabled="disabled"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- <el-form-item prop="lastUpdatedBy" label="更新人">
                   <el-input v-model="form.lastUpdatedBy" :disabled="disabled"/>
        </el-form-item> -->
              <el-form-item
                prop="handleStatus"
                label="委外领料单处理状态"
              >
                <!-- <el-input v-model="form.handleStatus" /> -->
                <el-select
                  v-model="form.handleStatus"
                  disabled
                  placeholder="请选择"
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
              <el-form-item
                prop="buyerOutsourcingDate"
                label="采购方委外时间"
              >
                <el-date-picker
                  v-model="form.buyerOutsourcingDate"
                  type="date"
                  placeholder="选择日期"
                />
                <!-- value-format="yyyy-MM-dd" -->
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                prop="buyerReceiveAddressContact"
                label="采购方领料地址信息和联系人"
              >
                <el-input
                  v-model="form.buyerReceiveAddressContact"
                  type="textarea"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item
                prop="buyerSpecialRemarks"
                label="采购方特殊说明"
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
        <el-collapse-item
          title="委外领料单明细"
          name="0"
        >
          <!-- <div v-if="!readOnly" style="padding-bottom: 10px;">
            <el-button
              class="detail-pbtn"
              type="primary"
              @click="OsMaterialRequisitionDetailAddLine"
            >
              新增
            </el-button>
          </div> -->
          <el-table
            :data="currentRows"
            style="width: 100%;height:100%"
            border
            height="200"
          >
            <!-- 业务实体 -->
            <el-table-column
              prop="orgName"
              label="业务实体"
              min-width="120"
              align="center"
            />
            <!-- 库存组织 -->
            <el-table-column
              prop="organizationName"
              label="库存组织"
              min-width="120"
              align="center"
            />
            <!-- 采购申请号 -->
            <el-table-column
              prop="requirementHeadNum"
              label="采购申请号"
              min-width="120"
              align="center"
            />
            <!-- 采购订单号 -->
            <el-table-column
              prop="orderNumber"
              label="采购订单号"
              min-width="120"
              align="center"
            />
            <!-- 物料编码 -->
            <el-table-column
              prop="materialCode"
              label="物料编码"
              min-width="120"
              align="center"
            />
            <!-- 物料名称 -->
            <el-table-column
              prop="materialName"
              label="物料名称"
              min-width="120"
              align="center"
            />
            <!-- 采购订单数量 -->
            <el-table-column
              prop="orderNum"
              label="采购订单数量"
              min-width="120"
              align="center"
            />
            <!-- 未交货数量 -->
            <el-table-column
              prop="notReceivedQuantity"
              label="未交货数量"
              min-width="120"
              align="center"
            />
            <!-- 供应商编码 -->
            <el-table-column
              prop="vendorCode"
              label="供应商编码"
              min-width="120"
              align="center"
            />
            <!-- 供应商名称 -->
            <el-table-column
              prop="vendorName"
              label="供应商名称"
              min-width="120"
              align="center"
            />
            <!-- 委外组件编码 -->
            <el-table-column
              prop="baseMaterialCode"
              label="委外组件编码"
              min-width="120"
              align="center"
            />
            <!-- 委外组件名称 -->
            <el-table-column
              prop="baseMaterialName"
              label="委外组件名称"
              min-width="120"
              align="center"
            />
            <!-- 委外组价总数 -->
            <el-table-column
              prop="sumOutsourcingQuantity"
              label="委外组价总数"
              min-width="120"
              align="center"
            />
            <!-- 库存地点 -->
            <el-table-column
              prop="storageLocation"
              label="库存地点"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.storageLocation"
                  code="STORAGE_LOCATION"
                  :disabled="!['add', 'edit'].includes($attrs.params.flag)"
                />
              </template>
            </el-table-column>
            <!-- 现有库存数量 -->
            <el-table-column
              prop="storageQuantity"
              label="现有库存数量"
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
              label="供应商需求数量"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.vendorRequireQuantity"
                  :disabled="readOnly || form.handleStatus !== 'VCREATE'"
                />
              </template>
            </el-table-column>
            <!-- 采购商委外组件数量 -->
            <el-table-column
              prop="buyerOutsourcingQuantity"
              label="采购商委外组件数量"
              min-width="180"
              align="center"
            />
            <!-- 供应商签收数量 -->
            <el-table-column
              prop="vendorReceiptQuantity"
              label="供应商签收数量"
              min-width="180"
              align="center"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.vendorReceiptQuantity"
                  type="number"
                  oninput="value=value.replace(/^\.+|[^\d.]/g,'')"
                  :disabled="readOnly || form.handleStatus === 'VCREATE'"
                  @input="value => quantityChangeHandler(value, scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 供方签收确认时间 -->
            <el-table-column
              prop="vendorConfirmDate"
              label="供方签收确认时间"
              min-width="180"
              align="center"
            />
            <!-- 差异 -->
            <el-table-column
              prop="vendorDiffQuantity"
              label="差异"
              min-width="180"
              align="center"
            />
            <!-- 物料行备注 -->
            <el-table-column
              prop="buyerRemark"
              label="物料行备注"
              min-width="120"
              align="center"
            />
          </el-table>
          <div style="padding: 10px 0;">
            <div>供应商差异说明</div>
            <el-input
              v-model="form.vendorDiffDescription"
              type="textarea"
              rows="3"
              :disabled="readOnly"
            />
          </div>
          <div style="padding: 10px 0;">
            <div>采购商补充说明</div>
            <el-input
              v-model="form.buyerAdditionalRemarks"
              type="textarea"
              rows="3"
              disabled
            />
            <!-- v-if="!readOnly || $attrs.params.flag === 'reply'" -->
          </div>
        </el-collapse-item>
        <!-- 上传附件 -->
        <el-collapse-item
          title="相关附件上传"
          name="1"
        >
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
                  :default-file="{
                    fileId: scope.row.fileuploadId,
                    fileName: scope.row.attachName
                  }"
                  :readonly="true"
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
            />
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
            :open-custom-table="true"
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
            取消
          </el-button>
          <el-button
            v-if="!readOnly && form.handleStatus === 'VCREATE'"
            @click="save"
          >
            暂存
          </el-button>
          <el-button
            v-if="!readOnly && form.handleStatus === 'VCREATE'"
            type="primary"
            @click="submit"
          >
            提交
          </el-button>
          <el-button
            v-if="!readOnly && form.handleStatus === 'SUBMIT'"
            type="primary"
            :disabled="hasDiffNum"
            @click="confirm"
          >
            接受
          </el-button>
          <el-button
            v-if="!readOnly && form.handleStatus === 'SUBMIT'"
            type="primary"
            @click="refuse"
          >
            驳回
          </el-button>
          <!-- 打印 -->
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
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CToolbar from 'lib@/components/c-toolbar'
import { downloadFileLink } from 'lib@/utils/file'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import TableView from 'lib@/components/Table/TableView'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import { osMaterialRequisitionApi } from 'mods@/outsourcingManagementSupplier/api'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'OsmaterialrequisitionEdit',
  components: {
    CToolbar,
    FormWrapper,
    TableView,
    OrganizationSelector,
    QuickSearch
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
      dialogTitle: '委外明细选择',
      dialogFormVisible: false,
      addListParam: {}, // 添加列表
      activeLine: ['0', '1'],
      tableHeader: [
        {
          prop: 'orgName',
          label: '业务实体'
        },
        {
          prop: 'organizationName',
          label: '库存组织'
        },
        {
          prop: 'requirementHeadNum',
          label: '采购申请号'
        },
        {
          prop: 'orderNumber',
          label: '采购订单号'
        },
        {
          prop: 'materialCode',
          label: '物料编码'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        },
        {
          prop: 'orderNum',
          label: '采购订单数量'
        },
        {
          prop: 'vendorCode',
          label: '供应商编码'
        },
        {
          prop: 'vendorName',
          label: '供应商名称'
        },
        {
          prop: 'baseMaterialCode',
          label: '委外组件编码'
        },
        {
          prop: 'baseMaterialName',
          label: '委外组件名称'
        },
        {
          prop: 'sumOutsourcingQuantity',
          label: '委外组价总数'
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
        { prop: 'requirementHeadNum', label: '采购申请编号' },
        { prop: 'orderNumber', label: '采购订单号' },
        {
          prop: 'vendorId',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display'
        },
        { prop: 'orderCreatedBy', label: '采购订单创建人' },
        { prop: 'orgId', label: '业务实体', type: 'OUorganizationSelector' },
        {
          prop: 'orderCreationStartDate',
          label: '创建开始时间',
          type: 'date'
        },
        {
          prop: 'orderCreationEndDate',
          label: '创建结束时间',
          type: 'date'
        }
      ],
      form: {
        materialRequisitionNumber: null,
        handleStatus: 'VCREATE',
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
    hasDiffNum () {
      return this.currentRows.some(i => i.vendorDiffQuantity != 0 && i.vendorDiffQuantity != null)
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
      const xml = encodeURIComponent('database:database:委外物料管理.ureport.xml')
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
      row.vendorDiffQuantity = Math.abs(+value - +(row.buyerOutsourcingQuantity || 0))
    },
    refuse () {
      // 驳回
      var materialRequisitionId = this.form.materialRequisitionId
      let params = Object.assign({}, this.form, { handleStatus: 'REFUSE' })
      // this.form.handleStatus = 'REFUSE'

      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      osMaterialRequisitionApi.refuse({
          osMaterialRequisition: params,
          osMaterialRequisitionAttachList: this.requirementAttaches,
          osMaterialRequisitionDetailList: this.currentRows
        })
        .then(res => {
          // let datas = res.data;
          // console.log(res);
          if (res.code == '0') {
            this.$message({ type: 'success', message: res.message })
            this.$emit('tab-remove', 'osmaterialrequisitionEdit' + materialRequisitionId)
            this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
          } else {
            this.$message({ type: 'error', message: res.message })
          }
        })
    },
    // 行删除
    handleDelClick (index, row) {
      this.requirementAttaches.splice(index, 1)
    },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    handleScriptProgress (percent) {},
    // 移除
    outerHandleRemove (fileuploadId) {},
    outerHandleUploadSuccess (file) {
      const { id, name, createdBy, creationDate } = file
      this.requirementAttaches[this.bankRowIndex].fileuploadId = id.toString()
      this.requirementAttaches[this.bankRowIndex].attachName = name
      this.requirementAttaches[this.bankRowIndex].createdBy = createdBy
      this.requirementAttaches[this.bankRowIndex].creationDate = creationDate
    },
    deleteOneContent (index, row) {
      this.currentRows.splice(index, 1)
      // console.log(this.currentRows)
    },
    handleCurrentChange (val) {
      this.currentRowsIn = val
      // console.log(this.currentRows)
    },
    saveData () {
      this.dialogFormVisible = false
      this.currentRows = this.currentRows.concat(this.currentRowsIn)
    },
    getQuerydata (params) {
      // console.log(params)
      this.addListParam = params
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
          // console.log(vendorOsMaterialRequisitionQueryDtoList)
          this.currentRows = osMaterialRequisitionDetailList
          this.requirementAttaches = osMaterialRequisitionAttachList
        })
    },
    OsMaterialRequisitionDetailDownloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionDetailExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
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
          this.$message({ type: 'error', message: error.message })
        })
    },
    OsMaterialRequisitionDetailAddLine () {
      this.dialogFormVisible = true
    },
    OsMaterialRequisitionAttachDownloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/osmaterialrequisition/exportOsMaterialRequisitionAttachExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
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
          this.$message({ type: 'error', message: error.message })
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
    confirm () {
      // 供方已确认
      var materialRequisitionId = this.form.materialRequisitionId
      if (this.currentRows.length == 0) {
        this.$message({ type: 'error', message: '委外领料单明细不能为空' })
        return false
      }
      let params = Object.assign({}, this.form, { handleStatus: 'COMFIRM' })
      // this.form.handleStatus = 'COMFIRM'

      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      this.$http({
        method: 'POST',
        url: '/api-sup-ce/sup/osmaterialrequisition/confirm',
        timeout: this.timeout,
        headers: {
          Authorization: 'Bearer ' + getToken()
        },
        data: {
          osMaterialRequisition: params,
          osMaterialRequisitionDetailList: this.currentRows,
          osMaterialRequisitionAttachList: this.requirementAttaches
        }
      })
        .then(response => {
          let datas = response
          console.log('!!!!!!!!!   datas :', response)
          console.log(datas)
          if (datas.code == '0') {
            this.$message({ type: 'success', message: datas.message })
            this.$emit('tab-remove', 'osmaterialrequisitionEdit' + materialRequisitionId)
            this.__setTabTodo('osmaterialrequisitionList.getQuerydata')
          } else {
            this.$message({ type: 'error', message: datas.message })
          }
        })
        // .catch(error => {
        //   console.log(error)
        //   this.$message({ type: 'error', message: error.message })
        // })
    },
    save () {
      // 供方暂存
      var materialRequisitionId = this.form.materialRequisitionId
      if (this.currentRows.length == 0) {
        this.$message({ type: 'error', message: '委外领料单明细不能为空' })
        return false
      }
      this.form.handleStatus = 'VCREATE'

      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      if (materialRequisitionId) {
        this.$http({
          method: 'POST',
          url:
            '/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisitionForVendor',
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
              this.$message({ type: 'error', message: datas.message })
            }
          })
          .catch(error => {
            console.log(error)
            this.$message({ type: 'error', message: error.message })
          })
      } else {
        this.$http({
          method: 'POST',
          url: '/api-sup-ce/sup/osmaterialrequisition/addOsMaterialRequisitionForVendor',
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
            this.$message({ type: 'error', message: error.message })
          })
      }
    },
    submit () {
      // 供方提交
      var materialRequisitionId = this.form.materialRequisitionId
      var handleStatus = this.form.handleStatus
      if (this.currentRows.length == 0) {
        this.$message({ type: 'error', message: '委外领料单明细不能为空' })
        return false
      }
      this.form.handleStatus = 'VSUBMIT'

      this.currentRows.forEach(e => {
        if (e.storageQuantity == '') {
          e.storageQuantity = null
        }
      })

      if (materialRequisitionId) {
        this.$http({
          method: 'POST',
          url:
            '/api-sup-ce/sup/osmaterialrequisition/updateOsMaterialRequisitionForVendor',
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
            console.log(datas)
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
            this.$message({ type: 'error', message: error.message })
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
            console.log(datas)
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
            this.$message({ type: 'error', message: error.message })
          })
      }
    },
    cancelBill () {
      const { row } = this.$attrs.params
      this.$emit('tab-remove', 'osmaterialrequisitionEdit' + row.materialRequisitionId)
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
