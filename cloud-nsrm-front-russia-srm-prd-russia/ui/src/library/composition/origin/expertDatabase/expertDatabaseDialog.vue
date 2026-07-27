<template>
  <SrmDialog
    v-if="dialogVisible"
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.expertDatabaseTitle')"
    :close-on-click-modal="false"
    size="large"
    append-to-body
  >
    <fieldset class="page-section">
      <legend class="page-section__title">
        {{ $t("bid_mod.expertDataBaseList") }}
      </legend>

      <FormWrapper
        :form-array="formArray"
        form-label-width="110px"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        style="height: 300px; padding: 0"
        :table-header="tableHeader"
        :check-change="checkChange"
        checkbox
        :pre-query-data="queryParam"
        :source="$api.brg.bargain.expertListPage"
      />
    </fieldset>

    <template #footer class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="save">
        {{ $t("common.submit") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
import { parseTime } from '@/utils'
import TableView from '@/library/components/Table/TableView'
import FormWrapper from '@/library/components/Table/FormWrapper'

export default {
  name: 'ExpertDatabaseDialog',

  components: {
    TableView,
    FormWrapper
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    groupList: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      queryParam: {},
      gridId: 'list',
      expertList: [],
      formArray: [
        { prop: 'expertName', label: () => this.$t('bidMod.expertName') },
        { prop: 'expertJob', label: () => this.$t('bidMod.expertType') }
      ],
      tableHeader: [
        {
          prop: 'expertName',
          label: () => this.$t('bidMod.expertName'),
          minWidth: 150
        },
        {
          prop: 'expertJobName',
          label: () => this.$t('bidMod.expertType'),
          width: 150
        },
        {
          prop: 'expertAccount',
          label: () => this.$t('bidMod.expertAccount'),
          width: 150
        },
        { prop: 'phone', label: () => this.$t('bidMod.phone'), width: 130 },
        { prop: 'email', label: () => this.$t('bidMod.email'), width: 200 },
        {
          prop: 'status',
          label: () => this.$t('bidMod.status'),
          width: 100,
          formattor: value => this.$getDictLabel('BASE_INFO_STATUS', value)
        },
        {
          prop: 'startDate',
          label: () => this.$t('bidMod.startDate'),
          width: 100,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'endDate',
          label: () => this.$t('bidMod.endDate'),
          width: 100,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      ],
      currentRows: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.getQueryData()
  },

  methods: {
    getQueryData (v) {
      this.queryParam = v || this.queryParam
      this.queryParam.status = 'VALID'
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    checkChange (rows) {
      this.currentRows = rows
    },

    save () {
      if (!this.currentRows.length) {
        return this.$message.error(this.$t('bidMod.msgExpertAdd')) // 请选择要添加到选定专家列表中的数据！
      }

      const hadExistAccount = this.groupList
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

      const hadExistExpert = this.groupList
        .filter(i => i.maxEvaluateScore)
        .map(i => i.userName)
      const finalList = this.expertList.filter(
        i => !hadExistExpert.includes(i.userName)
      )
      this.$emit('save', finalList)
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.page-section {
  padding: 10px;
  border: solid 1px #dddddd;
  border-radius: 8px;
  margin-bottom: 10px;
  margin-top: 10px;
  min-width: 99%;
  width: 99%;
}
.page-section__title {
  color: #807a7a;
}
</style>
