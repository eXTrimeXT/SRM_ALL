<template>
  <div class="cat-left-navi-bar">
    <div v-for="(item, i) in menuData" :key="'bar-item-'+i" class="bar-item">
      <div style="display: flex;justify-content: space-between;align-items: center" @click="sendId(item)">
        <div style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap">
          {{ item.categoryName }}
        </div>
        <i v-if="item.children.length != 0" class="el-icon-arrow-right right" />
        <i v-if="item.children.length != 0" class="el-icon-arrow-left left" />
      </div>
      <div v-if="item.children.length != 0" class="menu-detail">
        <div v-for="(item1, i1) in item.children" :key="'menu-detail-'+ i1">
          <div class="second-title" @click="sendId(item1)">
            {{ item1.categoryName }}
          </div>
          <div class="second-content">
            <div
              v-for="(item2, i2) in item1.children"
              :key="i2"
              class="second-content-item"
              :class="categoryId == item2.categoryId ? 'blue' : ''"
              @click="sendId(item2)"
            >
              {{ item2.categoryName }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LeftNaviBar',
  data () {
    return {
      menuData: [],
      categoryId: null
    }
  },
  created () {
    this.queryMenu()
  },
  methods: {
    sendId (item) {
      this.categoryId = item.categoryId
      this.$emit('send-id', item)
    },
    queryMenu () {
      this.$http({
        url: '/api-base/pj/category/lastThreeLevelCategoryTree',
        method: 'GET'
      }).then(res => {
        console.log(res)
        if (res.code == '0') {
          this.menuData = res.data
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.cat-left-navi-bar {
  height: 100%;
  overflow-y: auto;
  .bar-item {
    width: 185px;
    height: 40px;
    line-height: 40px;
    margin: 8px 0;
    padding: 0 12px 0 16px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #161C24;
    font-weight: 400;
    &:hover {
      background: #E7F2FF;
      color: #0077FF;
      cursor: pointer;
      .menu-detail,.left {
        display: block;
      }
      .right {
        display: none;
      }
    }
  }

  .right {
    display: block;
  }

  .left {
    display: none;
  }

  .blue {
    color: #0077FF;
  }

  .menu-detail {
    display: none;
    width: 632px;
    overflow-y: auto;
    position: absolute;
    left: 184px;
    top: -17px;
    bottom: -12px;
    z-index: 50;
    background: #FFFFFF;
    border: 1px solid #DCDDDE;
    box-shadow: 0 9px 28px 8px rgba(0,0,0,0.05), 0 6px 16px 0 rgba(0,0,0,0.08), 0 3px 6px -12px rgba(0,0,0,0.12);
    border-radius: 2px;
    padding: 21px 0 0 16px;
    & > div:nth-child(n+1) {
      margin-bottom: 21px;
    }
  }

  .second-title {
    font-family: PingFangSC-Semibold;
    font-size: 14px;
    color: #212B36;
    line-height: 22px;
    font-weight: 600;
  }

  .second-content {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #73777C;
    line-height: 22px;
    font-weight: 400;
    padding: 10px 84px 0 12px;
    display: flex;
    flex-wrap: wrap;
  }

  .second-content-item {
    flex: 0 0 25%;
    padding: 5px 0 5px 12px;
  }

  .second-content-item:hover {
    background: #E7F2FF;
    color: #0077FF;
  }
}

</style>
