<template>
  <Qa :sou-type="SOU_TYPE_ENUM.BID" />
</template>

<script>
/**
 * 招标 - 质疑澄清
 */
import { SOU_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Qa from './qa'

export default {
  name: 'BiddingQa',

  components: { Qa },

  data () {
    return {
      SOU_TYPE_ENUM
    }
  }
}
</script>
