<template>
  <Qa :souType="SOU_TYPE_ENUM.BID" />
</template>

<script>
/**
 * 招标 - 质疑澄清
 */
import { SOU_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Qa from 'lib@/compositionEngine/qa'

export default {
  name: 'BiddingVendorQa',

  components: { Qa },

  data () {
    return {
      SOU_TYPE_ENUM
    }
  }
}
</script>
