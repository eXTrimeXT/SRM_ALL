<template>
  <div class="chart-wrapper outerWrap">
    <!-- <el-row :gutter="15">
      <el-col
        :xs="24" :sm="24" :md="24" :lg="12" :xl="12"
        class="supply"
      >
        <div class="header">
          <div class="title">品类供方数占比</div>
          <img :src="imgUrl">
        </div>
        <div class="supply-chart">
            <supplyChart :height="height" width="100%;"/>
        </div>
      </el-col>
      <el-col
        :xs="24" :sm="24" :md="24" :lg="12" :xl="12"
        class="purchase" :class="{'mt-10':['device-xs','device-sm','device-md'].includes(device)}"
      >
        <div class="header">
          <div class="title">采购准时率趋势</div>
          <img :src="imgUrl">
        </div>
        <div class="purchase-chart">
          <purchaseChart :height="height" width="100%;"/>
        </div>
      </el-col>
    </el-row>
    <el-row class="map-wrapper" :class="{'mb-10':['device-xs','device-sm','device-md'].includes(device)}">
      <el-col>
        <div class="header">
          <div class="title">供应商地图分布</div>
            <img :src="imgUrl">
        </div>
        <div class="map-chart">
          <mapChart height="274px"  width="100%;"/>
        </div>
      </el-col>
    </el-row> -->
    <div class="header analyse">
      <div class="title">
        报表分析
      </div>
      <div class="more">
        <!-- 更多> -->
        {{ $t("common.more") }}
        <i class="el-icon-arrow-right" />
      </div>
    </div>
    <el-row class="chart-wrapper">
      <el-col
        :xs="24" :sm="24" :md="10" :lg="10" :xl="10"
        class="chart-left"
      >
        <div class="chart-supply">
           <div class="header">
            <div class="title">
              品类供方数占比
            </div>
            <img :src="imgUrl">
          </div>
          <div class="chart">
            <supplyChart height="155px" width="100%;" />
          </div>
        </div>
        <div class="chart-purchase">
          <div class="header">
            <div class="title">
              采购准时率趋势
            </div>
            <img :src="imgUrl">
          </div>
          <div class="chart">
            <purchaseChart :height="height" width="100%;" />
          </div>
        </div>
      </el-col>
      <el-col
        :xs="24" :sm="24" :md="14" :lg="14" :xl="14"
        class="chart-right"
      >
        <div class="header">
          <div class="title">
            供应商地图分布
            </div>
            <img :src="imgUrl">
        </div>
        <div class="map-chart">
          <mapChart height="368px" width="100%;" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import supplyChart from '../buyer/components/supplyChart'
import purchaseChart from '../buyer/components/purchaseChart'
import mapChart from '../buyer/components/mapChart'
import cloudInfo from '@/assets/images/cloud-info@2x.png'
export default {
  name: 'ChartImage',
  components: {
    supplyChart,
    purchaseChart,
    mapChart
  },
  data () {
    return {
      imgUrl: cloudInfo,
      height: '156px'
    }
  },
  computed: {
    ...mapGetters(['device'])
  },
  methods: {

  }
}
</script>
<style lang="scss" scoped>
.chart-wrapper {
  // height: 424px;
  &.outerWrap{
    border-radius: 4px;
    overflow: hidden;
  }
  .chart-left {
    border-right: 1px solid #E8E9EA;
    box-sizing: border-box;
  }
  .chart-right {
    box-sizing: border-box;
  }
}
.chart-supply,.chart-purchase,.map-chart {
  background: #fff;
  box-sizing: border-box;
}
.chart-supply {
  border-bottom:1px solid #E8E9EA;
}
.header {
  width:100%;
  height:56px;
  display:flex;
  align-items:center;
  background:#fff;
  padding-left:24px;
  &.analyse {
    justify-content: space-between;
    padding-right: 16px;
    border-bottom: 1px solid #E8E9EA;
    padding-left: 16px;
    .more {
      font-size: 12px;
      color:#96999C;
      cursor: pointer;
    }
    .title {
      font-size: 16px;
      height: 24px;
      line-height: 24px;
    }
  }
  .title {
    font-size: 14px;
    color: #161C24;
    line-height: 22px;
    font-weight: bold;
    height:22px;
  }
  img {
    width:16px;
    height:16px;
    margin-left:8px;
  }
}
</style>
