<template>
  <el-container
    class="the-quotationPricesDetail-detail"
    direction="vertical"
  >
    <el-main>
      <MainHeader>
        <template slot="left">
          <h2>报价详情 {{ parambiddNum }}</h2>
        </template>
      </MainHeader>

      <div class="form-container">
        <el-steps :active="2">
          <el-step title="待报价" />
          <el-step title="已审核" />
          <el-step title="询价结果已出" />
        </el-steps>
      </div>

      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          title="报价信息"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  label="询价单号"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.billNum" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  label="采购组织"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form.purOrg" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  label="业务实体"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form.organisation" />
                </el-form-item>
              </srm-col>
            </srm-row>
            <srm-row>
              <srm-col>
                <el-form-item
                  label="询价方式"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form.billAmount" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  label="报价方式"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form.currencyType" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  label="报价轮次"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form.vendorCode" />
                </el-form-item>
              </srm-col>
            </srm-row>

            <srm-row>
              <srm-col>
                <el-form-item
                  label="报价截止日期"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="form.deadline"
                    type="date"
                    placeholder="日期选择"
                    format="yyyy-MM-dd"
                    value-format="timestamp"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="价格生效日期"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="form.enableDate"
                    type="date"
                    placeholder="日期选择"
                    format="yyyy-MM-dd"
                    value-format="timestamp"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="发布日期"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="form.publishDate"
                    type="date"
                    placeholder="日期选择"
                    format="yyyy-MM-dd"
                    value-format="timestamp"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>

            <srm-row>
              <srm-col>
                <el-form-item
                  label="备注"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item
          title="商务信息"
          name="2"
        >
          <el-form
            ref="form2"
            :model="form2"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  label="结算币别"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.currency" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="付款条件"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.billDate" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="联系人"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form2.concatMan" />
                </el-form-item>
              </srm-col>
            </srm-row>

            <srm-row>
              <srm-col>
                <el-form-item
                  label="本位币"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.billAmount" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="结算方式"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.currencyType" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="联系电话"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form2.telephone"
                    type="number"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>

            <srm-row>
              <srm-col>
                <el-form-item
                  label="汇率类型"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.poLevel" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="汇率"
                  :label-width="formLabelWidth"
                >
                  <el-select v-model="form2.purchasor" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <el-form-item
                  label="邮箱"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form2.email" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item
          title="物料信息"
          name="3"
        >
          <el-button
            type="primary"
            style="margin-left: 22px;"
            @click="addOne"
          >
            添加
          </el-button>

          <el-table
            :data="tableData"
            style="width: 100%"
            border
            height="222px"
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <el-table-column
              align="center"
              prop="itemCode"
              label="物料编号"
              width="150"
            >
              <template slot-scope="scope">
                <el-select v-model="scope.row.itemCode" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="itemName"
              label="物料名称"
              width="150"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.itemName" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="status"
              label="状态"
              width="150"
            >
              <template slot-scope="scope">
                <el-select v-model="scope.row.status" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="unit"
              label="单位"
              width="150"
            >
              <template slot-scope="scope">
                <el-select v-model="scope.row.unit" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="billAmount"
              label="需求数量"
              width="150"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.billAmount"
                  type="number"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="price1"
              label="未税单价"
              width="150"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.price1" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="price2"
              label="含税单价"
              width="150"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.price2" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="price3"
              label="税率（%）"
              width="150"
            >
              <template slot-scope="scope">
                <el-select v-model="scope.row.price3" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="isLadder"
              label="是否阶梯报价"
              width="100"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isLadder"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="isLadder"
              label="阶梯报价"
              width="100"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isLadder"
                  true-label="Y"
                  false-label="N"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="deadline"
              label="报价截止时间"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.deadline"
                  type="date"
                  placeholder="日期选择"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="biddingTime"
              label="报价次数"
              width="150"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.biddingTime"
                  type="number"
                />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="inquiryOrderNum"
              label="询价单号"
              width="150"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.inquiryOrderNum" />
              </template>
            </el-table-column>

            <el-table-column
              align="center"
              prop="createdUserName"
              label="创建人"
              width="150"
            />
            <el-table-column
              label="操作"
              width="60"
              fixed="right"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="handleDelClick(scope.$index, scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <el-button
            type="primary"
            @click="prevOne"
          >
            前一单
          </el-button>
          <el-button
            type="primary"
            @click="nextOne"
          >
            后一单
          </el-button>
          <el-button
            type="primary"
            @click="saveOne"
          >
            保存
          </el-button>
          <el-button
            type="primary"
            @click="submitOne"
          >
            提交
          </el-button>
          <el-button
            type="primary"
            @click="backTo"
          >
            返回
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'

export default {
  name: 'QuotationPricesDetail',
  components: {
    MainHeader,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      parambiddNum: '',
      form: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      },
      form2: {
        billNum: '',
        billDate: '',
        deliveryAddress: '',
        affordType: '',
        specification: '',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      },
      isDisabled: this.$attrs.params.flag === 'edit',
      formLabelWidth: '120px',
      tableData: [{}],
      isModify: false,
      activeDims: ['1', '2', '3']
    }
  },
  created () {
    if (this.$attrs.params.flag === 'edit') {
      this.parambiddNum = this.$attrs.params.row.biddingNum
    }
  },
  methods: {
    addOne () {
      this.tableData.push({
        price1: '11',
        price2: '22',
        itemCode: 'c1',
        itemName: 'n1',
        specification: 's1',
        brand: '',
        demandDate: '',
        replyDate: '',
        remark: '',
        adjustReason: ''
      })
    },
    // 行删除
    handleDelClick (index, row) {
      if (row.attachFileId) {
        // debugger
      } else {
        this.tableData.splice(index, 1)
      }
    },
    editOne () {},
    confirmBill () {},
    cancelBill () {},
    reset () {
      // 重置所有过滤条件
      for (const i in this.form) {
        this.form[i] = ''
      }
    },
    prevOne () {},
    nextOne () {},
    saveOne () {},
    submitOne () {},
    backTo () {
      if (this.$attrs.params.flag === 'edit') {
        this.$emit(
          'tab-remove',
          'quotationPricesDetail' + this.$attrs.params.row.biddingNum
        )
      } else {
        this.$emit('tab-remove', 'quotationPricesDetail')
      }
    }
  }
}
</script>

<style scoped lang="scss">
.the-quotationPricesDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
