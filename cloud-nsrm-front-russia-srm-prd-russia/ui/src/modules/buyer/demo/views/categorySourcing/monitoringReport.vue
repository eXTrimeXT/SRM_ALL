<template>
  <el-container class="toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        :preFormObj="formQueryData"
        :hasErrorMsgConfirm="true"
        @getFormData="getQuerydata"
      />
      <MainHeader :lSpan="24" :rSpan="0" style="background:#f5f5f5;position：relative;">
        <template slot="left">
          <span class="warning-tit">状态预警</span>
          <div class="status-warning">
            <div v-for="(item, index) in warningData" :key="item.type" class="content-item">
              <div class="item-button">
                <div :class="{'button-circle': true, active: item.isActive}" @click="handlerItemButtonClick(index)">
                  <strong>{{ item.total }}</strong><small v-if="item.count">(<span :class="{red: item.isRed}">{{ `${item.count}` }}</span>)</small>
                </div>
                <div v-if="index != warningData.length - 1" class="item-line" />
              </div>
              <div class="button-text" :title="item.text">
                {{ item.text }}
              </div>
            </div>
          </div>
        </template>
      </MainHeader>
      <MainHeader>
        <template slot="left">
          <AuthorityButton type="primary" @click="exportHandle">
            导出
          </AuthorityButton>
          <span>(明细信息：点击上方状态显示对应明细信息)</span>
        </template>
        <template slot="right">
          <el-button @click="goBack">
            返回
          </el-button>
        </template>
      </MainHeader>
      <TableView
        ref="list"
        :table-header="tableHeader"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="false"
        :afterData="afterData"
        :tableInfor="tableInfor"
      />
      <!-- url="/api-base/reduction-report/queryBuyer" -->
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, formatTimeToDate } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
export default {
  name: 'MonitoringReport',
  components: {
    TableView,
    FormWrapper,
    MainHeader
  },
  data () {
    return {
      filterConfig: [
        {
          label: '显示阶段',
          prop: 'type',
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          label: '事业部',
          prop: 'BU'
        },
        {
          label: '引入OU',
          prop: 'OU'
        },
        {
          label: '引入场景',
          prop: 'filde1'
        },
        {
          label: '引入类型',
          prop: 'filde2'
        },
        {
          label: '引入类型',
          prop: 'filde2'
        },
        {
          label: '引入单号',
          prop: 'filde6'
        },
        {
          label: '供应商名称',
          prop: 'filde7'
        },
        {
          label: '采购分类',
          prop: 'filde8'
        },
        {
          label: '责任配套',
          prop: 'filde9'
        },
        {
          label: '创建时间',
          prop: 'filde10',
          type: 'date'
        }
      ],
      tableHeader: [
        {
          label: '事业部',
          prop: 'kz',
          width: 100
        },
        {
          label: '引入OU',
          prop: 'xz',
          width: 100
        },
        {
          label: '引入单号',
          prop: 'buyer',
          width: 100
        },
        {
          label: '供应商',
          prop: 'progressYear',
          width: 100
        },
        {
          label: '采购分类',
          prop: 'baseBomCost',
          width: 150
        },
        {
          label: '引入场景',
          prop: 'filed1',
          width: 160
        },
        {
          label: '引入类型',
          prop: 'filed2'
        },
        {
          label: '责任配套',
          prop: 'filed3'
        },
        {
          label: '品类寻源阶段',
          prop: 'filed4'
        },
        {
          label: '创建时间',
          prop: 'filed5'
        },
        {
          label: '寻源发布',
          prop: 'filed6'
        },
        {
          label: '资质审查',
          prop: 'filed7'
        },
        {
          label: '现场评审',
          prop: 'filed8'
        },
        {
          label: '送样申请',
          prop: 'filed9'
        },
        {
          label: '提供资料',
          prop: 'filed10'
        },
        {
          label: '审核资料',
          prop: 'filed11'
        },
        {
          label: '测试申请',
          prop: 'filed12'
        },
        {
          label: '测试',
          prop: 'filed13'
        },
        {
          label: '样品确认',
          prop: 'filed14'
        },
        {
          label: '物料试用',
          prop: 'filed15'
        },
        {
          label: '供方生效',
          prop: 'filed16'
        },
        {
          label: '总标准周期',
          prop: 'filed17'
        },
        {
          label: '供方引入总耗时',
          prop: 'filed18'
        }
      ],
      pageSize: 15,
      queryParam: {},
      tableData: [],
      exportLock: false,
      testData: [],
      tableInfor: [],
      warningData: []
    }
  },
  async created () {
    this.tableHeader = [...this.tableHeader]

    this.tableInfor = [
      {
        kz: '厨热',
        xz: '203',
        buyer: 'RFP00010020',
        progressYear: '华凌',
        baseBomCost: '原材料',
        ensureRatio: '',
        ensureAmount: '邀请',
        filed1: '11',
        filed2: '11',
        filed3: '11',
        filed4: '11',
        filed5: '11',
        filed6: '11',
        filed7: '11',
        filed8: '11',
        filed9: '11',
        filed10: '11',
        filed11: '11',
        filed12: '11',
        filed13: '11',
        filed14: '11',
        filed15: '11',
        filed16: '11',
        filed17: '11',
        filed18: '11'
      },
      {
        kz: '厨热',
        xz: '203',
        buyer: 'RFP00010020',
        progressYear: '华凌',
        baseBomCost: '原材料',
        ensureRatio: '',
        ensureAmount: '邀请',
        filed1: '11',
        filed2: '11',
        filed3: '11',
        filed4: '11',
        filed5: '11',
        filed6: '11',
        filed7: '11',
        filed8: '11',
        filed9: '11',
        filed10: '11',
        filed11: '11',
        filed12: '11',
        filed13: '11',
        filed14: '11',
        filed15: '11',
        filed16: '11',
        filed17: '11',
        filed18: '11'
      },
      {
        kz: '厨热',
        xz: '203',
        buyer: 'RFP00010020',
        progressYear: '华凌',
        baseBomCost: '原材料',
        ensureRatio: '',
        ensureAmount: '邀请',
        filed1: '11',
        filed2: '11',
        filed3: '11',
        filed4: '11',
        filed5: '11',
        filed6: '11',
        filed7: '11',
        filed8: '11',
        filed9: '11',
        filed10: '11',
        filed11: '11',
        filed12: '11',
        filed13: '11',
        filed14: '11',
        filed15: '11',
        filed16: '11',
        filed17: '11',
        filed18: '11'
      }
    ]

    this.warningData = [
      {
        type: '212',
        isActive: true,
        count: '7',
        isRed: true,
        text: '寻源发布',
        total: '20'
      },
      {
        type: '212',
        isActive: true,
        count: '200',
        isRed: true,
        text: '资质审查',
        total: '212'
      },
      {
        type: '212',
        isActive: false,
        count: '',
        isRed: false,
        text: '现场评审',
        total: '21'
      },
      {
        type: '212',
        isActive: true,
        count: '19',
        isRed: true,
        text: '送样申请',
        total: '19'
      },
      {
        type: '212',
        isActive: true,
        count: '15',
        isRed: true,
        text: '提供样品',
        total: '16'
      },
      {
        type: '212',
        isActive: true,
        count: '1',
        isRed: true,
        text: '提供资料',
        total: '1'
      },
      {
        type: '212',
        isActive: true,
        count: '',
        isRed: false,
        text: '资料审核',
        total: '0'
      },
      {
        type: '212',
        isActive: true,
        count: '',
        isRed: true,
        text: '，测试申请',
        total: '0'
      },
      {
        type: '42',
        isActive: true,
        count: '',
        isRed: true,
        text: '测试',
        total: '0'
      },
      {
        type: '212',
        isActive: false,
        count: '',
        isRed: false,
        text: '样品确认',
        total: '0'
      },
      {
        type: '32',
        isActive: true,
        count: '2',
        isRed: true,
        text: '物料试用',
        total: '2'
      },
      {
        type: '212',
        isActive: true,
        count: '43',
        isRed: true,
        text: '供方生效',
        total: '46'
      }
    ]
  },
  methods: {
    handlerItemButtonClick (currentIndex) {
      this.queryParam.filed4 = this.warningData[currentIndex].type
    },
    getQuerydata (params) {
      this.queryParam = { ...params }
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    async exportHandle () {
      return false
    },
    goBack () {
      history.go(-1)
    }
  }
}
</script>
<style scoped lang='scss'>
.flex-container :deep(.el-table__empty-block) {
  width: 100% !important;
}
.warning-tit{
  position: absolute;
  top: -10px;
  left: -14px;
  font-size: 16px;
  background: #ddd;
  padding: 5px 10px;
}
.status-warning {
  height: 130px;
  display: flex;
  align-items: center;
  justify-content: center;
  .content-item {
      .item-button {
          display: flex;
          .button-circle {
              width: 58px;
              height: 58px;
              background:  #8fbbef;
              color: #fff;
              border: 1px solid #909090;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 12px;
              cursor: pointer;
              &:hover {
                  box-shadow: 0px 0px 5px #888888;
              }
              .red{
                  color: #f70d0d;
              }
          }
          .button-circle.active{
              /*background-color: #aaa;*/
              background-color: #318bf5;
              color: #fff;
          }
          .item-line {
              width: 29px;
              height: 2px;
              background: #bbbaba;
              margin-top: 32px;
              text-align: center;
          }
      }
      .button-text {
          padding-top: 12px;
          font-size: 12px;
          width: 66px;
          white-space: nowrap;
          overflow: hidden;
          word-break: break-all;
          text-align: center;
      }
  }
}
</style>
