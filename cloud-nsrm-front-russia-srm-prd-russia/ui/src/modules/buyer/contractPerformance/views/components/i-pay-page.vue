<!-- 交付单页面 -->
<template>
  <div class="form-container">
    <el-form
      ref="form"
      :model="data"
    >
      <div class="title">
        交付单信息
      </div>
      <el-row :gutter="32">
        <el-col :span="6">
          <el-form-item
            :label="$t('合同编号')"
            prop="contractNo"
          >
            <el-input
              v-model="data.contractNo"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('common.vendorName')"
            prop="vendorName"
          >
            <el-input
              v-model="data.vendorName"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('单据状态')"
            prop="planStatus"
          >
            <dict-select
              v-model="data.planStatus"
              disabled
              code="MILESTONE_STATE"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('contract_mod.contractType')"
            prop="contractClass"
          >
            <dict-select
              v-model="data.contractClass"
              disabled
              code="ELEM_CONTRACT_TYPE"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('bid_mod.businessEntity')"
            prop="buName"
          >
            <el-input
              v-model="data.buName"
              disabled
            />
          </el-form-item>
        </el-col>
        <!-- <el-col :span="6">
          <el-form-item
            :label="$t('bid_mod.inv')"
            prop="invName"
          >
            <el-input
              disabled
              v-model="data.invName"
            ></el-input>
          </el-form-item>
        </el-col> -->
        <el-col :span="6">
          <el-form-item
            :label="$t('履约单号')"
            prop="perOrderNo"
          >
            <el-input
              v-model="data.perOrderNo"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('common.creator')"
            prop="contractCreatedFullName"
          >
            <el-input
              v-model="data.contractCreatedFullName"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('合同总金额（含税）')"
            prop="includeTaxAmount"
          >
            <el-input
              v-model="data.includeTaxAmount"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('common.creationTime')"
            prop="contractCreationDate"
          >
            <el-date-picker
              v-model="data.contractCreationDate"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>
      <div class="title">
        {{ $t('orderMod.buyerOrderSynergy.orderDetailsList') }}
      </div>
      <i-order-detail
        :data="data.perDelivOrderDetaList"
        editable
        :disabled="disabled"
      />
      <div class="title">
        审批信息
      </div>
      <el-row>
        <el-col :span="6">
          <el-form-item
            :label="$t('审批人')"
            prop="approverName"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <i-person-selector
              :value="data.approverName"
              :row="data"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            :label="$t('交付说明')"
            prop="deliveryExplain"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.deliveryExplain"
              type="textarea"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <i-field-view
        ref="iFieldView"
        :data="data.perDelivOrderConfList"
        :disabled="disabled"
      />
      <div class="title">
        评分（百分制）
      </div>
      <el-row :gutter="32">
        <el-col :span="6">
          <el-form-item
            :label="$t('质量评价')"
            prop="qualityEvaluation"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.qualityEvaluation"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item
            :label="$t('质量备注')"
            prop="qualityRemark"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.qualityRemark"
              type="textarea"
              maxlength="30"
              show-word-limit
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('交付评价')"
            prop="deliveryEvaluation"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.deliveryEvaluation"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item
            :label="$t('交付备注')"
            prop="deliveryRemark"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.deliveryRemark"
              type="textarea"
              maxlength="30"
              show-word-limit
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('技术评价')"
            prop="technologyEvaluation"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.technologyEvaluation"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item
            :label="$t('技术备注')"
            prop="technologyRemark"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.technologyRemark"
              type="textarea"
              maxlength="30"
              show-word-limit
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item
            :label="$t('服务评价')"
            prop="serviceEvaluation"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.serviceEvaluation"
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
        <el-col :span="18">
          <el-form-item
            :label="$t('服务备注')"
            prop="serviceRemark"
            :rules="[{ required: true, message: $t('contract_mod.required') }]"
          >
            <el-input
              v-model="data.serviceRemark"
              type="textarea"
              maxlength="30"
              show-word-limit
              :disabled="disabled"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <div class="title">
        {{ $t('accountMod.relevantAttachment') }}
      </div>
      <div style="margin-bottom: 10px;">
        <el-button
          type="primary"
          class="detail-pbtn"
          :disabled="disabled"
          @click="addFile"
        >
          {{ $t('common.add') }}
        </el-button>
      </div>
      <i-mini-table
        :data="data.perDelivOrderAttList"
        :columns="columns"
        border
      />
    </el-form>
  </div>
</template>

<script lang="jsx">
import IOrderDetail from './i-order-detail.vue'
import IFieldView from './i-field-view.vue'
import IMiniTable from './i-mini-table.vue'
import CPeopleSelector from '@/library/components/c-people-selector'

const IPersonSelector = {
  props: ['value', 'row', 'disabled'],
  data () {
    return {
      visible: false
    }
  },
  components: { CPeopleSelector },
  render (h) {
    const listeners = {
      'on-confirm': (data) => {
        if (!data) {
          return
        }
        const [user = {}] = data
        this.row.approverName = user.nickname || ''
        this.row.approverId = user.userId || ''
        this.row.approverCode = user.username || ''
      },
      'update:visible': (value) => (this.visible = value)
    }
    return (
      <div>
        <el-input value={this.value} disabled={this.disabled}>
          <div slot="append">
            <el-button
              icon="el-icon-search"
              disabled={this.disabled}
              size="medium"
              onClick={() => (this.visible = true)}
            />
          </div>
        </el-input>
        <c-people-selector on={{ ...listeners }} visible={this.visible} multiSelect={false} />
      </div>
    )
  }
}

export default {
  name: 'IPayPage',
  components: { IOrderDetail, IFieldView, IMiniTable, IPersonSelector },
  props: ['data', 'mode', 'disabled'],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceBillEdit',
        fileType: 'images'
      },
      visible: false,
      columns: [
        { prop: 'index', type: 'index' },
        {
          prop: 'fileId',
          label: this.$t('附件'),
          render: (h, scope) => {
            return (
              <SrmCommonFile
                extra-data="fileInfo"
                default-file= {
                  {
                    fileId: scope.row.fileId,
                    fileName: scope.row.fileName
                  }
                }
                readonly={this.disabled}
                on={{ 'on-change': ({ file }) => this.uploadSuccess(file, scope.row) }}
              />
            )
          }
        },
        { prop: 'lastUpdatedFullName', label: this.$t('上传人') },
        { prop: 'lastUpdateDate', label: this.$t('上传时间') },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          render: (h, scope) => {
            if (this.mode === 'handOver') {
              return (
                <el-popover placement="top" width="160" v-model={this.visible}>
                  <p>确定删除吗？</p>
                  <div style="text-align: right; margin: 0">
                    <el-button
                      type="text"
                      disabled={this.disabled}
                      onClick={() => {
                        this.visible = false
                      }}
                    >
                      取消
                    </el-button>
                    <el-button
                      type="primary"
                      disabled={this.disabled}
                      onClick={() => this.deleteFileRow(scope)}
                    >
                      确定
                    </el-button>
                  </div>
                  <el-button disabled={this.disabled} slot="reference" type="text">
                    {this.$t('common.delete')}
                  </el-button>
                </el-popover>
              )
            }
            return null
          }
        }
      ]
    }
  },
  methods: {
    validate () {
      console.log(this.$refs)
      return new Promise((rs) => {
        this.$refs.iFieldView.validate().then(i => {
          this.$refs.form.validate((flag) => rs(flag && i))
        })
      })
    },
    async deleteFileRow ({ row, $index }) {
      if (row.perDelivOrderAttId) {
        const { message } = await this.$api.cmPerform.buyer.main.performOrder.deletePerDelivOrderAtt(row.perDelivOrderAttId)
        this.$message.success(message)
      }
      this.data.perDelivOrderAttList.splice($index, 1)
      this.visible = false
    },
    addFile () {
      this.data.perDelivOrderAttList.push({
        fileId: '',
        fileName: ''
      })
    },
    // 上传附件成功
    uploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
.title {
  margin: 10px 0;
  font-size: 14px;
  font-weight: bolder;
}
.form-container {
  padding-bottom: 20px;
}
</style>
