import { defineComponent, getCurrentInstance } from 'vue-demi'
import {
    h,
    useField
} from '@meicloud/render-engine'

export const transformComponent = (name: any, Component: any, refName?: any) => {
    return defineComponent({
        name,
        setup (_, { attrs, listeners, slots }) {
            const field = useField()
            const currentInstance = getCurrentInstance()
            const ref = refName ?? name
            field.value.inject({ currentInstance: () => currentInstance?.proxy.$refs[ref] })
            return () => {
                return h(Component, {
                    attrs,
                    on: listeners,
                    ref
                }, slots)
            }
        }
    })
}
