<template>
  <el-container class="flex-container the_main_po_list" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 基础信息 -->
        <el-collapse-item :title="$t('vendorMod.companyBaseInfo2')" name="1">
          <BaseForm
            ref="formRef"
            :disabled="true"
            :form-items="formItems"
            :merge-form.sync="form"
            form-name="form"
            :wrapper-col="{ span: 6, gutter: 27 }"
          />
        </el-collapse-item>
        <el-collapse-item :title="$t('orderMod.deliveryNoticeDetail')" name="2">
          <BaseTable
            ref="detailInforRef"
            style="height: 140px;"
            row-key="orderId"
            border
            :disabled="true"
            :data-source="detailModel"
            :columns="detailColumn"
            columns-name="detailColumn"
            :initialize="false"
            :editable="false"
            @asyncGetRealDataSource="asyncGetRealDataSource"
          >
            <!-- 采购订单|行号 -->
            <template #orderNumberSlot="{scope}">
              <el-button type="text" @click="readOrder(scope.row)">
                {{ scope.row.orderNumber }}|{{ scope.row.orderDetailLineNum }}
              </el-button>
            </template>
            <!-- 已送货数量（头） -->
            <template #deliveryQuantityHeader="{scope}">
              <span style="padding-right: 2px;">{{ scope.column.label }}</span>
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('orderMod.deliveryQuantityHeaderDesc')"
                placement="top"
              >
                <em class="el-icon-question" />
              </el-tooltip>
            </template>
            <!-- 已入库数量（头） -->
            <template #warehouseQuantityHeader="{scope}">
              <span style="padding-right: 2px;">{{ scope.column.label }}</span>
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('orderMod.warehouseQuantityDesc')"
                placement="top"
              >
                <em class="el-icon-question" />
              </el-tooltip>
            </template>
          </BaseTable>
        </el-collapse-item>
        <el-collapse-item :title="$t('bidMod.fileInfo')" name="3">
          <upload-attach
            :isOperation="false"
            :readonly="true"
            :attachName="'fileSourceName'"
            :attachData="noticeFileList"
            :fileInfo="fileInfo"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button
          v-if="buttonConfigInfo.close"
          @click="cancelBill"
        >
          {{ $t('common.close') }}
        </el-button>
        <el-button
          v-if="buttonConfigInfo.reject"
          type="primary"
          @click="solveHandler('REJECT')"
        >
          {{ $t('common.refused') }}
        </el-button>
        <el-button
          v-if="buttonConfigInfo.accept"
          type="primary"
          @click="solveHandler('ACCEPT')"
        >
          {{ $t('orderMod.accept') }}
        </el-button>
      </template>
    </CToolbar>

    <reject-reason
      :visible="rejectReasonVisible"
      @handleReject="handleReject"
      @close="rejectReasonVisible = false"
    />
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import purchaseOrderLine from 'modb@/orderManagementBuyer/views/buyerDeliveryNotice/components/purchaseOrderLine'
import CToolbar from 'lib@/components/c-toolbar'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import { formItems, detailColumn } from './data/detail'
import { tabTodoMixin } from '@/utils/mixins'
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail'
import rejectReason from '@/library/composition/orderManagementBuyer/rejectReason'
export default {
  name: 'DeliveryNoticeDetail',
  components: {
    BaseForm,
    BaseTable,
    purchaseOrderLine,
    CToolbar,
    uploadAttach,
    purchaseOrderDetail,
    rejectReason
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      rejectReasonVisible: false,
      noticeFileList: [],
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'DEF', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'buyerDeliveryNotice', // 文件所属功能
        fileType: 'excel' // 文件所属类型
      },
      activeDims: ['1', '2', '3'],
      form: {
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        vendorId: null,
        vendorName: null,
        vendorCode: null,
        refuseReason: null
      },
      detailModel: [],
      detailModelReal: [],
      formItems: formItems(this),
      detailColumn: detailColumn(this)
    }
  },
  computed: {
    // 按钮控制
    buttonConfigInfo () {
      return {
        close: true,
        reject: !this.isReadOnly,
        accept: !this.isReadOnly
      }
    },
    isReadOnly () {
      return this.$attrs.params.flag === 'readOnly'
    }
  },
  created () {
    const { row, flag } = this.$attrs.params
    if (flag !== 'add') {
      this.queryDetails(row.deliveryNoticeId)
    } else {
      // this.initForm()
    }
  },
  methods: {
    // 初始化form的key值站位
    initForm () {
      this.formItems.forEach(item => {
        let key = item.computedUIAttrs()['key']
        this.$set(this.form, key, null)
      })
    },
    async queryDetails (id) {
      const { data } = await this.$http({
        url: '/api-sup-ce/sup/deliveryNotice/getDeliveryNoticeSaveDTO',
        method: 'GET',
        params: {
          deliveryNoticeId: id
        },
        loading: true
      })
      console.log(data, 'queryDetails')
      this.form = data.deliveryNotice
      this.detailModel = data.deliveryNoticeDetail
      this.noticeFileList = data.fileuploads
    },
    // 查看采购订单
    readOrder (row) {
      // 查看--只读状态
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'approveNumber',
          row,
          showType: 'readOnly'
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    selectHandler (node, _value, _scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      if (this.form.organizationId) {
        this.form.organizationId = null
        this.form.organizationCode = null
        this.form.organizationName = null
      }
    },
    selectHandler2 (node, _value, _scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    /*
    * @Description: 校验表单表格必填项
    * @return: {
    *   flag: true/false,  校验是否通过
    *   message: 返回填写信息
    * }
    */
    async getCheckForm () {
      const formFiled = await this.$refs.formRef.validate()
      const formFiled1 = await this.$refs.detailInforRef.validate()

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      if (!formFiled1.flag && Object.keys(formFiled1.obj).length > 0) {
        const warnObj1 = Object.keys(formFiled1.obj)[0]
        return {
          flag: formFiled1.flag,
          message: formFiled1.obj[warnObj1][0].message
        }
      }
      return { flag: true }
    },
    // 关闭页面
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('DeliveryNoticeList.getQuerydata')
    },
    // 暂存/发布
    async solveHandler (type) {
      if (type === 'REJECT') this.rejectReasonVisible = true
      if (type === 'ACCEPT') this.acceptBill()
    },
    handleReject (value) {
      if (!value) return this.$message.warning(this.$t('orderMod.writeRefusedReason'))
      this.rejectBill(value)
    },
    // 拒绝
    rejectBill (value) {
      this.form.refuseReason = value
      this.$http({
        url: '/api-sup-ce/sup/deliveryNotice/refuse',
        method: 'POST',
        data: {
          deliveryNotice: this.form,
          deliveryNoticeDetail: this.detailModelReal,
          fileuploads: this.noticeFileList
        },
        loading: true
      }).then(_ => {
        this.cancelBill()
      })
    },
    // 接受
    async acceptBill () {
      const confirmResult = await this.$confirm(
        this.$t('orderMod.confirmNuticeNumAndDateTip'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
      if (confirmResult !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/sup/deliveryNotice/accept',
        method: 'POST',
        data: {
          deliveryNotice: this.form,
          deliveryNoticeDetail: this.detailModelReal,
          fileuploads: this.noticeFileList
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.cancelBill()
      })
    },
    handleDetailChange (select) {
      console.log(select, 'handleDetailChange')
    },
    // 明细更新最新值
    asyncGetRealDataSource (data) {
      this.detailModelReal = data
    },
    // 删除明细
    deleteDetials (index) {
      this.detailModelReal.splice(index, 1)
    },
    // 到货日期
    handleDateChange (date) {
      console.log(date, 'handleDateChange')
    }
  }
}
</script>

<style lang="scss" scoped>
.btn_line {
  margin: 0 0 10px 0;
}
</style>
