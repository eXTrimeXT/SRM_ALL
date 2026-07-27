<template>
  <Qa :souType="SOU_TYPE_ENUM.BRG" />
</template>

<script>
/**
 * 项目式询价 - 质疑澄清
 */
import { SOU_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Qa from 'lib@/compositionEngine/qa'

export default {
  name: 'BargainQa',

  components: { Qa },

  data () {
    return {
      SOU_TYPE_ENUM
    }
  }
}
</script>
