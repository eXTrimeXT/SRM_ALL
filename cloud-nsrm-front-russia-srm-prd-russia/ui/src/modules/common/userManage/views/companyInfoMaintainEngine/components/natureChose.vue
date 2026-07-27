<template>
  <div class="box-warp">
    <div class="box-one" style="margin-left: 0" :class="{'clickClass':value=='INSIDE'}" @click="clickOne('INSIDE')">
      <img src="../img/domestic.svg" alt="">
      <div class="box-center">
        <div class="blueOne">境内企业</div>
        <div class="comments">境内企业是指在国内指在中国境内依法设立的企业，包括外商投资外商独资企业外商投资企业境内投资。</div>
      </div>
    </div>
    <div class="box-one" :class="{'clickClass':value=='OUT'}" @click="clickOne('OUT')">
      <img src="../img/abroad.svg" alt="">
      <div class="box-center">
        <div class="blueOne">境外企业</div>
        <div class="comments">境外企业是指在中国境外依法设立的企业，不是依据我国的法律设立的一般都是境外企业。</div>
      </div>
    </div>
    <div class="box-one" :class="{'clickClass':value=='PERSONAL'}" @click="clickOne('PERSONAL')">
      <img src="../img/person.svg" alt="">
      <div class="box-center">
        <div class="blueOne">个人</div>
        <div class="comments">个体户一般指个体工商户。个体工商户是指在法律允许的范围内，依法经核准登记，从事工商经营活动的自然人或家庭。</div>
      </div>
    </div>
  </div>
</template>
<script>

export default {
  components: {},
  data () {
    return {
      // choseWhat: 'INSIDE'
    }
  },
  props: {
    value: {
      type: String
    }
  },
  watch: {
    value: {
      handler () {
        console.log(this.value, 'value')
      },
      deep: true
    }
  },
  computed: {

  },
  async created () {

  },
  mounted () {

  },
  updated () {

  },
  methods: {
    clickOne (how) {
      this.choseWhat = how
      this.$emit('change', how)
    }
  }
}
</script>
<style lang="scss" scoped>
.box-warp{
  display: flex;
  .box-one{
    width: 32%;
    height: 116px;
    background: #FFFFFF;
    border: 1px solid #B9BABD;
    border-radius: 2px;
    margin: 0 1%;
    cursor: pointer;
    display: flex;
    .box-center{
      margin: 16px 27px 0 13px;
      .blueOne{
        font-size: 16px;
        color: #161C24;
        line-height: 24px;
        font-weight: 500;
      }
      .comments{
        font-size: 12px;
        color: #73777C;
        line-height: 20px;
      }
    }
    img{
      width: 48px;
      height: 42px;
      margin: 16px 0 0 16px;
    }
    .title{
      font-size: 20px;
    }
  }
  .clickClass{
    background-image: url('../img/frame.svg');
    background-repeat: no-repeat;
    background-position:right bottom;
    //background-size:109%;
    border: 1px solid #0077FF;
    .blueOne{
      color: #0077FF !important;
    }
  }
}
</style>
