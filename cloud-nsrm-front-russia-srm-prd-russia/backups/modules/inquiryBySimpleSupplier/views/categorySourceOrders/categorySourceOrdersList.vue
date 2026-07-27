<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            size="mini"
            @click="exportOne"
          >
            报名
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="exportOne"
          >
            导出
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/pss/member/dict/list"
        url-for-count="/isales-main/mstQuicksearchConfig/queryCount"
      />
    </el-main>
  </el-container>
</template>
<script>
  import TableView from 'lib@/components/Table/TableView'
  import MainHeader from 'lib@/components/Table/MainHeader'
  import FormWrapper from 'lib@/components/Table/FormWrapper'
  import categorySourceOrdersDetail from './categorySourceOrdersDetail'
  import { parseTime } from '@/utils'

  export default {
    name: 'CategorySourceOrdersList',
    components: {
      TableView, MainHeader, FormWrapper
    },
    data () {
      return {
        name: '',
        reviewFormNumber: '',
        gridData: [],
        pageSize: 15,
        gridId: 'list',
        selectList: [],
        currentRow: null,
        showFilterBar: 1,
        tableHeader: [],
        tableData: [],
        statusList: [],
        preArr: [
          { prop: 'inquiryOrderNum',
            label: '寻源单号'
          }, { prop: 'itemCode',
            label: '品类'
          }, { prop: 'category',
            label: '寻源状态',
type: 'select',
            options: []
          }, { prop: 'purOrg',
            label: '采购组织',
type: 'select',
            options: []
          }
        ],
        queryParam: {}
      }
    },
    created () {
      this.tableHeader = [
        { prop: 'biddingNum',
          label: '寻源单号',
width: 120,
          showType: 'button',
          btnStyle: 'text',
          callback: function (row) {
            this.currentRow = row
            this.editTab()
          }.bind(this),
          formattor (val) {
            return val || '--'
          }
        }, { prop: 'purType',
          label: '采购品类',
width: 100
        }, { prop: 'purTypeName',
          label: '采购品类名称',
width: 100
        }, { prop: 'ioStatus',
          label: '寻源状态',
width: 100
        }, { prop: 'purOrg',
          label: '事业部（采购组织）',
width: 200
        }, { prop: 'entityId',
          label: '业务实体',
width: 100
        }, { prop: 'publishDate',
          label: '发布时间',
width: 100
        }, { prop: 'remark',
          label: '备注',
width: 100
        }
      ]

      this.$nextTick(() => {
        // this.getQuerydata()
        let listdata = []
        for (let i = 1; i < 5; i++) {
          listdata.push({
            biddingNum: 'MTXY00' + i,
            ioStatus: '待引入',
            itemCode: 'MAT00' + i,
            itemName: '物料00' + i,
            purOrg: 'OOUU_冰箱事业部_顺德工厂' + i,
            biddingEndDate: '2020-3-' + i,
            publishDate: '2020-8-' + i
          })
        }
        this.$refs[this.gridId].tableData = listdata
      })
    },
    methods: {
      getQuerydata (v) {
        this.queryParam = v
        this.$nextTick(() => {
          this.$refs[this.gridId].query()
        })
      },
      handleCurrentChange (val) {
        this.currentRow = val
      },
      exportOne () {},
      editTab () {
        // 编辑tab
        let tab = {
          component: categorySourceOrdersDetail,
          params: {
            flag: 'edit',
            row: this.currentRow
          },
          title: this.currentRow.biddingNum,
          name: 'categorySourceOrdersDetail' + this.currentRow.biddingNum
        }
        this.$emit('tab-add', tab)
      }
    }
  }
</script>
<style scoped lang="scss">
.the_quotationPrices_wrapper{

}
</style>
