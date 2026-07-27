<template>
  <el-container
    class="companystateEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <srm-row>
            <srm-col :initCol="4">
              <el-form-item
                prop="companyStateCode"
                label="供应商状态编码"
              >
                <el-input v-model="form.companyStateCode" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="stratTime"
                label="生效时间"
              >
                <el-input v-model="form.stratTime" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="endTime"
                label="失效时间"
              >
                <el-input v-model="form.endTime" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowInquiry"
                label="是否允许询价"
              >
                <el-input v-model="form.ifAllowInquiry" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowBid"
                label="是否允许招标"
              >
                <el-input v-model="form.ifAllowBid" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowOrder"
                label="是否允许下单"
              >
                <el-input v-model="form.ifAllowOrder" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowWarehousing"
                label="是否允许入库"
              >
                <el-input v-model="form.ifAllowWarehousing" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowStatement"
                label="是否允许对账"
              >
                <el-input v-model="form.ifAllowStatement" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="ifAllowPay"
                label="是否允许付款"
              >
                <el-input v-model="form.ifAllowPay" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="transitDay"
                label="业务过渡期（天）"
              >
                <el-input v-model="form.transitDay" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="orderCount"
                label="采购订单次数（次）"
              >
                <el-input v-model="form.orderCount" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="amountLimitPerOrder"
                label="单笔采购金额限额（元）"
              >
                <el-input v-model="form.amountLimitPerOrder" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="remark"
                label="备注说明"
              >
                <el-input v-model="form.remark" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="createdBy"
                label="创建人"
              >
                <el-input
                  v-model="form.createdBy"
                  disabled="disabled"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="creationDate"
                label="创建时间"
              >
                <el-input
                  v-model="form.creationDate"
                  disabled="disabled"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="lastUpdatedBy"
                label="最后更新人"
              >
                <el-input
                  v-model="form.lastUpdatedBy"
                  disabled="disabled"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <c-toolbar>
        <template #right>
          <el-button

            @click="cancelBill"
          >
            取消
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"

            @click="save"
          >
            确认
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

export default {
  name: 'CompanystateEdit',
  components: {
    MainHeader,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      form: {
        companyStateCode: null,
        stratTime: null,
        endTime: null,
        ifAllowInquiry: null,
        ifAllowBid: null,
        ifAllowOrder: null,
        ifAllowWarehousing: null,
        ifAllowStatement: null,
        ifAllowPay: null,
        transitDay: null,
        orderCount: null,
        amountLimitPerOrder: null,
        remark: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      rules: {},
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
      this.form = row
    }
  },
  methods: {
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { companyStateId, ...rest } = this.form
          if (flag === 'add') {
            this.$api.generate.companystate.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            this.$api.generate.companystate.update(this.form).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        } else {
          this.__focus_eror__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'companystateEdit')
      } else {
        this.$emit('tab-remove', 'companystateEdit' + row.companyStateId)
      }
      this.__setTabTodo('companystateList.getQuerydata')
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
.companystateEdit {
  height: 100%;
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
