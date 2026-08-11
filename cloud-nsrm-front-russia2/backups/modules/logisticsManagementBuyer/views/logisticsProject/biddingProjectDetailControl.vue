<template>
  <div class="conetnt">
    <el-form
      ref="form2"
      :model="bidingConForm"
      class="form-incontainer"
    >
      <el-row class="the_biding_control_row">
        <el-col :span="8">
          <el-form-item :label="$t('bidMod.currentRoundSupplierCount')">
            <el-input
              v-model="bidingConForm.currentRoundSupplierCount"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('bidMod.submitSupplierCount')">
            <el-input
              v-model="bidingConForm.submitSupplierCount"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="$t('bidMod.endTime')">
            <el-input
              v-model="bidingConForm.endTime"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <!-- 本轮截止时间调整原因 -->
          <el-form-item :label="$t('logisticsMod.currentDeadlineReason')">
            <el-input
              v-model="bidingConForm.extendReason"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div style="padding: 3px">
      <el-button
        v-if="bidingStatus == 'PUBLICITY_OF_RESULT'"
        type="primary"
        class="detail-pbtn"
        @click="bidingControlFormVisible2 = true"
      >
        {{ $t("logisticsMod.newRoundDeadline") }}
      </el-button>
      <el-button
        v-if="bidingStatus == 'TENDER_ENDING'"
        type="primary"
        class="detail-pbtn"
        @click="bidingControlFormVisible3 = true"
      >
        {{ $t("logisticsMod.adjustCurrentDeadline") }}
      </el-button>
      <!-- 立即结束投标功能 -->
      <el-button
        v-if="bidingStatus == 'ACCEPT_BID'"
        type="primary"
        class="detail-pbtn"
        @click="endImmediatelyDoBidding"
      >
        {{ $t("logisticsMod.endBidImmediate") }}
      </el-button>
    </div>
    <el-table
      :data="bidControlItemList"
      style="width: 100%"
      border
      height="350px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="80"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="linkManName"
        :label="$t('bidMod.linkMan')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        width="180"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="status"
        :label="$t('bidMod.orderStatus')"
        width="100"
        :formatter="formattor"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="lastUpdatedUserName"
        :label="$t('bidMod.lastUpdatedBy2')"
        width="100"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="lastUpdateDate"
        :label="$t('bidMod.lastUpdateDate2')"
        width="150"
        :show-overflow-tooltip="true"
      />
    </el-table>
    <!-- 新一轮截止时间 -->
    <srm-dialog
      size="small"
      :title="$t('logisticsMod.newRoundDeadline')"
      :visible.sync="bidingControlFormVisible2"
      :close-on-click-modal="false"
    >
      <p>
        <span style="padding-right: 11px">{{
          $t("bidMod.bidingEndDatetime")
        }}</span>
        <el-date-picker
          v-model="bidingEndDatetime"
          type="datetime"
          value-format="timestamp"
          :placeholder="$t('bidMod.datePicker')"
        />
      </p>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          :disabled="!bidingEndDatetime"
          @click="startDoBiding"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button
          @click="bidingControlFormVisible2 = false"
        >
          {{
            $t("common.cancel")
          }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 调整本轮截止时间 -->
    <srm-dialog
      :visible.sync="bidingControlFormVisible3"
      :title="$t('logisticsMod.adjustCurrentDeadline')"
      size="middle"
    >
      <el-form
        ref="form"
        :model="endDatetimeForm"
        class="form-incontainer"
        :rules="rules"
      >
        <el-row>
          <el-col>
            <el-form-item
              :label="$t('bidMod.extBidingEndDatetime')"
              prop="extBidingEndDatetime"
            >
              <el-date-picker
                v-model="endDatetimeForm.extBidingEndDatetime"
                type="datetime"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item
              :label="$t('bidMod.bidingExtendReason')"
              prop="bidingExtendReason"
            >
              <el-input
                v-model="endDatetimeForm.bidingExtendReason"
                type="textarea"
                :rows="2"
                :placeholder="$t('common.pleaseTypeContents')"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="extendDoBiding"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="bidingControlFormVisible3 = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'

export default {
  name: 'BiddingProjectDetailControl',
  components: { QuickSearch, MainHeader },
  props: [
    'bidingConForm',
    'bidingStatus',
    'scopeBidingId',
    'bidControlItemList'
  ],
  data () {
    return {
      bidingControlFormVisible2: false,
      bidingControlFormVisible3: false,
      bidingEndDatetime: null,
      endDatetimeForm: {
        extBidingEndDatetime: null,
        bidingExtendReason: null
      },
      rules: {
        extBidingEndDatetime: [
          { required: true, message: this.$t('bidMod.bidMsgList[40]') }
        ], // 请选择截止时间
        bidingExtendReason: [
          {
            required: true,
            message: this.$t('logisticsMod.msgExtensionReason')
          }
        ] // 请输入延长原因
      }
    }
  },
  watch: {},
  methods: {
    formattor (row) {
      return this.$getDictLabel('BIDDING_ORDER_STATES', row.status)
    },
    endImmediatelyDoBidding () {
      this.$emit('endImmediatelyDoBidding')
    },
    // 调整本轮截止时间
    extendDoBiding () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/biding/extendBiding',
            method: 'POST',
            data: {
              bidingId: this.scopeBidingId,
              endTime: this.endDatetimeForm.extBidingEndDatetime,
              extendReason: this.endDatetimeForm.bidingExtendReason
            },
            loading: true
          })
            .then(res => {
              this.$emit('getFormDetail', this.scopeBidingId)
              this.$emit('getTab5List', this.scopeBidingId)
              this.$message.success(res.message)
              this.bidingControlFormVisible3 = false
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    // 新一轮截止时间
    startDoBiding () {
      this.$http({
        url: '/api-pd/logistics/biding/startBiding',
        method: 'POST',
        data: {
          bidingId: this.scopeBidingId,
          endTime: this.bidingEndDatetime
        },
        loading: true
      })
        .then(res => {
          this.$emit('getFormDetail', this.scopeBidingId)
          this.$emit('getTab5List', this.scopeBidingId)
          this.$message.success(res.message)
          this.bidingControlFormVisible2 = false
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.conetnt /deep/ {
  // .form-incontainer .el-row .el-form-item:first-child {
  //   margin-left: 0;
  //   margin-right: 0;
  // }
}
</style>
