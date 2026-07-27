<template>
  <srm-dialog
    :title="$t('remind.tipTitle')"
    :visible.sync="dialogVisible"
    size="small"
    :close-on-click-modal="false"
  >
    <div style="min-height:80px;padding:20px 0;font-size:14px;line-height: 22px;">
      <span style="color:red;">{{ $t('remind.subTip') }}：</span>{{ remindTxt }}
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="dialogVisible = false">{{ $t("common.confirm") }}</el-button>
    </div>
  </srm-dialog>
</template>
<script>
import { getDictItem } from '@/api/common'
export default {
  name: 'SysRemind',
  data () {
    return {
      userType: this.$store.getters.userType,
      dialogVisible: false,
      remindTxt: this.$t('remind.tipCont')
    }
  },
  mounted () {
    let remindTip = 'Y'
    if (remindTip === 'Y') {
      getDictItem('REMIND_TIME').then(res => {
        let timeData = res.data || []
        if (timeData.length > 0) {
          let start = timeData.find(i => i.dictItemCode == 'startTime')
          let end = timeData.find(i => i.dictItemCode == 'endTime')
          let remindTxt = timeData.find(i => i.dictItemCode == 'remindTxt')
          let startTime = new Date(start.dictItemName).getTime()
          let endTime = new Date(end.dictItemName).getTime()
          let currentTime = new Date().getTime()
          if (currentTime > startTime && currentTime < endTime) {
            this.dialogVisible = true
          }
          if (remindTxt) {
            this.remindTxt = remindTxt.dictItemName
          }
        }
      })
    }
  },
  methods: {
  }
}
</script>
