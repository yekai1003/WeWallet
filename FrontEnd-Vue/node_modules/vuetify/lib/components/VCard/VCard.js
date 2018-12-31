var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

// Styles
import '../../../src/stylus/components/_cards.styl';
// Mixins
import Colorable from '../../mixins/colorable';
import Measurable from '../../mixins/measurable';
import Routable from '../../mixins/routable';
import Themeable from '../../mixins/themeable';
// Helpers
import { convertToUnit } from '../../util/helpers';
import mixins from '../../util/mixins';
/* @vue/component */
export default mixins(Colorable, Measurable, Routable, Themeable).extend({
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
                height: convertToUnit(this.height)
            };
            if (this.img) {
                style.background = 'url("' + this.img + '") center center / cover no-repeat';
            }
            if (this.height) style.height = convertToUnit(this.height);
            if (this.maxHeight) style.maxHeight = convertToUnit(this.maxHeight);
            if (this.maxWidth) style.maxWidth = convertToUnit(this.maxWidth);
            if (this.width) style.width = convertToUnit(this.width);
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