<template>
  <div class="changeTitle changeTitleTop">
    <i />{{ $t(language) }}
  </div>
</template>

<script>
export default {
  name: 'changeTitle',
  props: {
    language: {
      // 场景编码 AUTH:供应商评审,SAMPLE:样品确认,MATERIAL:物料试用,QUA:资质审查
      type: String,
      default: () => ''
    }
  }
}
</script>

<style scoped>
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:10px;
  font-weight: 400;
}
.changeTitleTop{
  margin-top: 10px;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
</style>
