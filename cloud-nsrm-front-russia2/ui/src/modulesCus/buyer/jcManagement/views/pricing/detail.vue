<template>
  <!-- <CWorkflowMulti
    ref="workflowMulti"
    :fun-params="workflowParamsInfo"
    :button-config-info="buttonConfigInfo"
    :button-custom="buttonCustom"
    @tab-click="workflowView"
    @workflow-handler="workflowHandler"
    @flow-cancel="flowCancelHandler"
    @click-handler="type => saveBill(type)"
    @submit-direct="type => saveBill(type)"
    @confirm="(type, comment) => saveBill(type, comment)"
    @close-tab="back"
  > -->
  <ApprovalProcess
    :business-id="workflowBusinessId"
    business-type="EXT_SOU_PUR_FIX_PRICE"
    :approval-status="baseInfo.fixPriceStatus"
    :status-map="statusMap"
    :readonly="$attrs.params.type === 'view'"
    :operation-pre-options="operationPreOptions"
    @approval-handler-callback="approvalHandlerCallback"
  >
    <el-form
      ref="relForm"
      :model="baseInfo"
      style="padding-bottom:40px"
      :rules="rules"
      :disabled="readOnly"
    >
      <el-collapse v-model="activeList" class="tab-form-style">
        <!-- ****************************************** 报价信息 ************************************** -->
        <!-- '报价信息' -->
        <el-collapse-item name="1" :title="$t('bidMod.quoteInfo')">
          <el-row :gutter="32">
            <el-col :span="6">
              <!-- '项目编号' -->
              <el-form-item :label="$t('bidMod.bidingNum')" prop="designProjectCode">
                <QuickSearch
                  :show-input="baseInfo.designProjectCode"
                  show-key="designProjectCode"
                  :scope-data="baseInfo"
                  name="scc_npm_sou_purinq_project_fixprice"
                  @close-quicksearch="getProjectInfo"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '项目名称' -->
              <el-form-item :label="$t('bidMod.bidingName')">
                <el-input
                  v-model="baseInfo.designProjectName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '轮数' -->
              <el-form-item :label="$t('cusEntry.supplement20250205.roundCount')">
                <el-input
                  v-model="baseInfo.designNum"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '创建人' -->
              <el-form-item :label="$t('common.creator')">
                <el-input
                  v-model="baseInfo.designCreateNickName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '联系方式' -->
              <el-form-item :label="$t('vendorMod.contactMethod')">
                <el-input
                  v-model="baseInfo.designCreatePhone"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '创建时间' -->
              <el-form-item :label="$t('common.creationTime')">
                <el-date-picker
                  v-model="baseInfo.creationDate"
                  type="date"
                  :format="$formatDatePicker"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item :label="$t('cusEntry.centralizedPurchase.executionTimeFrom')" prop="executeTimeFrom">
                <el-date-picker
                  v-model="baseInfo.executeTimeFrom"
                  :format="$formatDatePicker"
                  type="date"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item :label="$t('cusEntry.centralizedPurchase.executionTimeTo')" prop="executeTimeTo">
                <el-date-picker
                  v-model="baseInfo.executeTimeTo"
                  :format="$formatDatePicker"
                  type="date"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '供货范围' -->
              <el-form-item :label="$t('cusEntry.centralizedPurchase.supplyScope')">
                <DictSelect
                  v-model="baseInfo.designArea"
                  code="REGION"
                  multiple
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <!-- '最终询价对比表' -->
              <el-form-item :label="$t('cusEntry.supplement20250205.finalInquiryComparisonTable')" prop="fileId">
                <SrmCommonFile
                  limit="10"
                  multiple
                  :extraData="{
                    fileModular: 'inq',
                    fileFunction: 'priceOrders',
                    fileType: 'images'
                  }"
                  :file-list="fileList"
                  :readonly="readOnly"
                  @on-change="({fileList}) => handleUploadSuccess(fileList)"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <!-- '项目介绍' -->
              <el-form-item :label="$t('cusEntry.centralizedPurchase.projectDesc')">
                <el-input
                  v-model="baseInfo.designProjIntroduce"
                  type="textarea"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <el-collapse-item name="2" :title="$t('cusEntry.centralizedPurchase.priceDetail')">
          <!-- <div>
            <AuthorityButton
              type="primary"
              style="margin-bottom: 10px;"
            >
              {{ $t('cusEntry.centralizedPurchase.sayncData') }}
            </AuthorityButton>
          </div> -->
          <el-table
            :data="priceDetailList"
            border
            max-height="500px"
          >
            <el-table-column
              type="index"
              align="center"
              width="60"
            />
            <!-- '物资编码' -->
            <el-table-column
              align="center"
              prop="itemCode"
              :label="$t('cusEntry.inq.materialCode')"
              minWidth="120"
            />
            <!-- '物资名称' -->
            <el-table-column
              align="center"
              prop="itemDesc"
              :label="$t('cusEntry.competition.materialName')"
              minWidth="120"
            />
            <!-- '规格型号' -->
            <el-table-column
              align="center"
              prop="model"
              :label="$t('vendorMod.specification')"
              minWidth="120"
            />
            <!-- '计量单位' -->
            <el-table-column
              align="center"
              prop="unit"
              :label="$t('cusEntry.competition.measurementUnit')"
              minWidth="120"
              :formatter="row => {
                return row.unit ? $getDictLabel('unit', row.unit) : ''
              }"
            />
            <!-- '备注' -->
            <el-table-column
              align="center"
              prop="remark"
              :label="$t('components.eio.headers.remark')"
              minWidth="120"
            />
            <el-table-column
              v-for="item in areaList"
              :key="item.areaCode"
              :label="item.areaName"
              align="center"
            >
              <el-table-column
                :label="$t('cusEntry.centralizedPurchase.notTaxPrice')"
                prop="notaxPrice"
                width="100"
                align="center"
                show-overflow-tooltip
                :formatter="(row) => {
                  return getMaterialBidVendorPropsValue(item.areaCode, row.vendorOrderItemList, 'notaxPrice')
                }"
              />
              <el-table-column
                :label="$t('cusEntry.centralizedPurchase.extWarrantyPeriod')"
                prop="extWarrantyPeriod"
                width="100"
                align="center"
                show-overflow-tooltip
                :formatter="(row) => {
                  return getMaterialBidVendorPropsValue(item.areaCode, row.vendorOrderItemList, 'extWarrantyPeriod')
                }"
              />
              <el-table-column
                :label="$t('cusEntry.centralizedPurchase.winVendor')"
                prop="vendorName"
                width="100"
                align="center"
                show-overflow-tooltip
                :formatter="(row) => {
                  return getMaterialBidVendorPropsValue(item.areaCode, row.vendorOrderItemList, 'vendorName')
                }"
              />
            </el-table-column>
            <!-- '审批状态' -->
            <el-table-column
              align="center"
              prop="fixPriceStatus"
              :label="$t('common.approvalStatus')"
              minWidth="120"
              :formatter="row => {
                return row.fixPriceStatus ? $getDictLabel('SOU_APPROVAL_STATUS', row.fixPriceStatus) : ''
              }"
            />
          </el-table>
          <CPagination
            :total="pagination.total"
            :page-num="pagination.pageNum"
            :page-size="pagination.pageSize"
            @current-change="paginationCurrentChange"
            @size-change="paginationSizeChange"
          />
        </el-collapse-item>
      </el-collapse>
    </el-form>
  </ApprovalProcess>
  <!-- </CWorkflowMulti> -->
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import { validEmail, validatePhone } from '@/utils/validate'
import WorkflowCommon from '@/library/mixins/workflow-common'
import priceApi from 'modcb@/jcManagement/api'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import ApprovalProcess from 'modc@/components/approval-process'
export default {
  name: 'JcQuotationDetail',
  components: {
    BaseForm,
    pictureCard,
    CToolbar,
    QuickSearch,
    ApprovalProcess,
    CPagination
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      curStatus: 0,
      activeList: ['1', '2'],
      baseInfo: {
        executionTimeFrom: '',
        executionTimeTo: '',
        fixPriceStatus: 'DRAFT',
        creationDate: '',
        designProjectCode: '',
        designProjectName: '',
        designNum: '',
        designCreateNickName: '',
        designCreatePhone: '',
        executeTimeFrom: '',
        executeTimeTo: '',
        designArea: [],
        fileId: null
      },
      rules: {
        designProjectCode: [{ required: true, message: this.$t('cusEntry.tipMessage.selectProjectCode') }],
        executeTimeFrom: [{ required: true, message: this.$t('cusEntry.tipMessage.executeTimeFromMsg') }],
        executeTimeTo: [{ required: true, message: this.$t('cusEntry.tipMessage.executeTimeToMsg') }],
        fileId: [{ required: true, message: this.$t('cusEntry.tipMessage.comparisonPriceFile') }]
      },
      selectUserList: [],
      projectList: [], // 项目列表
      fileList: [], // 附件列表
      areaList: [], // 区域列表
      priceDetailList: [],
      pagination: {
        total: 0,
        pageSize: 15,
        pageNum: 10
      },
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'SUBMITTED', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      }
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.baseInfo.fixPriceStatus) && !this.readOnly
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.baseInfo.fixPriceStatus) && this.workflowParamsInfo.integrationMode === 'Push' && !this.readOnly
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.baseInfo.fixPriceStatus)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.baseInfo.purFixPriceHeadId || null
    },
    readOnly () {
      return this.$attrs.params.type === 'view'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    workflowTabDisabled () {
      return false
    }
  },
  created () {
    this.getButtonConfig()
    if (this.$attrs.params.type !== 'add') {
      const id = this.$attrs.params.row.purFixPriceHeadId
      this.getDetail(id)
    }
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let validForm = false
      await this.$refs.relForm.validate(valid => {
        validForm = valid
      })
      if (!validForm) {
        this.$message.warning(this.$t('cusEntry.tipMessage.required'))
        return false
      }
      // 调用暂存接口
      this.baseInfo.designArea = this.baseInfo.designArea.length ? this.baseInfo.designArea.join() : ''
      const payload = {
        ...this.baseInfo,
        fileList: this.fileList,
        tempSave: true
      }
      const res = await priceApi.price.saveOrSubmit(payload)
      this.baseInfo.designArea = this.baseInfo.designArea ? this.baseInfo.designArea.split(',') : []
      this.baseInfo.purFixPriceHeadId = res.data.purFixPriceHeadId
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveBill('SAVE')
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    /* 流程发起取消 */
    flowCancelHandler () {
      this.baseInfo.designArea = this.baseInfo.designArea ? this.baseInfo.designArea.split(',') : []
    },
    /* 页码变更 */
    paginationCurrentChange (pageNum) {
      this.pagination.pageNum = pageNum
      const projectId = this.baseInfo.souProjectId
      projectId && this.getBidDetail(projectId)
    },
    /* 页条数变更 */
    paginationSizeChange (pageSize) {
      this.pagination.pageSize = pageSize
      const projectId = this.baseInfo.souProjectId
      projectId && this.getBidDetail(projectId)
    },
    /* 获取详情 */
    getDetail (id) {
      priceApi.price.getDetail(id).then(res => {
        if (res.data) {
          const {
            itemInfo,
            fileList,
            ...form
          } = res.data
          this.fileList = fileList
          this.baseInfo = form
          this.baseInfo.fileId = this.fileList.map(item => item.fileId).join()
          this.baseInfo.designArea = form.designArea ? form.designArea.split(',') : []
          const {
            areas,
            itemList
          } = itemInfo || {}
          this.areaList = this.setAreaStruct(areas)
          this.pagination.total = itemList.total
          this.priceDetailList = itemList.list || []
        }
      })
    },
    /* 获取报价明细 */
    getBidDetail (projectId) {
      const {
        pageNum,
        pageSize
      } = this.pagination
      const payload = {
        projectId,
        pageNum,
        pageSize
      }
      priceApi.price.queryBidDetail(payload).then(res => {
        if (res.data) {
          const {
            areas,
            itemList
          } = res.data
          this.areaList = this.setAreaStruct(areas)
          this.pagination.total = itemList.total
          this.priceDetailList = itemList.list || []
        }
      })
    },
    /* 项目变更 */
    getProjectInfo (value) {
      const {
        designProjectCode = '',
        designProjectName = '',
        designNum = '',
        designCreateUsername = '',
        designCreateNickName = '',
        designCreatePhone = '',
        designProjIntroduce = '',
        designArea = '',
        projectId = ''
      } = value || {}
      this.baseInfo = Object.assign(this.baseInfo, {
        designProjectCode,
        designProjectName,
        designNum,
        designCreateUsername,
        designCreateNickName,
        designCreatePhone,
        designProjIntroduce,
        souProjectId: projectId,
        designArea: designArea ? designArea.split(',') : []
      })
      if (projectId) {
        /* 查询定价明细 */
        const payload = {
          projectId,
          pageNum: 1,
          pageSize: 15
        }
        priceApi.price.queryBidDetail(payload).then(res => {
          if (res.data) {
            const {
              areas,
              itemList
            } = res.data
            this.areaList = this.setAreaStruct(areas)
            this.pagination.total = itemList.total
            this.priceDetailList = itemList.list || []
          }
        })
      } else {
        this.priceDetailList = []
      }
    },
    /* 构造区域结构 */
    setAreaStruct (areas = {}) {
      const keys = Object.keys(areas)
      return keys.map(item => ({
        areaCode: item,
        areaName: areas[item]
      }))
    },
    /* 获取物料对应的区域的中标供应商对应的字段值 */
    getMaterialBidVendorPropsValue (areaCode, bidVendor = {}, prop) {
      /* 获取中标供应商的数据 */
      const bidVendorAreaKeys = Object.keys(bidVendor)
      if (bidVendorAreaKeys.length === 0) return ''
      /* 获取对应区域的中标供应商 */
      return (bidVendor[areaCode] || {})[prop]
    },
    /* 附件上传 */
    handleUploadSuccess (fileList) {
      this.fileList = fileList?.map(item => ({
        fileId: item.fileId,
        fileName: item.fileName
      })) || []
      this.baseInfo.fileId = this.fileList.map(item => item.fileId).join()
    },
    saveBill (type) {
      this.$refs.relForm.validate(valid => {
        if (valid) {
          this.baseInfo.designArea = this.baseInfo.designArea.length ? this.baseInfo.designArea.join() : ''
          const payload = {
            ...this.baseInfo,
            fileList: this.fileList,
            tempSave: type === 'SAVE'
          }
          priceApi.price.saveOrSubmit(payload).then(async res => {
            if (res.data) {
              this.baseInfo.purFixPriceHeadId = res.data.purFixPriceHeadId
              if (type === 'SAVE') {
                this.$message.success(this.$t('common.successSave'))
                this.getDetail(res.data.purFixPriceHeadId)
              } else {
                await this.handlerAfter(type, 'Y', () => {
                  this.__setTabTodo('JcPricingList.getQueryData')
                })
              }
            }
          }).catch(() => {
            this.baseInfo.designArea = this.baseInfo.designArea ? this.baseInfo.designArea.split(',') : []
          })
        } else {
          this.$message.warning(this.$t('cusEntry.tipMessage.required'))
        }
      })
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = false
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'EXT_SOU_PUR_FIX_PRICE'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    }
  }
}
</script>

<style scoped lang="scss">
.off-cursor {
  cursor: pointer;
}
.search-po {
  float: right;
}
:deep(.el-input__clear) {
  font-size: 12px;
  width: 12px;
}
:deep(.el-input__suffix) {
  height: 28px;
  padding: 0 4px;
  color: #96999c;
  line-height: 28px;
  margin: 1px 0;
  &:hover {
    color: #0077ff;
    border-color: #96999c;
    background-color: #f6f6f6;
  }
}
.timeBox{
    display: flex;
    justify-content: flex-start;
    align-items: center;
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    >.timeNum{
        color: red;
    }
}
.tip{
    font-size: 12px;
    color:red;
    display: inline-block;
    margin: 8px 0;
}
</style>
