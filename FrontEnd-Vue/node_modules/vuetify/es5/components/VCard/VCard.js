'use strict';

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; // Styles

// Mixins

// Helpers


require('../../../src/stylus/components/_cards.styl');

var _colorable = require('../../mixins/colorable');

var _colorable2 = _interopRequireDefault(_colorable);

var _measurable = require('../../mixins/measurable');

var _measurable2 = _interopRequireDefault(_measurable);

var _routable = require('../../mixins/routable');

var _routable2 = _interopRequireDefault(_routable);

var _themeable = require('../../mixins/themeable');

var _themeable2 = _interopRequireDefault(_themeable);

var _helpers = require('../../util/helpers');

var _mixins = require('../../util/mixins');

var _mixins2 = _interopRequireDefault(_mixins);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/* @vue/component */
exports.default = (0, _mixins2.default)(_colorable2.default, _measurable2.default, _routable2.default, _themeable2.default).extend({
    name: 'v-card',
    props: {
        flat: Boolean,
        hover: Boolean,
        img: String,
        raised: Boolean,
        tag: {
            type: String,
            default: 'div'
        },
        tile: Boolean
    },
    computed: {
        classes: function classes() {
            return _extends({
                'v-card': true,
                'v-card--flat': this.flat,
                'v-card--hover': this.hover,
                'v-card--raised': this.raised,
                'v-card--tile': this.tile
            }, this.themeClasses);
        },
        styles: function styles() {
            var style = {
                height: (0, _helpers.convertToUnit)(this.height)
            };
            if (this.img) {
                style.background = 'url("' + this.img + '") center center / cover no-repeat';
            }
            if (this.height) style.height = (0, _helpers.convertToUnit)(this.height);
            if (this.maxHeight) style.maxHeight = (0, _helpers.convertToUnit)(this.maxHeight);
            if (this.maxWidth) style.maxWidth = (0, _helpers.convertToUnit)(this.maxWidth);
            if (this.width) style.width = (0, _helpers.convertToUnit)(this.width);
            return style;
        }
    },
    render: function render(h) {
        var _generateRouteLink = this.generateRouteLink(this.classes),
            tag = _generateRouteLink.tag,
            data = _generateRouteLink.data;

        data.style = this.styles;
        return h(tag, this.setBackgroundColor(this.color, data), this.$slots.default);
    }
});
//# sourceMappingURL=VCard.js.map