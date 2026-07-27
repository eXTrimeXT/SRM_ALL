<template>
  <el-container
    class="the-materialSourceOrdersDetail-detail"
    direction="vertical"
  >
    <el-main>
      <main-header>
        <template
          slot="left"
        >
          <h2>物料寻源需求详情 {{ parambiddNum }}</h2>
        </template>
      </main-header>
      <div class="form-container">
        <el-steps
          :active="1"
          style="width:55%"
        >
          <el-step title="已发布" />
          <el-step title="已报名" />
        </el-steps>
      </div>

      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          title="基本信息"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-row type="flex">
              <el-col>
                <el-form-item
                  label="物料编码"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.billNum" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  label="物料名称"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.billDate" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  label="采购组织"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.deliveryAddress" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  label="采购品类代码"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.billAmount" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  label="采购品类名称"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.currencyType" />
                </el-form-item>
              </el-col>
              <el-col />
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  label="备注"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item
          title="物料寻源需求附件"
          name="2"
        >
          <el-button
            type="primary"
            size="mini"
            style="margin-left: 22px;"
            @click="addOne"
          >
            添加附件
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
              prop="fileType"
              label="附件类型"
              width="150"
            >
              <template slot-scope="scope">
                <el-select v-model="scope.row.fileType" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="itemName"
              label="附件上传（限制大小为XXM)"
              width="200"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.itemName" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="fileTemplate"
              label="模板"
              width="200"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.fileTemplate" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="uploadDate"
              label="上传日期"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.uploadDate"
                  type="date"
                  placeholder="日期选择"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="60"
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
      <c-toolbar>
        <template slot="right">
          <el-button
            type="primary"
            size="mini"
            @click="prevOne"
          >
            前一单
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="nextOne"
          >
            后一单
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="saveOne"
          >
            保存
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="submitOne"
          >
            报名
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="backTo"
          >
            返回
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'

export default {
  name: 'MaterialSourceOrdersDetail',
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
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      tableData: [{}],
      isModify: false,
      activeDims: ['1', '2']
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      // console.log(111)
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
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    prevOne () {},
    nextOne () {},
    saveOne () {},
    submitOne () {},
    backTo () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'materialSourceOrdersDetail' + this.$attrs.params.row.biddingNum
        )
      } else {
        this.$emit('tab-remove', 'materialSourceOrdersDetail')
      }
      // this.__setTabTodo('inquiryOrdersList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-materialSourceOrdersDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
