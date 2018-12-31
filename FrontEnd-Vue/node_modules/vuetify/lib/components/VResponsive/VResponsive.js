import '../../../src/stylus/components/_responsive.styl';
// Mixins
import Measurable from '../../mixins/measurable';
// Utils
import mixins from '../../util/mixins';
import { convertToUnit } from '../../util/helpers';
/* @vue/component */
export default mixins(Measurable).extend({
    name: 'v-responsive',
    props: {
        aspectRatio: [String, Number]
    },
    computed: {
        computedAspectRatio: function computedAspectRatio() {
            return Number(this.aspectRatio);
        },
        aspectStyle: function aspectStyle() {
            return this.computedAspectRatio ? { paddingBottom: 1 / this.computedAspectRatio * 100 + '%' } : undefined;
        },
        __cachedSizer: function __cachedSizer() {
            if (!this.aspectStyle) return [];
            return this.$createElement('div', {
                style: this.aspectStyle,
                staticClass: 'v-responsive__sizer'
            });
        }
    },
    methods: {
        genContent: function genContent() {
            return this.$createElement('div', {
                staticClass: 'v-responsive__content'
            }, this.$slots.default);
        }
    },
    render: function render(h) {
        return h('div', {
            staticClass: 'v-responsive',
            style: {
                height: convertToUnit(this.height),
                maxHeight: convertToUnit(this.maxHeight),
                maxWidth: convertToUnit(this.maxWidth),
                width: convertToUnit(this.width)
            },
            on: this.$listeners
        }, [this.__cachedSizer, this.genContent()]);
    }
});
//# sourceMappingURL=VResponsive.js.map