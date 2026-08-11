<template>
  <el-container class="flex-container the_main_po_list" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 基础信息 -->
        <el-collapse-item :title="$t('vendorMod.companyBaseInfo2')" name="1">
          <BaseForm
            ref="formRef"
            :form-items="formItems"
            :merge-form.sync="form"
            form-name="form"
            :wrapper-col="{ span: 6, gutter: 27 }"
          />
        </el-collapse-item>
        <el-collapse-item :title="$t('orderMod.deliveryNoticeDetail')" name="2">
          <div class="btn_line">
            <!-- 采购申请新增 -->
            <el-button
              type="primary"
              :disabled="isReadOnly"
              class="detail-pbtn"
              @click="openOrderDialog"
            >
              {{ $t('orderMod.createdOrderDetail') }}
            </el-button>
          </div>
          <BaseTable
            ref="detailInforRef"
            style="height: 140px;"
            row-key="orderId"
            border
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
            <!-- 剩余可通知数量(头) -->
            <template #remainingDeliveryNoticeQuantityHeader="{scope}">
              <span style="padding-right: 2px;">{{ scope.column.label }}</span>
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('orderMod.noticeQuantityDesc')"
                placement="top"
              >
                <em class="el-icon-question" />
              </el-tooltip>
            </template>
            <!-- 本次通知数量 -->
            <template #noticeSum="{scope}">
              <el-input
                v-model="scope.row.noticeSum"
                v-input-format="{ type: 'float' }"
                :disabled="isReadOnly"
              />
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
            <!-- 到货日期 -->
            <template #receiveDate="{scope}">
              <el-date-picker
                v-model="scope.row.receiveDate"
                :disabled="isReadOnly"
                :picker-options="endTiumePickerOptions"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
              />
            </template>
            <template #operation="{scope}">
              <el-button
                type="text"
                :disabled="isReadOnly"
                @click="deleteDetials(scope.$index)"
              >
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </BaseTable>
        </el-collapse-item>
        <el-collapse-item :title="$t('bidMod.fileInfo')" name="3">
          <p class="btn_line">
            <el-button
              type="primary"
              :disabled="isReadOnly"
              class="detail-pbtn"
              @click="addUploadFile"
            >
              {{ $t('common.add') }}
            </el-button>
          </p>
          <upload-attach :attachName="'fileSourceName'" :attachData="noticeFileList" :fileInfo="fileInfo" :readonly="isReadOnly" />
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
          v-if="buttonConfigInfo.save"
          type="primary"
          @click="solveHandler('SAVE')"
        >
          {{ $t('common.staging') }}
        </el-button>
        <el-button
          v-if="buttonConfigInfo.submit"
          type="primary"
          @click="solveHandler('SUBMIT')"
        >
          {{ $t('common.publish') }}
        </el-button>
      </template>
    </CToolbar>

    <!-- 新增订单明细 -->
    <purchaseOrderLine
      :visible="orderVisible"
      :form="form"
      @confirmAdd="confirmAdd"
      @close="orderVisible = false"
    />
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import purchaseOrderLine from './components/purchaseOrderLine'
import CToolbar from 'lib@/components/c-toolbar'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import { formItems, detailColumn } from './data/detail'
import { setRepeatData } from 'lib@/utils/util'
import { tabTodoMixin } from '@/utils/mixins'
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail'
export default {
  name: 'DeliveryNoticeDetail',
  components: {
    BaseForm,
    BaseTable,
    purchaseOrderLine,
    CToolbar,
    uploadAttach
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      noticeFileList: [],
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'DEF', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'buyerDeliveryNotice', // 文件所属功能
        fileType: 'excel' // 文件所属类型
      },
      orderVisible: false,
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
        vendorCode: null
      },
      detailModel: [],
      detailModelReal: [],
      formItems: formItems(this),
      detailColumn: detailColumn(this),
      endTiumePickerOptions: {
        disabledDate: time => {
          const start = new Date()
          return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
        }
      }
    }
  },
  computed: {
    // 按钮控制
    buttonConfigInfo () {
      return {
        close: true,
        save: !this.isReadOnly,
        submit: !this.isReadOnly
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
    }
  },
  methods: {
    async queryDetails (id) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/deliveryNotice/getDeliveryNoticeSaveDTO',
        method: 'GET',
        params: {
          deliveryNoticeId: id
        },
        loading: true
      })
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
    // 本次送货数量校验
    noticeNumValidate (rule, value, callback) {
      const index = rule.field.split('.')[1] // 获取行号
      for (let i in this.detailModelReal) {
        let row = this.detailModelReal[i]
        if (index === i) {
          if (!value) callback(new Error(this.$t('orderMod.writeNoticeSum')))
          // 行号n本次通知数量>剩余可通知数量，请检查！
          if (row.noticeSum > row.remainingDeliveryNoticeQuantity) callback(new Error(`${this.$t('vendorMod.relegation.lineNumber')}[${Number(i) + 1}]${this.$t('orderMod.noticeNumCheckTip')}`))
        }
      }
      callback()
    },
    // 确认添加明细
    confirmAdd (selection, ref) {
      const condition = row => {
        return row.orderNumber + '|' + row.orderDetailLineNum
      }
      setRepeatData(this.detailModelReal, selection, condition)
      this.orderVisible = false
    },
    // 添加附件
    addUploadFile () {
      this.noticeFileList.push({
        fileuploadId: null,
        fileSourceName: null
      })
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
      if (type === 'SAVE') this.saveBill()
      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitBill()
        } else {
          this.__focus_error__(message)
        }
      }
    },
    // 暂存
    saveBill () {
      this.$http({
        url: '/api-sup-ce/po/deliveryNotice/saveTemporary',
        method: 'POST',
        data: {
          deliveryNotice: this.form,
          deliveryNoticeDetail: this.detailModelReal,
          fileuploads: this.noticeFileList
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.queryDetails(res.data)
      })
    },
    // 发布
    submitBill () {
      this.$http({
        url: '/api-sup-ce/po/deliveryNotice/publish',
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
    // 打开明细弹窗
    openOrderDialog () {
      const sign =
        !this.form.orgId ||
        !this.form.organizationId ||
        !this.form.vendorId
      if (sign) {
        // 请先选择业务实体、库存组织、供应商！
        return this.$message.warning(this.$t('orderMod.selectInputValue'))
      }
      this.orderVisible = true
    },
    // 明细更新最新值
    asyncGetRealDataSource (data) {
      this.detailModelReal = data
    },
    // 删除明细
    deleteDetials (index) {
      this.detailModelReal.splice(index, 1)
    }
  }
}
</script>

<style lang="scss" scoped>
.btn_line {
  margin: 0 0 10px 0;
}
</style>
