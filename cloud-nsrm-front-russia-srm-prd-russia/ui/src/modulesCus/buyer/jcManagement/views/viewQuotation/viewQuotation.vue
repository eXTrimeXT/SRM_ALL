<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <div class="stepDiv">
        <el-steps
          :active="curStatus"
          :align-center="true"
          finish-status="success"
        >
          <el-step :title="'发布'" />
          <el-step :title="'报价开始'" description="2023-11-30 12:00:00" />
          <el-step :title="'报价截止'" description="2023-12-31 12:00:00" />
          <el-step :title="'评选中'" />
          <el-step :title="'询价结束'" />
        </el-steps>
      </div>
      <div class="timeBox">
        距离本轮报价截止还剩余：<span class="timeNum">已截止</span>
      </div>
      <el-form ref="relForm" :model="formData">
        <el-row :gutter="32">
          <el-col :span="6">
            <el-form-item :label="'询价单号'">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="'询价单状态'">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="'发起人'">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="'采购人'">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="'采购部门'">
              <el-input readonly />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="'审核状态'">
              <el-input readonly />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-collapse v-model="activeList" class="tab-form-style">
        <el-collapse-item name="1" :title="'报价跟踪'">
          <div>
            <span style="margin-right:10px">轮次</span>
            <el-select placeholder="请选择">
              <el-option
                :label="1"
                :value="1"
              />
            </el-select>
          </div>
          <span class="tip">附件在未截止报价之前禁止下载</span>
          <el-table
            ref="selectUserList"
            :data="selectUserList"
            border
            max-height="500px"
          >
            <el-table-column
              align="center"
              prop="userCode"
              :label="'供应商编码'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'供应商名称'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'报价状态'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'报价人'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'报价时间'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'供应商IP'"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'附件查看'"
              fixed="right"
              width="120"
            >
              <template slot-scope="scope">
                <AuthorityButton
                  type="primary"
                >
                  下载{{ scope }}
                </AuthorityButton>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item name="1" :title="'评选跟踪'">
          <div style="margin-bottom:10px">
            <span style="margin-right:10px">区域</span>
            <el-select style="margin-right:10px" placeholder="请选择">
              <el-option
                :label="1"
                :value="1"
              />
            </el-select>
            <AuthorityButton
              type="primary"
            >
              发起新一轮
            </AuthorityButton>
            <AuthorityButton
              type="primary"
            >
              提交
            </AuthorityButton>
            <AuthorityButton
              type="primary"
            >
              询价结束
            </AuthorityButton>
          </div>
          <el-table
            ref="selectUserList"
            :data="selectUserList"
            border
            max-height="500px"
          >
            <el-table-column
              align="center"
              prop="userCode"
              :label="'轮次'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'供货范围'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'物资编码'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'物资名称'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'物料分类'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'规格型号'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'品牌'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'计量单位'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'数量'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'备注'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'历史未税价格'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'历史供应商'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'报价次数'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'中标供应商'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'税率'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'未税单价'"
              width="120"
            />
            <el-table-column
              align="center"
              prop="userCode"
              :label="'未税总价'"
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
        { value: '', label: '轮次' },
        { value: '', label: '供货范围' },
        { value: '', label: '物资编码' },
        { value: '', label: '物资名称' },
        { value: '', label: '物料分类' },
        { value: '', label: '规格型号' },
        { value: '', label: '品牌' },
        { value: '', label: '计量单位' },
        { value: '', label: '数量' },
        { value: '', label: '备注' },
        { value: '', label: '历史未税价格' },
        { value: '', label: '历史供应商' },
        { value: '', label: '报价次数' },
        { value: '', label: '中标供应商' },
        { value: '', label: '税率' },
        { value: '', label: '未税单价' },
        { value: '', label: '未税总价' }
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
