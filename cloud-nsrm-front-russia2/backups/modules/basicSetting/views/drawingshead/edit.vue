<template>
  <el-container class="drawingsheadEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-form ref="form" :model="form" :rules="rules" :disabled="readOnly">
          <el-row :gutter="27">
            <el-col :span="6">
              <!-- 申请人账号 -->
              <el-form-item prop="applyCode" :label="$t('vendorMod.applyCode')">
                <quick-search
                  :show-input="form.applyCode"
                  show-key="username"
                  :scope-data="form"
                  name="scc_rbac_user_vendor_display"
                  @close-quicksearch="getapplyCode"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 申请人名称 -->
              <el-form-item prop="applyBy" :label="$t('vendorMod.applyBy')">
                <el-input v-model="form.applyBy" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 供应商名称 -->
              <el-form-item prop="vendorName" :label="$t('vendorMod.vendorName')">
                <quick-search
                  :show-input="form.vendorName"
                  show-key="companyName"
                  :scope-data="form"
                  name="scc_sup_company_info_display"
                  @close-quicksearch="getUserObjnotice"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item prop="drawingsNum" :label="$t('vendorMod.drawingsNum')">
                <el-input v-model="form.drawingsNum" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 开始日期 -->
              <el-form-item prop="startDate" :label="$t('components.beginDate')">
                <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 结束日期 -->
              <el-form-item prop="endDate" :label="$t('components.dateClosed')">
                <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 业务实体名称 -->
              <el-form-item prop="orgId" :label="$t('vendorMod.orgEntityName')">
                <organization-selector
                  ref="organizationSelector"
                  v-model="form.orgName"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- 库存组织名称 -->
              <el-form-item prop="organizationName" :label="$t('vendorMod.organizationName')">
                <organization-selector
                  ref="organizationSelector2"
                  v-model="form.organizationName"
                  :parent-id="form.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler2"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <!-- 申请原因 -->
              <el-form-item prop="applyReason" :label="$t('vendorMod.applyReason')">
                <el-input v-model="form.applyReason" type="textarea" :rows="2" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse v-model="activeLine">
        <!-- 明细 -->
        <el-collapse-item :title="$t('vendorMod.details')" name="1">
          <el-container class="flex-container" style="height: 300px;">
            <el-main>
              <div style="padding: 12px 0;">
                <el-button class="detail-pbtn" type="primary" :disabled="readOnly" @click="addLine">
                  <!-- 新增 -->
                  {{ $t('common.add') }}
                </el-button>
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  :disabled="readOnly"
                  @click="exportExcel"
                >
                  <!-- 导出 -->
                  {{ $t('common.export') }}
                </el-button>
                <!-- 导入 -->
                <m-import
                  btn-class-name="detail-pbtn"
                  style="display: inline-block;margin: 0 10px;"
                  :title="$t('common.import')"
                  :disabled="readOnly"
                  up-load-url="/api-base/base/drawingshead/importDrawingsLineExcel"
                  :extra-data="extraData"
                  @downloadTemplate="downloadTemplate"
                  @handleSuccess="handleSuccess"
                />
              </div>
              <base-table
                ref="table"
                :columns="columns"
                :data-source="dataSource"
                :initialize="false"
                row-key="drawingsLineId"
                border
                @asyncGetRealDataSource="asyncGetRealDataSource"
              >
                <!-- <template #categoryName="{ scope }">
                        <QuickSearch
                          :showInput="scope.row.categoryName"
                          show-key="categoryName"
                          :scope-data="scope.row"
                          name="scc_base_purchase_category2"
                          @close-quicksearch="(val)=>getCategoryObj(val,scope)"
                        />
                </template> -->
                <template #materialName="{ scope }">
                  <quick-search
                    :show-input="scope.row.materialName"
                    show-key="materialName"
                    :scope-data="scope.row"
                    name="scc_base_material_item_display"
                    @close-quicksearch="val => getMaterialName(val, scope)"
                  />
                </template>
              </base-table>
            </el-main>
          </el-container>
        </el-collapse-item>
        <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="2">
          <p class="btn_line">
            <el-button
              type="primary"

              class="detail-pbtn"
              :disabled="readOnly"
              @click="addUploadOne"
            >
              {{ $t('common.add') }}
            </el-button>
          </p>
          <el-table :data="drawingsAttachList" style="width: 100%" border max-height="250px">
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
            <!-- 描述 -->
            <el-table-column
              align="center"
              prop="description"
              :label="$t('contractMod.contractDesc')"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.description" />
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
            <el-table-column v-if="!readOnly" :label="$t('common.operation')" width="60">
              <template slot-scope="scope">
                <el-button type="text" @click="handleDelClick(scope.$index, scope.row)">
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <c-toolbar>
        <template #right>
          <el-button  @click="cancelBill">
            <!-- 取消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" :disabled="readOnly"  @click="save">
            <!-- 确认 -->
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'DrawingsheadEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    QuickSearch,
    OrganizationSelector
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      realDataSource: [],
      dataSource: [],
      activeLine: ['1', '2'],
      columns: [
        {
          attrs: {
            prop: 'materialName',
            label: this.$t('vendorMod.materialName') // 物料名称
          },
          slot: 'materialName',
          rules: { required: true, message: this.$t('vendorMod.required') } // 必填
        },
        {
          attrs: {
            prop: 'materialCode',
            label: this.$t('vendorMod.materialCode') // 物料编码
          },
          rules: { required: true, message: this.$t('vendorMod.required') } // 必填
        },
        {
          attrs: {
            prop: 'categoryName',
            label: this.$t('vendorMod.categoryName2') // 品类名称
          },
          rules: { required: true, message: this.$t('vendorMod.required') } // 必填
        },
        {
          attrs: {
            prop: 'categoryCode',
            label: this.$t('vendorMod.categoryCode2') // 品类编码
          },
          rules: { required: true, message: this.$t('vendorMod.required') } // 必填
        },
        {
          attrs: {
            prop: 'operation',
            label: this.$t('common.operation'), // 操作
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem,
              show: () => !this.readOnly
            }
          ]
        }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'quotalinetest',
        fileType: 'excel'
      },
      form: {
        applyId: null,
        applyCode: null,
        applyBy: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        drawingsNum: null,
        startDate: null,
        endDate: null,
        applyReason: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      drawingsAttachList: [],
      rules: {
        applyCode: [{ required: true, message: this.$t('vendorMod.pleaseSelectApplicantAccountNumber') }], // 请选择申请人账号
        applyBy: [{ required: true, message: this.$t('vendorMod.pleaseSelectApplicantName') }], // 请选择申请人名称
        vendorName: [{ required: true, message: this.$t('vendorMod.pleaseSelectSupplierName') }], // 请选择供应商名称
        startDate: [{ required: true, message: this.$t('vendorMod.pleaseSelectAStartDate') }], // 请选择开始日期
        endDate: [{ required: true, message: this.$t('vendorMod.pleaseSelectAnEndDate') }], // 请选择结束日期
        orgId: [{ required: true, message: this.$t('vendorMod.pleaseSelectBusinessEntityName') }], // 请选择业务实体名称
        organizationName: [{ required: true, message: this.$t('vendorMod.pleaseSelectInventoryOrganizationName') }] // 请选择库存组织名称
      },
      readOnly: false
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.getDetail()
    }
  },
  methods: {
    getDetail () {
      this.$api.generate.drawingshead.getById(this.$attrs.params.row.drawingsId).then(res => {
        const { drawingsAttachList, drawingsLineList, drawingsHead } = res.data
        this.form = drawingsHead
        this.dataSource = drawingsLineList
        this.drawingsAttachList = drawingsAttachList
      })
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/drawingshead/exportDrawingsLineExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getDetail()
    },
    getUserObjnotice (val, scope) {
      if (val) {
        scope.vendorId = val.companyId
        scope.vendorName = val.companyName
        scope.vendorCode = val.companyCode
      }
    },
    getapplyCode (val, scope) {
      if (val) {
        scope.applyCode = val.username
        scope.applyBy = val.nickname
      }
    },
    selectHandler (node, value, scope) {
      console.log('node', node)
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    addUploadOne () {
      this.drawingsAttachList.push({
        fileuploadId: null,
        attachName: ''
      })
    },
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdUserName = createdBy
      row.creationDate = creationDate
    },
    outerHandleAttachmentRemove (row) {
      row.fileuploadId = ''
      row.attachName = ''
    },
    // 行删除
    handleDelClick (index, row) {
      this.drawingsAttachList.splice(index, 1)
    },
    exportExcel () {
      axios({
        method: 'POST',
        url: `${sysPrefix()}/api-base/base/drawingshead/exportDrawingsLineExcel`,
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
          dom.setAttribute('download', filename || `${this.fileName}.xlsx`) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
        })
        .catch(error => {
          console.log(error)
          this.$message({ type: 'error', message: error.message })
        })
    },
    save () {
      this.$refs.form.validate(result => {
        this.$refs.table.validate(res => {
          if (result && res) {
            const { flag } = this.$attrs.params
            const data = {
              drawingsHead: this.form,
              drawingsLineList: this.realDataSource,
              drawingsAttachList: this.drawingsAttachList
            }
            this.$api.generate.drawingshead.saveDrawings(data).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else {
            this.__focus_error__()
          }
        })
      })
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    addLine () {
      this.$refs.table.add({})
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'drawingsheadEdit')
      } else {
        this.$emit('tab-remove', 'drawingsheadEdit' + row.drawingsId)
      }
      this.__setTabTodo('drawingsheadList.getQuerydata')
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getMaterialName (val, scope) {
      if (val) {
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialId', val.materialId)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialCode', val.materialCode)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'materialName', val.materialName)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryName', val.categoryName)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryCode', val.categoryCode)
        this.$set(this.$refs.table.form.dataSource[scope.$index], 'categoryId', val.categoryId)
      }
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
.drawingsheadEdit {
  height: 100%;
  padding-bottom: 50px;
  /deep/ .table-wrapper {
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
