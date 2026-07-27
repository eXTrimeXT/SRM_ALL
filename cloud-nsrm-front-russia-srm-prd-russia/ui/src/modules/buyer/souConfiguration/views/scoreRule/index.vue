<template>
  <el-container
    class="flex-container-notab the_inquiryBasicData_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />

      <MainHeader>
        <template slot="left">
          <el-button
            code="bid:bidingBasicData:createNewScoreRule"
            type="primary"
            @click="openDetailDialog('add', null)"
          >
            {{ $t("common.add") }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :url="tableViewUrl"
      />

      <!--详情弹窗-->
      <DetailDialog
        v-if="detailDialogVisible"
        :visible.sync="detailDialogVisible"
        :edit-row="editRow"
        :flag="detailDialogFlag"
        @save-success="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { scoreRuleHttp } from 'modb@/souConfiguration/api'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import DetailDialog from './detailDialog.vue'

export default {
  name: 'BidingBasicData',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    DetailDialog
  },

  data () {
    return {
      tableHeader: [
        // 评分规则编码
        {
          prop: 'scoreRuleNo',
          label: this.$t('bidMod.scoreRuleModelNo'),
          minWidth: 150
        },
        // 评分规则名称
        {
          prop: 'scoreRuleName',
          label: this.$t('bidMod.evalRuleName'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailDialog('view', row)
        },
        // 寻源方式
        {
          prop: 'souType',
          label: this.$t('bidMod.sourceType'),
          minWidth: 150,
          formattor: val => this.$getDictLabel('SOU_TYPE', val)
        },
        // 总分值
        {
          prop: 'totalScore',
          label: this.$t('bidMod.totalScore'),
          width: 100
        },
        // 状态
        {
          prop: 'scoreRuleStatus',
          label: this.$t('bidMod.status'),
          width: 100,
          formattor: val => this.$getDictLabel('SOU_SCORE_RULE_STATUS', val)
        },
        // 编辑时间
        {
          prop: 'lastUpdateDate',
          label: this.$t('bidMod.lastUpdateDate'),
          width: 100,
          formattor: val => this.$dayjsParse(val)
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('bidMod.creationDate'),
          width: 100,
          formattor: val => this.$dayjsParse(val)
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            // b 编辑
            {
              // 拟定
              show: row => ['DRAFT'].includes(row.scoreRuleStatus),
              callback: row => this.openDetailDialog('edit', row),
              formattor: () => this.$t('common.edit')
            },
            // 删除
            {
              // 拟定
              show: row => ['DRAFT'].includes(row.scoreRuleStatus),
              callback: row => this.deleteRow(row),
              formattor: () => this.$t('common.delete')
            },
            // 生效
            {
              // [拟定, 失效]
              show: row => ['DRAFT', 'INVALID'].includes(row.scoreRuleStatus),
              callback: row => this.activeOrInactiveRow('valid', row),
              formattor: () => this.$t('common.active')
            },
            // 失效
            {
              // 生效
              show: row => ['VALID'].includes(row.scoreRuleStatus),
              callback: row => this.activeOrInactiveRow('invalid', row),
              formattor: () => this.$t('common.inactive')
            }
          ]
        }
      ],
      tableData: [],
      queryParam: {},
      preArr: [
        // 评分规则编码
        { prop: 'scoreRuleNo', label: this.$t('bidMod.scoreRuleModelNo') },
        // 评分规则名称
        { prop: 'scoreRuleName', label: this.$t('bidMod.evalRuleName') },
        // 状态
        {
          prop: 'status',
          label: this.$t('bidMod.status'),
          type: 'dict',
          code: 'SOU_SCORE_RULE_STATUS'
        }
      ],
      editRow: null,
      detailDialogFlag: 'add',
      detailDialogVisible: false,
      tableViewUrl: scoreRuleHttp.listPageUrl
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 删除 */
    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await scoreRuleHttp.delete(row.scoreRuleId)
      if (response) {
        // 删除成功
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    },

    /* 新增 / 编辑 / 查看 */
    openDetailDialog (type, row) {
      this.editRow = row
      this.detailDialogFlag = type
      this.detailDialogVisible = true
    },

    /* 生效 / 失效 */
    async activeOrInactiveRow (type, row) {
      const response = await scoreRuleHttp[type](row.scoreRuleId)
      if (response) {
        // 删除成功
        this.$message.success(this.$t('common.successSave'))
        this.getQueryData()
      }
    }
  }
}
</script>
