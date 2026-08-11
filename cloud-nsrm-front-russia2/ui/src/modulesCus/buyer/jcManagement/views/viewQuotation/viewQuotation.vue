<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <div class="stepDiv">
        <el-steps
          :active="curStatus"
          :align-center="true"
          finish-status="success"
        >
        <!-- '发布' -->
          <el-step :title="$t('common.publish')" />
          <!-- '报价开始' -->
          <el-step :title="$t('bidMod.inQstatus3')" description="2023-11-30 12:00:00" />
          <!-- '报价截止' -->
          <el-step :title="$t('bidMod.inQstatus4')" description="2023-12-31 12:00:00" />
          <!-- '评选中' -->
          <el-step :title="$t('bidMod.pingxuan')" />
          <!-- '询价结束' -->
          <el-step :title="$t('bidMod.inQstatus6')" />
        </el-steps>
      </div>
      <div class="timeBox">
        <!-- 距离本轮报价截止还剩余：<span class="timeNum">已截止</span> -->
        {{ $t("bidMod.curQuoteDeadline") }}<span class="timeNum">{{ $t("bidMod.competitionLts.expired") }}</span>
      </div>
      <el-form ref="relForm" :model="formData">
        <el-row :gutter="32">
          <el-col :span="6">
            <!-- '询价单号' -->
            <el-form-item :label="$t('bidMod.inquiryNo')">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- '询价单状态' -->
            <el-form-item :label="$t('bidMod.inQstatus')">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- '发起人' -->
            <el-form-item :label="$t('vendorMod.createdBy')">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- '采购人' -->
            <el-form-item :label="$t('cusEntry.bidMod.purchaser')">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- '采购部门' -->
            <el-form-item :label="$t('oneStopShopping.department')">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- '审核状态' -->
            <el-form-item :label="$t('vendorMod.approvalStatus')">
              <el-input readonly />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-collapse v-model="activeList" class="tab-form-style">
        <!-- '报价跟踪' -->
        <el-collapse-item name="1" :title="$t('bidMod.quoteTrack')">
          <div>
            <!-- <span style="margin-right:10px">轮次</span> -->
            <span style="margin-right:10px">{{ $t("bidMod.bidingRound") }}</span>
            <!-- 请选择 -->
            <el-select :placeholder="$t('components.approvalHead.headers.selectNode')">
              <el-option
                :label="1"
                :value="1"
              />
            </el-select>
          </div>
          <!-- <span class="tip">附件在未截止报价之前禁止下载</span> -->
          <span class="tip">{{ $t("cusEntry.supplement20250205.attachmentNotAllowedToDownloadBeforeDeadline") }}</span>
          <el-table
            ref="selectUserList"
            :data="selectUserList"
            border
            max-height="500px"
          >
          <!-- '供应商编码' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('common.vendorCode')"
            />
            <!-- '供应商名称' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('common.companyName')"
            />
            <!-- '报价状态' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bid_mod.quoteStatus')"
            />
            <!-- '报价人' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.quoteMan')"
            />
            <!-- '报价时间' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.quotedTime')"
            />
            <!-- '供应商IP' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.vendorIp')"
            />
            <!-- '附件查看' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.supplement20250205.attachmentView')"
              fixed="right"
              width="120"
            >
              <template slot-scope="scope">
                <AuthorityButton
                  type="primary"
                >
                  <!-- 下载{{ scope }} -->
                  {{ $t("components.eio.headers.attachmentId") }}{{ scope }}
                </AuthorityButton>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- '评选跟踪' -->
        <el-collapse-item name="1" :title="$t('cusEntry.supplement20250205.selectionTracking')">
          <div style="margin-bottom:10px">
            <span style="margin-right:10px">
              <!-- 区域 -->
              {{ $t("vendorMod.area1") }}
            </span>
            <!-- 请选择 -->
            <el-select style="margin-right:10px" :placeholder="$t('components.approvalHead.headers.selectNode')">
              <el-option
                :label="1"
                :value="1"
              />
            </el-select>
            <AuthorityButton
              type="primary"
            >
              <!-- 发起新一轮 -->
              {{ $t("bidMod.biddingControl.startNewRound") }}
            </AuthorityButton>
            <AuthorityButton
              type="primary"
            >
              <!-- 提交 -->
              {{ $t("common.submit") }}
            </AuthorityButton>
            <AuthorityButton
              type="primary"
            >
              <!-- 询价结束 -->
              {{ $t("bidMod.inQstatus6") }}
            </AuthorityButton>
          </div>
          <el-table
            ref="selectUserList"
            :data="selectUserList"
            border
            max-height="500px"
          >
          <!-- '轮次' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.bidingRound')"
              width="120"
            />
            <!-- '供货范围' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.centralizedPurchase.supplyScope')"
              width="120"
            />
            <!-- '物资编码' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.inq.materialCode')"
              width="120"
            />
            <!-- '物资名称' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.competition.materialName')"
              width="120"
            />
            <!-- '物料分类' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.categoryName')"
              width="120"
            />
            <!-- '规格型号' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('vendorMod.specification')"
              width="120"
            />
            <!-- '品牌' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('dataConfMod.band')"
              width="120"
            />
            <!-- '计量单位' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.competition.measurementUnit')"
              width="120"
            />
            <!-- '数量' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bid_mod.quantity')"
              width="120"
            />
            <!-- '备注' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('components.eio.headers.remark')"
              width="120"
            />
            <!-- '历史未税价格' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.supplement20250205.historicalTaxExcludedPrice')"
              width="120"
            />
            <!-- '历史供应商' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.supplement20250205.historySupplier')"
              width="120"
            />
            <!-- '报价次数' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('cusEntry.inq.quoteCount')"
              width="120"
            />
            <!-- '中标供应商' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bidMod.wonBidVendor')"
              width="120"
            />
            <!-- '税率' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('components.ocr.commodityTaxRate')"
              width="120"
            />
            <!-- '未税单价' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('bid_mod.untaxedPrice')"
              width="120"
            />
            <!-- '未税总价' -->
            <el-table-column
              align="center"
              prop="userCode"
              :label="$t('competition.orderNotaxTotalPrice')"
              width="120"
            />
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>

<script>
import quotationDetails from './quotationDetails'
import aNewRound from './aNewRound'
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import { validEmail, validatePhone } from '@/utils/validate'
export default {
  name: 'ViewQuotationOrder',
  components: {
    BaseForm,
    pictureCard,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      curStatus: 3,
      activeList: ['1', '2', '3', '4'],
      form: {
        status: 'DRAFT'
      },
      formData: {

      },
      selectUserList: [],
      tableHeader: [
        { value: '', label: this.$t('bidMod.bidingRound') },  // '轮次'
        { value: '', label: this.$t('cusEntry.centralizedPurchase.supplyScope') },  // '供货范围'
        { value: '', label: this.$t('cusEntry.inq.materialCode') },  // '物资编码'
        { value: '', label: this.$t('cusEntry.competition.materialName') },  // '物资名称'
        { value: '', label: this.$t('bidMod.categoryName') },  // '物料分类'
        { value: '', label: this.$t('vendorMod.specification')},  // '规格型号' 
        { value: '', label: this.$t('dataConfMod.band') }, // '品牌'
        { value: '', label: this.$t('cusEntry.competition.measurementUnit') },  // '计量单位'
        { value: '', label: this.$t('bid_mod.quantity') },  // '数量'
        { value: '', label: this.$t('components.eio.headers.remark') },  // '备注'
        { value: '', label: this.$t('cusEntry.supplement20250205.historicalTaxExcludedPrice') },  // '历史未税价格'
        { value: '', label: this.$t('cusEntry.supplement20250205.historySupplier') },  // '历史供应商'
        { value: '', label: this.$t('cusEntry.inq.quoteCount') },  // '报价次数'
        { value: '', label: this.$t('bidMod.wonBidVendor') },   // '中标供应商'
        { value: '', label: this.$t('components.ocr.commodityTaxRate') },  // '税率'
        { value: '', label: this.$t('bid_mod.untaxedPrice') },  // '未税单价'
        { value: '', label: this.$t('competition.orderNotaxTotalPrice') }  // '未税总价'
      ]
    }
  },
  created () {
    this.$emit('tab-add', {
      component: quotationDetails,
      params: {
        flag: 'add',
        tabName: 'quotationDetails'
      },
      title: '报价信息',
      name: 'quotationDetails'
    })
    this.$emit('tab-add', {
      component: aNewRound,
      params: {
        flag: 'add',
        tabName: 'aNewRound'
      },
      title: '发起新一轮报价',
      name: 'aNewRound'
    })
  },
  methods: {
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
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
