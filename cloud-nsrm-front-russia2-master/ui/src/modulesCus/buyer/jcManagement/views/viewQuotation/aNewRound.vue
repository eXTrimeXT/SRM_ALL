<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <el-form ref="relForm" :model="formData">
        <el-row :gutter="32">
          <el-col :span="8">
            <!-- '新一轮报价开始时间' -->
            <el-form-item :label="$t('bidMod.newQuoteStartTime')">
              <el-date-picker
                type="datetime"
                placeholder="请选择"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                auto-complete="off"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- '新一轮报价结束日期' -->
            <el-form-item :label="$t('cusEntry.supplement20250205.newRoundQuoteEndDate')">
              <!-- 请选择 -->
              <el-date-picker
                type="datetime"
                :placeholder="$t('components.approvalHead.headers.selectNode')"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                auto-complete="off"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div style="margin:10px 0">
        <el-button
          type="primary"
        >
          <!-- 新增 -->
          {{ $t("common.add") }}
        </el-button>
      </div>
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
        <!-- '联系人' -->
        <el-table-column
          align="center"
          prop="userCode"
          :label="$t('vendorMod.contactPerson')"
        />
        <!-- '电话' -->
        <el-table-column
          align="center"
          prop="userCode"
          :label="$t('common.phone')"
        />
        <!-- '邮箱' -->
        <el-table-column
          align="center"
          prop="userCode"
          :label="$t('common.email')"
        />
        <!-- '来源' -->
        <el-table-column
          align="center"
          prop="userCode"
          :label="$t('cusEntry.inq.sourceFromType')"
        />
        <!-- '操作' -->
        <el-table-column
          align="center"
          prop="userCode"
          :label="$t('components.headers.operation')"
          fixed="right"
          width="120"
        >
          <template slot-scope="scope">
            <AuthorityButton
              type="primary"
            >
              <!-- 中标{{ scope }} -->
              {{ $t("bid_mod.winTheBidding") }}{{ scope }}
            </AuthorityButton>
          </template>
        </el-table-column>
      </el-table>
      <CToolbar>
        <template slot="right">
          <el-button @click="handleCancel">
            <!-- 取消 -->
            {{ $t("components.common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="handleCancel"
          >
            <!-- 确认 -->
            {{ $t("components.common.confirm") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import { validEmail, validatePhone } from '@/utils/validate'
export default {
  name: 'QuotationDetail',
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
      tableHeader: []
    }
  },
  created () {
  },
  methods: {
    handleCancel () {
      this.$emit('tab-remove', this.$attrs.tabName)
    },
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
