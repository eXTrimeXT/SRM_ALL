<template>
  <srm-dialog
    :visible.sync="expertDatabaseVisible"
    :before-close="onClose"
    :title="$t('bid_mod.expertDatabaseTitle')"
    :close-on-click-modal="false"
    size="large"
  >
    <fieldset class="page-section">
      <legend class="page-sectionn__title">
        {{ $t("bid_mod.expertDataBaseList") }}
      </legend>
      <form-wrapper
        :form-array="formArray"
        @getFormData="getQuerydata"
      />
      <main-header>
        <!-- <template #left>
          <el-button type="primary" @click="saveToExpertList">
            {{ $t("bid_mod.saveToExpertList") }}
          </el-button>
        </template> -->
      </main-header>
      <table-view
        :ref="gridId"
        style="width: 870px;height: 300px;"
        :table-header="tableHeader"
        :check-change="checkChange"
        checkbox
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :source="$api.logistics.bidding.expertListPage"
      />
    </fieldset>
    <fieldset class="page-section">
      <legend class="page-sectionn__title">
        {{ $t("bid_mod.checkExpert") }}
      </legend>
      <base-table
        stripe
        style="width: 870px;max-height: 300px;"
        :data="expertList"
        :columns="expertColumns"
        columns-name="expertColumns"
        :empty-text="$t('components.noData')"
        border
        @deleteExpertHandle="deleteExpertHandle"
      >
        <template #isFirstResponse="scope">
          <el-checkbox
            v-model="scope.row.isFirstResponse"
            true-label="Y"
            false-label="N"
          />
        </template>
        <template #maxEvaluateScore="scope">
          <el-input
            v-model="scope.row.maxEvaluateScore"
            v-input-format="{ type: 'number' }"
            disabled
          />
        </template>
      </base-table>
    </fieldset>
    <template
      #footer
      class="dialog-footer"
    >
      <el-button
        type="primary"
        @click="save"
      >
        {{ $t("common.save") }}
      </el-button>
      <el-button @click="onClose">
        {{ $t("common.cancel") }}
      </el-button>
    </template>
  </srm-dialog>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'ExpertDatabaseDialog',
  components: { BaseTable, TableView, FormWrapper, MainHeader },
  props: ['expertDatabaseVisible', 'originList'],
  data () {
    return {
      pageSize: 15,
      queryParam: {},
      gridId: 'list',
      expertList: [],
      formArray: [
        { prop: 'expertName', label: () => this.$t('bidMod.expertName') },
        { prop: 'expertType', label: () => this.$t('bidMod.expertType') },
        {
          prop: 'expertClassify',
          label: () => this.$t('bidMod.expertClassify')
        }
      ],
      tableHeader: [
        {
          prop: 'expertName',
          label: () => this.$t('bidMod.expertName'),
          minWidth: 150
        },
        {
          prop: 'expertType',
          label: () => this.$t('bidMod.expertType'),
          width: 150
        },
        {
          prop: 'expertAccount',
          label: () => this.$t('bidMod.expertAccount'),
          width: 150
        },
        { prop: 'phone', label: () => this.$t('bidMod.phone'), width: 100 },
        { prop: 'email', label: () => this.$t('bidMod.email'), width: 200 },
        {
          prop: 'status',
          label: () => this.$t('bidMod.status'),
          width: 100,
          formattor: value => this.$getDictLabel('YES_OR_NO', value)
        },
        {
          prop: 'startDate',
          label: () => this.$t('bidMod.startDate'),
          width: 100,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'endDate',
          label: () => this.$t('bidMod.endDate'),
          width: 100,
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        }
      ],
      expertColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bidMod.expertName'),
            prop: 'fullName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bidMod.expertType'),
            prop: 'position'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '130',
            label: t => t.$t('bid_mod.isFirstResponse'),
            prop: 'isFirstResponse'
          },
          slot: 'isFirstResponse'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.maxEvaluateScore'),
            prop: 'maxEvaluateScore'
          },
          slot: 'maxEvaluateScore'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bidMod.expertAccount'),
            prop: 'userName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bidMod.phone'),
            prop: 'phone'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bidMod.status'),
            formatter: (row, column, cellValue, index) => {
              return this.$getDictLabel('YES_OR_NO', cellValue)
            },
            prop: 'status'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '130',
            label: t => t.$t('bidMod.startDate'),
            prop: 'startDate'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '130',
            label: t => t.$t('bidMod.endDate'),
            prop: 'endDate'
          }
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteExpertHandle',
              event: 'deleteExpertHandle',
              name: this.$t('common.delete'),
              // show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      currentRows: []
    }
  },
  watch: {
    expertDatabaseVisible: {
      handler (value) {
        if (value) {
          this.getQuerydata()
          // 筛选工作小组中 从专家库添加的人员 只有专家库的人员 才有最大评分值
          const expertList = this.originList.filter(i => i.maxEvaluateScore)
          this.expertList = expertList
        }
      },
      immediate: true
    }
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.queryParam.status = 'Y'
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (rows) {
      this.currentRows = rows
    },
    onClose () {
      console.log('[update]: expertDatabaseVisible')
      this.$emit('update:expertDatabaseVisible', false)
    },
    deleteExpertHandle (scope) {
      this.expertList.splice(scope.$index, 1)
    },
    save () {
      if (!this.currentRows.length) {
        return this.$message.error(this.$t('bidMod.msgExpertAdd')) // 请选择要添加到选定专家列表中的数据！
      }
      const hadExistAccount = this.originList
        .filter(i => i.maxEvaluateScore)
        .map(i => i.userName)
      const checkHadExist = this.currentRows.some(i =>
        hadExistAccount.includes(i.expertAccount)
      )
      if (checkHadExist) {
        return this.$message.error(this.$t('bidMod.msgExpertGroupRepeat')) // 选中的专家中和工作小组成员有重复！
      }
      const hadExistUserName = this.expertList.map(i => i.userName)
      const checkHadExistInExpert = this.currentRows.some(i =>
        hadExistUserName.includes(i.expertAccount)
      )
      if (checkHadExistInExpert) {
        return this.$message.error(this.$t('bidMod.msgExpertRepeat')) // 不能重复添加相同的专家！
      }
      this.currentRows.forEach(item => {
        const {
          expertName: fullName,
          expertType: position,
          expertAccount: userName,
          ...rest
        } = item
        const data = {
          fullName,
          position,
          userName,
          maxEvaluateScore: 100,
          isFirstResponse: 'N',
          judgeFlag: 'Y',
          confirmedFlag: 'Y',
          confirmeDatetime: new Date(Date.now()),
          ...rest,
          lastUpdatedBy: ''
        }
        this.expertList.push(data)
      })
      const hadExistExpert = this.originList
        .filter(i => i.maxEvaluateScore)
        .map(i => i.userName)
      const finalList = this.expertList.filter(
        i => !hadExistExpert.includes(i.userName)
      )
      this.$emit('save', finalList)
      this.onClose()
    }
  }
}
</script>
<style scoped>
.buttons {
  margin-bottom: 10px;
}
.page-section {
  padding: 10px;
  border: solid 1px #dddddd;
  border-radius: 8px;
  margin-bottom: 10px;
  margin-top: 10px;
}
.page-section__title {
  color: #807a7a;
}
</style>
