<template>
  <span
    v-if="!Array.isArray(text)"
    :data-key="code"
  >{{ text }}</span>
  <span
    v-else
    :data-key="code"
    class="z-reference"
    :style="widthStyle"
  >
    <span v-if="!showBrLine">
      <span v-for="(name, index) in text" :key="name">
        {{ getText(name, index) }}
      </span>
    </span>
    <p
      v-for="(name, index) in text"
      v-else
      :key="name"
      style="margin: 0"
    >
      {{ getText(name, index) }}
      <template v-if="showBrLine">
        <br><br><br>
      </template>
    </p>
  </span>
</template>
<script>
import minix from './minix'
import toLocalUpperCase from './toLocalUpperCase'
import { isIE, isIE11 } from 'lib@/utils/validate'

export default {
  name: 'ZReference',
  mixins: [minix],
  data () {
    return { isParty: false }
  },
  computed: {
    showBrLine () {
      return this.componentInfo.key === 'stampParty'
    },
    widthStyle () {
      const style = {}
      // if (this.isParty) {
      //   const textWidths = this.text.map((name, index) =>
      //     this.getTextWidth(this.getText(name, index))
      //   );
      //   style.width = Math.max(...textWidths) + "px";
      // }
      return style
    },
    text () {
      const { key, dictionaryCode } = this.componentInfo
      // console.log(key);
      if (key === 'amountUpperCase') {
        const lowerCase = this.context.mergeForm.includeTaxAmount
        const upperCase = toLocalUpperCase(lowerCase)
        return upperCase
      }
      console.log(this.context.partnerData)
      try {
        this.context.partnerData.forEach(datas => {
          if (datas.partnerType == '甲方') {
            this.context.mergeForm.partyContacts = datas.contactName
            this.context.mergeForm.partyPhone = datas.phone
            this.context.mergeForm.partyAddress = datas.address
            this.context.mergeForm.partyFax = datas.fax
            this.context.mergeForm.partyTaxpayer = datas.taxPayer
            this.context.mergeForm.partyBank = datas.bankName
            this.context.mergeForm.partyBankAccount = datas.bankAccount
            this.context.mergeForm.partyTax = datas.taxNumber
          } else if (datas.partnerType == '乙方') {
            this.context.mergeForm.secondPartyContacts = datas.contactName
            this.context.mergeForm.secondPartyPhone = datas.phone
            this.context.mergeForm.secondPartyAddress = datas.address
            this.context.mergeForm.secondPartyFax = datas.fax
            this.context.mergeForm.secondPartyTaxpayer = datas.taxPayer
            this.context.mergeForm.secondPartyBank = datas.bankName
            this.context.mergeForm.secondPartyBankAccount = datas.bankAccount
            this.context.mergeForm.secondPartyTax = datas.taxNumber
          } else if (datas.partnerType == '丙方') {
            this.context.mergeForm.thirdPartyContacts = datas.contactName
            this.context.mergeForm.thirdPartyPhone = datas.phone
            this.context.mergeForm.thirdPartyAddress = datas.address
            this.context.mergeForm.thirdPartyFax = datas.fax
            this.context.mergeForm.thirdPartyTaxpayer = datas.taxPayer
            this.context.mergeForm.thirdPartyBank = datas.bankName
            this.context.mergeForm.thirdPartyBankAccount = datas.bankAccount
            this.context.mergeForm.thirdPartyTax = datas.taxNumber
          }
        })
      } catch (e) {}

      if (['party', 'stampParty', 'secondParty', 'thirdParty'].includes(key)) {
        const map = {
          party: '甲方',
          stampParty: '甲方',
          secondParty: '乙方',
          thirdParty: '丙方'
        }
        console.log(this.context.partnerData)

        let texts = this.context.partnerData
          .filter(i => i.partnerType === map[key])
          .map(i => i.partnerName)
        // this.isParty = map[key] === "甲方";
        // const attr1 = "['甲方一', '甲方二', '甲方三', '甲方四', '甲方五']";

        if (texts[2] == '甲方三') {
          texts = []
        }
        // console.log(texts);
        return texts
      }
      const value = this.context.mergeForm[key]
      const text = dictionaryCode ? this.$getDictLabel(dictionaryCode, value) : value
      console.log('[text]', text)
      return text
    }
  },
  created () {
  },
  methods: {
    removeElement (_element) {
      if (isIE11() || isIE()) {
        _element.removeNode(true)
      } else {
        const _parentElement = _element.parentNode
        if (_parentElement) {
          // console.log('[有父节点]', _element)
          _parentElement.removeChild(_element)
        }
        // console.log('[无父节点]', _element)
      }
    },
    getTextWidth (text) {
      let width = 0
      let html = document.createElement('span')
      html.innerText = text
      html.className = 'getTextWidth'
      document.querySelector('body').appendChild(html)
      width = document.querySelector('.getTextWidth').offsetWidth
      // document.querySelector(".getTextWidth").remove();
      const geTexttWidths = document.querySelector('.getTextWidth')
      if (geTexttWidths.length) {
        Array.from(geTexttWidths).forEach(i => this.removeElement(i))
      } else {
        console.log('[geTexttWidths]', geTexttWidths)
        this.removeElement(geTexttWidths)
      }
      console.log(`[${text}]: ${width}`)
      return `${width}`
    },
    getText (name, index) {
      // if (this.isParty) {
      //   let showIndex;
      //   if (this.text.length === 1) {
      //     showIndex = false;
      //   }
      //   if (this.text.length > 1) {
      //     showIndex = true;
      //   }
      //   const prefix = showIndex ? `甲方${index + 1}` : "甲方";
      //   return `${prefix}：${name}`;
      // }
      return name
    }
  }
}
</script>
<style>
.z-reference {
  width: 200px;
  vertical-align: top;
  display: inline-block;
}
</style>
