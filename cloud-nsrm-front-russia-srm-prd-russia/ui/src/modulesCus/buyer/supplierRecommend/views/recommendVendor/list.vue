<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <QuickSearch
            showButton
            class="quickBtn"
            :btnTitle="'不公示推荐(' + recommWithoutPublic +')'"
            name="REQQIRE_TO_RECOMM_WITHOUT_PUBLIC"
            :confirmAutoClose="true"
            multiSelect
            @close-quicksearch="getWithoutPublic"
          />
          <QuickSearch
            showButton
            class="quickBtn"
            :btnTitle="'公示推荐(' + recommPublic +')'"
            name="REQQIRE_TO_RECOMM_PUBLIC"
            @close-quicksearch="getPublic"
          />
          <AuthorityButton code="recommendVendor:append" type="primary" @click="appendVendor">
            追加供应商
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import QuickSearch from '@/library/components/QuickSearch'
import RecommendVendorDetail from './edit'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import recommendHttp from '../../api'
import { uniqBy } from 'lodash'

export default {
  name: 'RecommendVendorList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: recommendHttp.listPageUrl,
      recommWithoutPublic: 0,
      recommPublic: 0,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'souName',
          label: '项目名称'
        },
        {
          prop: 'extRecommendNo',
          label: '供应商推荐单号'
        },
        {
          prop: 'projectStatus',
          label: '单据状态',
          type: 'dict',
          code: 'SOU_RECOMMVENDOR_STATUS'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'extCompanyAddr',
          label: '公司地址'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        },
        {
          prop: 'extCategoryName',
          label: '品类',
          type: 'catSelect',
          showKey: 'categoryName'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      inviteDialogVisible: false,
      inviteHisDialogVisible: false,
      recommendDialogVisible: false
    }
  },

  computed: {
    username () {
      return this.$store.getters.userInfo.username || ''
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        console.log('nVal', nVal)
        if (nVal) {
          const { from, funName, formId, formNo, taskIndex } = nVal
          if (from === 'fromFun' && funName === 'recommendVendor' && taskIndex === 2) {
            this.editTab('view', {
              projectId: formId,
              extRecommendNo: formNo
            })
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'extRecommendNo',
        label: '供应商推荐单号',
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'extOrgBuName',
        label: '板块',
        minWidth: 120
      },
      {
        prop: 'extOrgOuName',
        label: '公司',
        minWidth: 150
      },
      {
        prop: 'souName',
        label: '项目名称',
        minWidth: 150
      },
      {
        prop: 'sourceFromType',
        label: '需求来源',
        minWidth: 120,
        dataType: 'dict',
        code: 'PR_SOU_REQUIREMENT_FROM'
      },
      {
        prop: 'projectStatus',
        label: '单据状态',
        dataType: 'dict',
        code: 'SOU_RECOMMVENDOR_STATUS',
        minWidth: 120
      },
      {
        prop: 'rcommendType',
        label: '推荐供应商类型',
        // dataType: 'dict',
        // code: 'SOU_RECOMMVENDOR_TYPE',
        minWidth: 130,
        formattor: (val) => this.$getDictLabel('SOU_RECOMMVENDOR_TYPE', val)
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: '创建日期',
        minWidth: 130
      },
      // {
      //   prop: 'partCancle',
      //   label: '是否部分取消',
      //   dataType: 'dict',
      //   code: 'YES_OR_NO',
      //   minWidth: 130
      // },
      {
        prop: 'partCancle',
        label: '是否部分取消',
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECT'].includes(row.projectStatus),
            formattor: () => this.$t('common.edit'),
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.projectStatus),
            formattor: () => this.$t('common.delete'),
            callback: row => {
              this.deleteRows(row)
            }
          },
          // 审批中 需要审批操作
          {
            show: row => ['APPROVING'].includes(row.projectStatus) && row.startBpmUsername === this.username,
            formattor: () => this.$t('common.approve'),
            code: 'recommendVendor:approval',
            callback: row => {
              this.editTab('approval', row)
            }
          }
          // {
          //   show: row => ['APPROVING'].includes(row.projectStatus),
          //   callback: row => this.approvalPassOne(row),
          //   formattor: () => {
          //     return '审批通过'
          //   }
          // 点击打开流程tab tab模式下，已提交 的状态 是当前人的审批单据时显示审批按钮
          // 已撤回、已驳回 显示编辑按钮重新提交
          // }
        ]
      }
    ]
    this.getRecommCount()
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      const { extRecommendNo, ...rest } = params
      transformTimeQuery(['creationDate'], rest)
      let query = {
        '*': {},
        'recommvendorProjectExtend': {
          '*': {}
        }
      }
      if (extRecommendNo) {
        query.recommvendorProjectExtend.$condition = {
          '$strictQuery': true,
          filter: {
            extRecommendNo: {
              contains: extRecommendNo
            }
          }
        }
      }
      this.queryParam = transformMQL.listPageData({
        type: 'RecommvendorProject',
        action: 'query',
        params: {
          ...rest
        },
        query,
        filterOperator: {
          creationDate: 'between'
        }
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    afterQuery (data) {
      if (!data || !data.length) return
      data.forEach(item => {
        const { recommvendorProjectExtend } = item
        if (recommvendorProjectExtend && recommvendorProjectExtend.length) {
          for (let key in recommvendorProjectExtend[0]) {
            item[key] = recommvendorProjectExtend[0][key]
            item.rcommendType = recommvendorProjectExtend[0].rcommendType
          }
        }
      })
    },

    async getRecommCount () {
      const response = await recommendHttp.countRecomm()
      if (response && response.data) {
        this.recommWithoutPublic = response.data.recommWithoutPublic
        this.recommPublic = response.data.recommPublic
      }
      console.log('response', response)
    },

    async getWithoutPublic (val) {
      console.log('val', val)
      if (val && val.length) {
        // TODO 待校验 单号相同才能一起生产供应商推荐
        if (uniqBy(val, 'souNo').length > 1) {
          return this.$message.warning('所勾选行招标单号不一致，不允许创建')
        }
        let params = val.map(item => ({
          requirementHeadId: item.requirementHeadId
        }))
        let transformParams = transformMQL.save('PrSouRequirementPoolForBuyer', params, 'createVendorRecommend')
        const response = await recommendHttp.createVendorRecommend(transformParams)
        if (response.data.records?.length) {
          const result = response.data.records[0]
          if (result.projectId) {
            this.editTab('edit', {
              projectId: result.projectId,
              extRecommendNo: result.project.extRecommendNo
            })
          } else {
            this.$message.warning('操作失败')
          }
        }
      }
    },

    async getPublic (val) {
      console.log('val', val)
      if (!val || !val.reqHeadId) return
      let transformParams = transformMQL.save('SouReqApplyBuyer', [{ reqHeadId: val.reqHeadId }], 'createVendorRecommend')
      const response = await recommendHttp.createSouVendorRecommend(transformParams)
      if (response.data.records?.length) {
        const { projectId, extRecommendNo } = response.data.records[0]
        if (projectId) {
          this.editTab('edit', {
            projectId,
            extRecommendNo
          })
        } else {
          this.$message.warning('操作失败')
        }
      }
    },

    async approvalPassOne (row) {
      this.$http({
        url: '/api-pj/external/bpm/callback',
        method: 'POST',
        data: {
          ControlState: 'end',
          businessId: row.projectId
        },
        loading: true
      }).then(res => {
        this.getQuerydata()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 编辑
        [
          'edit',
          {
            component: RecommendVendorDetail,
            params: {
              flag: type,
              row,
              tabName: row.extRecommendNo
            },
            title: '推荐供应商' + (row.extRecommendNo || ''),
            name: row.extRecommendNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: RecommendVendorDetail,
            params: {
              flag: type,
              row,
              tabName: '推荐供应商' + row.projectId
            },
            title: '推荐供应商' + (row.extRecommendNo || ''),
            name: '推荐供应商' + row.projectId
          }
        ],
        // 审批
        [
          'approval',
          {
            component: RecommendVendorDetail,
            params: {
              flag: type,
              row,
              tabName: row.extRecommendNo
            },
            title: '推荐供应商' + (row.extRecommendNo || ''),
            name: row.extRecommendNo
          }
        ],
        // 编辑
        [
          'append',
          {
            component: RecommendVendorDetail,
            params: {
              flag: type,
              row,
              tabName: row.extRecommendNo
            },
            title: '追加供应商' + (row.extRecommendNo || ''),
            name: row.extRecommendNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /** 追加供应商 */
    appendVendor () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (this.selectedRows.length > 1) return this.$message.warning('仅可勾选一条数据')
      for (let item of this.selectedRows) {
        if (item.projectStatus !== 'APPROVED') {
          return this.$message.warning('已审批单据才可发起追加')
        }
      }
      this.editTab('append', this.selectedRows[0])
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('RecommvendorProject', [row.projectId], 'delete')
      await recommendHttp.delete(transformParams)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    },

    /** 废弃 */
    abandonRows () {

    }
  }
}
</script>
<style lang="scss" scoped>
.quickBtn {
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
}
</style>
