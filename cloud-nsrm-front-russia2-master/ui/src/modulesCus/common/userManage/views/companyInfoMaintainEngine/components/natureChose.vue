<template>
  <div class="box-warp">
    <div
      v-for="item in overseasRelationArr"
      class="box-one"
      :class="{'clickClass':value == item.value}"
      @click="clickOne(item.value)"
    >
      <img v-if="item.value == 'INSIDE'" src="../img/abroad.svg" alt="">
      <img v-if="item.value == 'OUT'" src="../img/abroad.svg" alt="">
      <img v-if="item.value == 'PERSONAL'" src="../img/person.svg" alt="">
      <div class="box-center">
        <div class="blueOne">
          {{ item.label }}
        </div>
        <div class="comments">
          {{ item.description }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { DictClass } from '@/library/utils/dict/dict-utils'

export default {
  components: {},
  props: {
    value: {
      type: String
    }
  },
  data () {
    return {
      // choseWhat: 'INSIDE'
    }
  },
  computed: {
    overseasRelationArr () {
      let res = DictClass.getDict('RELATION')
      if (!res) {
        DictClass.loadDictionary('RELATION')
      }
      return DictClass.getDict('RELATION') || []
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
