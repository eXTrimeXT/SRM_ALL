<template>
  <div class="box-warp">
    <div class="box-one" style="margin-left: 0" :class="{'clickClass':value=='INSIDE'}" @click="clickOne('INSIDE')">
      <img src="../img/domestic.svg" alt="">
      <div class="box-center">
        <div class="blueOne">{{ $t('registered.typeInner') }}</div>
        <div class="comments">{{ $t('registered.typeInnerDes') }}</div>
      </div>
    </div>
    <div class="box-one" :class="{'clickClass':value=='OUT'}" @click="clickOne('OUT')">
      <img src="../img/abroad.svg" alt="">
      <div class="box-center">
        <div class="blueOne">{{ $t('registered.typeOuter') }}</div>
        <div class="comments">{{ $t('registered.typeOuterDes') }}</div>
      </div>
    </div>
    <div class="box-one" :class="{'clickClass':value=='PERSONAL'}" @click="clickOne('PERSONAL')">
      <img src="../img/person.svg" alt="">
      <div class="box-center">
        <div class="blueOne">{{ $t('registered.typePerson') }}</div>
        <div class="comments">{{ $t('registered.typePersonDes') }}</div>
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
