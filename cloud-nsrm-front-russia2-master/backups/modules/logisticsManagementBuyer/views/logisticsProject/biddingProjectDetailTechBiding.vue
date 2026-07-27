<template>
  <el-collapse
    v-model="activeDims"
    class="tab-form-style"
  >
    <!-- 技术标信息 -->
    <el-collapse-item
      v-if="allParams.biding.ifVendorSubmitShipDate == 'Y'"
      :title="$t('logisticsMod.techBidInfo')"
      name="1"
    >
      <p style="margin:4px;">
        <el-button
          class="detail-pbtn"
          type="primary"
          @click="openTechBidding"
        >
          {{ $t("bidMod.techopenBiding") }}
        </el-button>
      </p>
      <shipTableClumn
        ref="shipTableClumnId"
        :table-header="tableHeader"
        operate-flag-type="vendorOperateFlag"
        visible-flag-type="vendorVisibleFlag"
        :schedule-form="scheduleForm"
        :transport-flag="transportFlag"
        :is-read-only="true"
      >
        <template slot="header">
          <!-- 供应商编码 -->
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('common.vendorCode')"
            :show-overflow-tooltip="true"
            width="150"
          />
          <!-- 供应商名称 -->
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            :show-overflow-tooltip="true"
            width="150"
          />
        </template>
      </shipTableClumn>
    </el-collapse-item>
    <!-- 附件信息 -->
    <el-collapse-item
      :title="$t('bidMod.fileInfo')"
      name="2"
    >
      <el-table
        :data="lgtVendorFiles"
        style="width: 100%"
        border
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          :show-overflow-tooltip="true"
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('common.vendorName')"
          :show-overflow-tooltip="true"
          width="150"
        />
        <!-- 附件名称 -->
        <el-table-column
          align="center"
          prop="fileName"
          :label="$t('bidMod.fileName')"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :default-file="{
                fileId: scope.row.docId,
                fileName: scope.row.fileName
              }"
              :readonly="true"
            />
          </template>
        </el-table-column>
        <!-- 备注 -->
        <el-table-column
          align="center"
          prop="comments"
          :label="$t('common.remark')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.comments"
              disabled
            />
          </template>
        </el-table-column>
      </el-table>
    </el-collapse-item>
    <!-- 技术评选结论 -->
    <el-collapse-item
      :title="$t('logisticsMod.techSelectionConclusion')"
      name="3"
    >
      <el-form
        ref="form3"
        :model="form"
        :rules="rules"
      >
        <div style="padding-bottom: 10px">
          <el-button
            class="detail-pbtn"
            type="primary"
            :disabled="!isGroup"
            @click="saveSelection"
          >
            {{ $t("common.save") }}
          </el-button>
        </div>
        <el-form-item prop="technoSelection">
          <el-input
            v-model="form.technoSelection"
            type="textarea"
            :rows="2"
            :disabled="!isGroup"
            :placeholder="$t('logisticsMod.msgTechSelConclusion')"
          />
        </el-form-item>
      </el-form>
    </el-collapse-item>
  </el-collapse>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'

export default {
  name: 'BiddingProjectDetailTechBiding',
  components: { QuickSearch, shipTableClumn },
  props: [
    'lgtVendorFiles',
    'allParams',
    'scheduleForm',
    'form',
    'scopeBidingId',
    'isShow',
    'isGroup',
    'tableHeader'
  ],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      rules: {
        technoSelection: [
          {
            required: true,
            message: this.$t('logisticsMod.msgTechSelConclusion')
          }
        ] // 请输入技术评选结论
      }
    }
  },
  computed: {
    transportFlag () {
      // 陆运、铁运
      if (
        ['LAND_TRANSPORT', 'RAILWAY_TRANSPORT'].includes(
          this.allParams.biding.transportModeCode
        )
      ) {
        return true
      } else {
        return false
      }
    }
  },
  watch: {},
  mounted () {
  },
  methods: {
    openTechBidding () {
      this.$http({
        url: '/api-pd/logistics/biding/techOpenBiding',
        method: 'GET',
        params: { bidingId: this.scopeBidingId },
        loading: true
      })
        .then(data => {
          return this.$message.success(this.$t('common.success'))
        })
        .catch(err => {
          console.log(err)
        })
    },
    saveSelection () {
      this.$refs.form3.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/biding/saveTechnoSelection',
            method: 'get',
            params: {
              bidingId: this.scopeBidingId,
              technoSelection: this.form.technoSelection
            },
            loading: true
          })
            .then(res => {
              this.$message.success(res.message)
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
          return false
        }
      })
    }
  }
}
</script>
<style scoped></style>
