<template>
  <!-- 选择图标 -->
  <srm-dialog
    :title="$t('dataConfMod.selectIcon')"
    size="middle"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="iconsWrapper">
      <i
        v-for="(icon, key) in iconData"
        :key="key"
        :data-class="icon.className"
        :class="['iconfont', icon.className, icon.checked ? 'checked' : '', 'iconCommon']"
        @click="clickHandle($event)"
      ></i>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'

const iconList = ['iconyuyan', 'icondrag', 'iconprocess', 'icontrade-assurance', 'iconiconfuzhi', 'icongerenzhongxin', 'iconwuliu', 'icongongyingshang', 'icongongyingshang1', 'icontrade', 'iconpeiejilu', 'iconhzk-jx', 'iconcomponent', 'iconjixiao', 'icondingdangenzong', 'iconwuliu1', 'iconG-peiechakan-', 'iconjiesuan', 'iconjichushezhi', 'iconcaigouxunyuan', 'icondingdanguanli', 'iconbaojia', 'iconskin', 'iconquality', 'iconxunyuan', 'iconxitongguanli', 'iconarrowBottom', 'iconsearch', 'iconqiyeguanli1', 'iconarrowLeft', 'iconduizhangdan', 'iconarrowTop', 'iconarrowRight', 'iconscc-supplier-analysis', 'iconscc-material-supplier', 'iconscc-non-material-supplier', 'iconscc-contract', 'iconscc-members', 'iconscc-performance', 'iconscc-logistics', 'iconscc-sourcing', 'iconscc-financial', 'iconscc-supplier-sys-man', 'iconscc-infrastructure', 'iconscc-monitoring', 'iconscc-enterprise-man', 'iconscc-sales', 'iconscc-account-privileges', 'iconscc-information', 'iconscc-financial-synergy', 'iconscc-buyer-basic-data', 'iconscc-appraisal', 'iconscc-price-man', 'iconscc-process', 'iconscc-quality-ment', 'iconview', 'icongood', 'iconhome']

export default {
  name: 'Icons',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
      type: String
    }
  },
  data () {
    return {
      iconData: iconList.map(item => ({ className: item, checked: false }))
    }
  },
  watch: {
    visible (n, o) {
      if (n && n !== o && this.id) {
        this.iconData = this.iconData.map(item => {
          if (this.id === item.className) return { ...item, checked: true }
          return { ...item, checked: false }
        })
      }
    }
  },
  methods: {
    close () {
      this.$emit('close')
    },
    clickHandle (e) {
      this.$emit('clickHandle', e)
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.iconsWrapper {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.checked {
  border: 1px solid #999;
}
.iconCommon {
  width: 50px;
  height: 50px;
  font-size: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
</style>
