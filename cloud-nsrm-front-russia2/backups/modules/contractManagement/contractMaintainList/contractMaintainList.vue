<template>
  <el-container
    class="flex-container the_contractTemplateList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template v-if="curRole === 'BUYER'" slot="left">
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-cm/contract/contractHead/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import contractMaintainDetail from './contractMaintainDetail'
import contractMaintainDetailRead from './contractMaintainDetailRead'
import contractMaintainDetailVendor from './contractMaintainDetailVendor'
import LaunchPaymentRequest from './launchPaymentRequest'
import { getDictItem } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'ContractMaintainList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    contractMaintainDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      curRole: this.$store.getters.userType,
      name: 'contractTemplateTable',
      tableName: 'contractTemplateTable',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        { prop: 'contractNo', label: () => this.$t('contractMod.contractNo') },
        {
          prop: 'mainContractNo',
          label: () => this.$t('contractMod.contractHeadId')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('contractMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        {
          prop: 'contractName',
          label: () => this.$t('contractMod.contractName')
        },
        {
          prop: 'templateType',
          label: () => this.$t('contractMod.templateType'),
          type: 'dict',
          code: 'TEMPLATE_TYPE'
        },
        {
          prop: 'status',
          label: () => this.$t('contractMod.status'),
          type: 'dict',
          code: 'CONTRACT_STATUS'
        },
        {
          prop: 'contractType',
          label: () => this.$t('contractMod.contractType'),
          type: 'dict',
          code: 'CONTRACT_TYPE'
        },
        {
          prop: 'creationDate',
          label: () => this.$t('contractMod.concreationDate'),
          type: 'date'
        }
      ],
      queryParam: {},
      templTypeList: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'contractType',
        label: () => this.$t('contractMod.contractType'),
        width: 100,
        dataType: 'dict',
        code: 'CONTRACT_TYPE'
      },
      {
        prop: 'templName',
        label: () => this.$t('contractMod.templHeadId'),
        width: 120
      },
      {
        prop: 'contractNo',
        label: () => this.$t('contractMod.contractNo'),
        width: 170
      },
      {
        prop: 'contractName',
        label: () => this.$t('contractMod.contractName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'mainContractNo',
        label: () => this.$t('contractMod.contractHeadId'),
        width: 150
      },
      {
        prop: 'vendorName',
        label: () => this.$t('contractMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'contractStatus',
        label: () => this.$t('contractMod.contractStatus'),
        width: 100,
        dataType: 'dict',
        code: 'CONTRACT_STATUS'
      },
      {
        prop: 'startDate',
        label: () => this.$t('contractMod.constartDate'),
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('contractMod.createdBy'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('contractMod.creationDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('contractMod.lastUpdatedBy'),
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('contractMod.lastUpdateDate'),
        width: 150,
        formattor (val) {
          return val ? parseTime(val) : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: (row) => this.launchPaymentRequest(row),
            show: (row) => row.contractStatus === 'ARCHIVED',
            formattor: () => this.$t('contractMod.initiatePaymentRequest')
          },
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            formattor (val) {
              return this.$t('contractMod.manage')
            },
            show: function (row) {
              if (
                (_this.curRole === 'BUYER' &&
                  (row.contractStatus === 'DRAFT' ||
                    row.contractStatus === 'REJECTED' ||
                    row.contractStatus === 'REFUSED')) ||
                (_this.curRole === 'VENDOR' && row.contractStatus === 'SUPPLIER_CONFIRMING')
              ) {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.publishOne(row)
            }.bind(this),
            formattor (val) {
              return this.$t('common.publish')
            },
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.contractStatus === 'UNPUBLISHED') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.approvalOne(row)
            }.bind(this),
            formattor (val) {
              return this.$t('contractMod.approved')
            },
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.contractStatus === 'UNDER_REVIEW') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.approvalOneNot(row)
            }.bind(this),
            formattor (val) {
              return this.$t('bidMod.approvalRefuse')
            },
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.contractStatus === 'UNDER_REVIEW') {
                return true
              } else {
                return false
              }
            }
          },
          {
            callback: function (row) {
              this.archiveBill(row)
            }.bind(this),
            formattor (val) {
              return this.$t('contractMod.archive') // [供应商已确认]
            },
            show: function (row) {
              if (_this.curRole === 'BUYER' && row.contractStatus === 'SUPPLIER_CONFIRMED') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    // 发布范围
    // 模板类型
    getDictItem('TEMPLATE_TYPE').then((res) => {
      this.templTypeList = adaptDictData(res.data, 'dict')
    })
    this.getTemplTypeList()

    this.$nextTick(() => {
      this.getQuerydata()
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
    getTemplTypeList () {
      this.$http({
        url: '/api-cm/template/templHead/listPageByParm',
        method: 'POST',
        data: {},
        loading: true
      })
        .then((data) => {
          if (data.data && data.data.list) {
            this.templTypeList = data.data.list.map((v) => {
              return {
                value: v['templHeadId'],
                label: v['templName']
              }
            })
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addOne () {
      this.$emit('tab-add', {
        component: contractMaintainDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('contractMod.createContract'),
        name: 'contractMaintainDetail'
      })
    },
    launchPaymentRequest (row) {
      this.$emit('tab-add', {
        component: LaunchPaymentRequest,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.contractName,
        name: 'contractMaintainDetail' + row.contractName
      })
    },
    editOne (row) {
      if (this.curRole === 'BUYER') {
        this.$emit('tab-add', {
          component: contractMaintainDetail,
          params: {
            flag: 'edit',
            row: row
          },
          title: row.contractName,
          name: 'contractMaintainDetail' + row.contractName
        })
      } else if (this.curRole === 'VENDOR') {
        this.$emit('tab-add', {
          component: contractMaintainDetailVendor,
          params: {
            flag: 'edit',
            row: row
          },
          title: row.contractName,
          name: 'contractMaintainDetailVendor' + row.contractName
        })
      }
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: contractMaintainDetailRead,
        params: {
          flag: 'readOnly',
          row: row
        },
        title: row.contractName,
        name: 'contractMaintainDetailRead' + row.contractName
      })
    },
    archiveBill (row) {
      // archive
      this.$http({
        url: '/api-cm/contract/contractHead/buyerArchive',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    enableOne () {},
    publishOne (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/buyerPublish',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('common.publishedSuccessfully'))
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    approvalOne (row) {
      this.$http({
        url: '/api-cm/contract/contractHead/buyerApprove',
        method: 'GET',
        params: { contractHeadId: row.contractHeadId },
        loading: true
      })
        .then((data) => {
          this.$message.success(this.$t('vendorMod.approvalSuccess'))
          this.getQuerydata()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    approvalOneNot (row) {
      this.$prompt(this.$t('contractMod.msgRefuseReason'), this.$t('contractMod.refusedReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-cm/contract/contractHead/buyerRefused',
            method: 'POST',
            data: {
              contractHeadId: row.contractHeadId,
              approvalAdvice: value
            },
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('contractMod.operateSuccessfully'))
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    deleteOne (row) {
      this.$confirm(this.$t('contractMod.sureDeleteDate'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-cm/contract/contractHead/buyerDelete',
            method: 'GET',
            params: { contractHeadId: row.contractHeadId },
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
