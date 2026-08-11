<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      >
        <template #orgName="{ scope }">
          <OrganizationSelector
            ref="ouSelector"
            v-model="scope.organizationName"
            :parent-id="-1"
            node-type="OU"
            :placeholder="$t('common.pleaseSelect')"
            @select="(val) => selectHandler(val, scope)"
          />
        </template>
      </FormWrapper>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :open-custom-table="true"
        url="/api-pef/perf/vendorlevelappr/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import hierarchicalReviewDeatil from './hierarchicalReviewDeatil'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'HierarchicalReviewList',
  components: {
    FormWrapper,
    TableView,
    MainHeader,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'hierarchicalReviewList',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      pageSize: 15,
      username: '',
      preArr: [
        // {
        //   prop: 'orgName',
        //   label: () => this.$t('supplierRating.entity'),
        //   type: 'slot',
        //   slot: 'orgName'
        // },
        {
          prop: 'auditCode',
          label: () => this.$t('supplierRating.approvalNumber')
        },
        {
          prop: 'auditStatus',
          label: () => this.$t('supplierRating.status'),
          type: 'dict', // 字典类型
          code: 'LEVEL_APPROVAL_STATUS' // 字典code
        },
        {
          prop: 'dateList',
          label: () => this.$t('supplierRating.creationDate'),
          type: 'daterange'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('supplierRating.creator')
        }
      ]
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        this.getQuerydata()
        if (this.$route.params.form === 'hierarchicalRating') {
          this.editTab('hierarchicalRating', this.$route.params.vendorLevelApproveId)
          // let orderId = Number(this.$route.params.formId);
          // let formNo = this.$route.params.formNo; // 流程标题
          // let row = {
          //   ...this.$route.params,
          //   orderId,
          //   orderNumber: formNo // tab 标题显示
          // };
          // this.readOne(row);
        }
      }
    }
  },
  created () {
    this.username = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.lastUpdatedUserName
      : ''
    console.log(this.$store.getters)
    let _this = this
    this.tableHeader = [
      {
        prop: 'auditCode',
        label: _this.$t('supplierRating.approvalNumber'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.readOne(row)
      },
      // {
      //   prop: "orgName",
      //   label: "业务实体",
      // },
      {
        prop: 'auditStatus',
        label: _this.$t('common.approvalStatus'),
        dataType: 'dict', // 数据类型为字典
        code: 'LEVEL_APPROVAL_STATUS' // 字典code
      },
      {
        prop: 'createdBy',
        label: _this.$t('supplierRating.creator')
      },
      {
        prop: 'creationDate',
        label: _this.$t('supplierRating.creationDate'),
        dataType: 'dateTime'
      },
      {
        prop: 'auditDate',
        label: _this.$t('supplierRating.approvalTime'),
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: (row) => this.editTab('edit', row),
            formattor () {
              return _this.$t('common.edit') // '编辑'
            },
            show: (row) =>
              ['SUBMITTED', 'REJECTED', 'UNDER_SUBMIT'].includes(row.auditStatus)
              // &&
              // row.createdUserName === this.username
            // show: row => row.auditStatus === "REJECTED" || row.createdUserName === this.username
          },
          {
            callback: (row) => this.approve(row),
            show: (row) => row.auditBy === this.username,
            formattor: () => {
              return _this.$t('common.approve')
            }
          },
          {
            callback: (row) => this.toRefuse('view', row),
            show: (row) => row.auditBy === this.username,
            formattor: () => {
              return _this.$t('common.toRefuse')
            }
          },
          {
            callback: row => this.readOne(row),
            formattor: () => {
              return _this.$t('common.view')
            }
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
    readOne (row) {
      const tab = {
        component: hierarchicalReviewDeatil,
        params: {
          row,
          flag: 'view',
          tabName: 'hierarchicalReviewDeatil' + row.vendorLevelApproveId,
          activeWorkflowTab: false
        },
        title: row.auditCode,
        name: 'hierarchicalReviewDeatil' + row.vendorLevelApproveId
      }
      this.$emit('tab-add', tab)
    },

    selectHandler (val, scope) {
      scope.orgName = val.organizationName || ''
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'hierarchicalRating') {
        tab = {
          component: hierarchicalReviewDeatil,
          params: {
            flag: type,
            tabName: 'hierarchicalReviewDeatil' + row,
            vendorLevelApproveId: row
          },
          title: row,
          name: 'hierarchicalReviewDeatil' + row
        }
      } else {
        tab = {
          component: hierarchicalReviewDeatil,
          params: {
            flag: type,
            tabName: 'hierarchicalReviewDeatil' + row.vendorLevelApproveId,
            row
          },
          title: row.auditCode,
          name: 'hierarchicalReviewDeatil' + row.vendorLevelApproveId
        }
      }

      this.$emit('tab-add', tab)
    },
    toRefuse (row) {},
    approve (row) {
      this.$confirm(this.$t('supplierRating.confirmSubmission'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        //   this.$http({
        //     url: '/api-sup/invite/inviteVendor/delete',
        //     method: 'GET',
        //     params: {id: row.inviteVendorId},
        //   }).then(res => {
        //     this.$message.success(res.message);
        //     this.getQuerydata();
        //   });
      })
      // .catch(() => {
      // });
    }
  }
}
</script>
<style lang="scss" scoped></style>
