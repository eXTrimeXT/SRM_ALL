<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <p style="margin-top: 0">
        <!--立即结束报名-->
        <el-button
          v-if="projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP"
          type="primary"
          @click="stopSignUp"
        >
          {{ $t('bidMod.biddingManagementBuyer.changeSignUpEndTime') }}
        </el-button>

        <!--刷新-->
        <el-button class="detail-pbtn" @click="() => $refs.applyManageTable.query()">
          {{ $t('common.refresh') }}
        </el-button>
      </p>

      <TableView
        ref="applyManageTable"
        :table-data="applyManageTableData"
        :table-header="applyManageTableHeader"
        :pre-query-data="{ projectId: projectId }"
        auto-query
        :url="tableViewUrl"
      />

      <!--审查报名资料-->
      <EntryManagementDialog
        v-if="entryManagementDialogVisible"
        :visible.sync="entryManagementDialogVisible"
        :edit-row="editRow"
        :project-id="projectId"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 报名管理
 */
import { SOU_PROJECT_STATUS_ENUM, SOU_SIGN_UP_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import TableView from 'lib@/components/Table/TableView'
import EntryManagementDialog from './applyManage/entryManagementDialog'

export default {
  name: 'ApplyManage',

  components: {
    TableView,
    EntryManagementDialog
  },

  props: {
    projectId: {
      type: [Number, String],
      default: ''
    },
    projectStatus: {
      type: String,
      default: ''
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      tableViewUrl: compBuyerHttp.signUp.listPageUrl,
      applyManageTableHeader: [
        // 供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.supplierCode'),
          width: 120
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('bidMod.vendorName'),
          minWidth: 150
        },
        // 联系人
        {
          prop: 'linkmanName',
          label: this.$t('bidMod.contactMan'),
          width: 120
        },
        // 电话
        {
          prop: 'phone',
          label: this.$t('bidMod.registPhone'),
          width: 120
        },
        // 邮箱
        {
          prop: 'email',
          label: this.$t('bidMod.registPostbox'),
          width: 120
        },
        // 参与状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.Participate'),
          width: 110,
          formattor: val => this.$getDictLabel('SOU_SIGN_UP_STATUS', val)
        },
        // 报名资料
        {
          prop: 'inquiryNo',
          label: this.$t('bidMod.applicationChecklist'),
          width: 110,
          showType: 'buttons',
          btnStyle: 'text',
          buttons: [
            // 审查
            {
              // 确认中 已报名
              show: row => [SOU_SIGN_UP_STATUS_ENUM.CONFIRM_ING, SOU_SIGN_UP_STATUS_ENUM.SIGN_UP_DONE].includes(row.signUpStatus),
              callback: row => this.openEntryManagementDialog(row),
              formattor: () => this.$t('bidMod.investigate')
            }
          ]
        },
        // 报名时间
        {
          prop: 'signUpTime',
          label: '报名时间',
          width: 140,
          dataType: 'dateTime'
        }
      ],
      applyManageTableData: [],
      entryManagementDialogVisible: false,
      editRow: null,
      SOU_PROJECT_STATUS_ENUM
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          if (this.$refs.applyManageTable) {
            this.$refs.applyManageTable.query()
          }
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 审查报名信息 */
    openEntryManagementDialog (row) {
      this.editRow = row
      this.entryManagementDialogVisible = true
    },

    /* 立即结束报名 */
    async stopSignUp () {
      const response = await compBuyerHttp.signUp.changeSignUpEndTime({
        projectId: this.projectId,
        stopNow: true
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$refs.applyManageTable.query()
        // 更新节点
        this.$emit('updateProcessNode')
        // 更新基础数据
        this.$emit('fetchBaseInfo')
      }
    }
  }
}
</script>
