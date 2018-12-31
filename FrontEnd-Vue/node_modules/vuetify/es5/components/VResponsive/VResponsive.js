'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

require('../../../src/stylus/components/_responsive.styl');

var _measurable = require('../../mixins/measurable');

var _measurable2 = _interopRequireDefault(_measurable);

var _mixins = require('../../util/mixins');

var _mixins2 = _interopRequireDefault(_mixins);

var _helpers = require('../../util/helpers');

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/* @vue/component */

// Utils
exports.default = (0, _mixins2.default)(_measurable2.default).extend({
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
                height: (0, _helpers.convertToUnit)(this.height),
                maxHeight: (0, _helpers.convertToUnit)(this.maxHeight),
                maxWidth: (0, _helpers.convertToUnit)(this.maxWidth),
                width: (0, _helpers.convertToUnit)(this.width)
            },
            on: this.$listeners
        }, [this.__cachedSizer, this.genContent()]);
    }
});
// Mixins
//# sourceMappingURL=VResponsive.js.map