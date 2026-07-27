<template>
  <el-container class="electronicReport_container">
    <el-aside style="width: 200px;background: #fff;">
      <el-menu
        :default-active="currentPath"
        style="border: none;"
      >
        <el-menu-item
          v-for="item in electronicReport"
          :key="item.id"
          :index="item.value"
          :title="item.label"
          @click="clickHandle(item.value)"
        >
          <span slot="title">{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <div class="iframe_wrapper">
        <iframe
          v-if="!!source"
          class="report_iframe"
          :src="source"
          title=""
        />
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'ElectronicReport',
  components: {},
  data () {
    return {
      electronicReport: null,
      currentPath: null
    }
  },
  computed: {
    source () {
      return this.currentPath
        ? `${sysPrefix()}/api-base/ureport/preview?_u=${this.currentPath}`
        : null
    }
  },
  created () {
    this.initDictionary()
  },
  methods: {
    clickHandle (path) {
      this.currentPath = path
    },
    initDictionary () {
      const params = ['ELECTRONIC_REPORT'].map(i => ({
        dictCode: i
      }))
      getDictItemList(params).then(res => {
        const [ELECTRONIC_REPORT] = res.data
        const electronicReport = adaptDictData(
          ELECTRONIC_REPORT.ELECTRONIC_REPORT
        )
        this.electronicReport = electronicReport
        this.currentPath = electronicReport[0]
          ? electronicReport[0].value
          : null
      })
    }
  }
}
</script>
<style scoped lang="scss">
.electronicReport_container {
  height: 100%;
}
.report_iframe {
  width: 100%;
  height: 100%;
  position: absolute;
  border: 0;
  top: 0;
  bottom: 0;
}
.list-item {
  list-style: none;
}
.iframe_wrapper {
  position: relative;
  height: 100%;
}
</style>
