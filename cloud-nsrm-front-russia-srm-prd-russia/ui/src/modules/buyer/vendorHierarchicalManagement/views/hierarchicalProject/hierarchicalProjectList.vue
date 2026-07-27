<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <el-button
            size="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :open-custom-table="true"
        url="/api-pef/perf/vendorlevelhead/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import hierarchicalProjectDeatil from './hierarchicalProjectDeatil'

export default {
  name: 'HierarchicalProjectList',
  components: {
    FormWrapper,
    TableView,
    MainHeader
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'hierarchicalProjectList',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      pageSize: 15,
      preArr: [
        {
          prop: 'orderNo',
          label: () => this.$t('supplierRating.gradedCoding') // '分级项目编码'
          /* type: "quicksearch",
          showKey: "companyName",
          name: "scc_sup_company_info_display" */
        },
        {
          prop: 'projectName',
          label: () => this.$t('supplierRating.gradedName')
        },
        {
          prop: 'createdBy',
          label: () => this.$t('supplierRating.creator'),
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // {
        //   prop: "inviteStatus",
        //   label: "状态",
        //   type: "select",
        //   options: () => this.VENDOR_LEVEL_STATUS
        // },
        {
          prop: 'dateList',
          label: () => this.$t('vendorMod.relegation.creationTime'),
          type: 'daterange'
        }
        // {
        //   prop: "createEndDate",
        //   label: ()=>this.$t('supplierRating.creationEndTime'),
        //   type: "date",
        // }
      ]
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orderNo',
        label: _this.$t('supplierRating.gradedCoding'),
        width: 120
      },
      {
        prop: 'projectName',
        label: _this.$t('supplierRating.gradedName'),
        width: 150
      },
      {
        prop: 'createdBy',
        label: _this.$t('supplierRating.creator'),
        width: 150
      },
      {
        prop: 'creationDate',
        label: _this.$t('supplierRating.creationDate'),
        type: 'date',
        width: 120
      },
      {
        prop: 'endDate',
        label: _this.$t('supplierRating.endTime'),
        type: 'date',
        width: 120
      },
      {
        prop: 'status',
        label: _this.$t('supplierRating.projectStatus'),
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL_STATUS', // 字典code
        width: 120
      },
      {
        prop: 'errorMessage',
        label: _this.$t('dataConfMod.errorInfo'),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 170,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: (row) => this.editTab('edit', row),
            formattor () {
              return _this.$t('common.edit') // '编辑'
            },
            show: (row) => ['DRAFT', 'FAIL'].includes(row.status)
          },
          {
            callback: (row) => this.editTab('submit', row),
            formattor: () => {
              return this.$t('common.submit')
            },
            show: (row) => row.status === 'DRAFT'
          },
          {
            callback: (row) => this.editTab('view', row),
            // code: "pr:requirementApply:edit",
            formattor: () => {
              return this.$t('common.viewTask')
            },
            show: (row) => ['SUCCESS', 'FAIL', 'CALCULATING'].includes(row.status)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    getQuerydata (v) {
      if (v && v.dateList) {
        v.createStartDate = v.dateList[0]
        v.createEndDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.createStartDate
        delete v.createEndDate
      }
      this.queryParam = v || this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: hierarchicalProjectDeatil,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode,
          tabName: 'hierarchicalProjectDeatil'
        },
        title: this.$t('vendorMod.newProject'),
        name: 'hierarchicalProjectDeatil'
      }
      this.$emit('tab-add', tab)
    },
    editTab (type, row) {
      if (type === 'submit') {
        this.$http({
          url: '/api-pef/perf/vendorlevelhead/get',
          method: 'GET',
          params: { vendorLevelId: row.levelHeadId },
          loading: true
        }).then((resFst) => {
          if (resFst) {
            this.$http({
              url: '/api-pef/perf/vendorlevelhead/submitted',
              method: 'POST',
              data: resFst.data,
              loading: true
            })
              .then((res) => {
                this.$message({
                  type: 'success',
                  message: this.$t('common.successSubmit')
                }) // 提交成功
                this.getQuerydata()
              })
              .catch((err) => {
                console.log(err)
              })
          }
        })
      } else {
        let tab = {
          component: hierarchicalProjectDeatil,
          ctrlHeight: true,
          params: {
            flag: type,
            tabName: 'hierarchicalProjectDeatil' + row.orderNo,
            row
          },
          title: row.orderNo,
          name: 'hierarchicalProjectDeatil' + row.orderNo
        }
        this.$emit('tab-add', tab)
      }
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
          this.$http({
            url: '/api-sup/invite/inviteVendor/delete',
            method: 'GET',
            params: { id: row.inviteVendorId }
          }).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
<style lang="scss" scoped></style>
