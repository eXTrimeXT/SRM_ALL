<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <ul
        v-if="reportList.length>0"
        class="commonMenuList"
      >
        <li
          v-for="(item, k) in reportList"
          :key="k"
        >
          <img
            v-if="item.iconSrc"
            :src="item.iconSrc"
            width="40px"
            height="40px"
            alt=""
          >
          <div
            v-else
            style="width:40px;height:40px;background:#ddd;border-radius:10px;"
          />
          <p
            class="the_sub_route"
            :title="item.nickName"
            @click="toDetail(item)"
          >
            {{ item.nickName }}
          </p>
        </li>
      </ul>
      <div
        v-else
        class="noDataTip"
      >
        <c-exception
          type="401"
          title="暂无数据!"
          sub-title="请联系管理员配置相关报表，并赋值相应角色！"
        />
      </div>
    </el-main>
  </el-container>
</template>

<script>
import dynamicReportPage from '../dynamicReportPage'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CException from 'lib@/components/c-exception'

export default {
  name: 'ReportStatisticsList',
  components: {
    CException
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      reportList: [],
      id2icon: {}
    }
  },
  created () {
    this.init()
  },
  methods: {
    // 查询图标
    async getIconList () {
      const parmas = {
        fileModular: 'rbac',
        fileFunction: 'function',
        fileType: 'icon',
        convertBase64: 'Y',
        pageNum: 1,
        pageSize: 200
      }
      const { data } = await this.$api.accountAccess.funIconListPage(parmas)
      let iconList = data.list || []
      let resId2icon = iconList.reduce((id2icon, item) => {
        const { fileuploadId, base64 } = item
        const src = `data:image/png;base64,${item.base64}`
        id2icon[fileuploadId] = src
        return id2icon
      }, {})
      return resId2icon
    },
    // 查询报表
    async listPage () {
      let params = {
        pageSize: 100,
        pageNum: 1
      }
      const { data } = await this.$api.base.dynamicSql.listPageWithUserType(params)
      return data.list || []
    },
    async init () {
      let id2iconObj = await this.getIconList()
      let reportList = await this.listPage()
      this.reportList = reportList.map(i => ({
        ...i,
        iconSrc: i.icon ? id2iconObj[i.icon] : ''
      }))
    },
    // 跳转详情
    toDetail (item) {
      this.$emit('tab-add', {
        component: dynamicReportPage,
        params: {
          flag: 'view',
          sqlCode: item.name
        },
        title: item.nickName || '查看报表',
        name: 'dynamicReportPage' + item.name
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.commonMenuList {
    display: flex;
    justify-content: flex-start;
    width: 100%;
    margin: 0;
    list-style: none;
    padding: 40px 10px 20px;
    li {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      text-align: center;
      list-style: none;
      padding: 0;
      width: 200px;
      .the_sub_route {
        font-size: 14px;
        color: #292929;
        margin: 8px 0;
        max-width: 92%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      .the_sub_route:hover {
        color: #1890ff;
        cursor: pointer;
      }
      i {
        font-size: 38px;
        color: #1890ff;
      }
    }
  }
  .noDataTip{
    padding: 100px;
    text-align: center;
  }
</style>
