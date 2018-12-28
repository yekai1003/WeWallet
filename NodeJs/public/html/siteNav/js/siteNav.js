$(document).ready(function () {

    $.ajax({
        url: "http://d.wn51.com/browser/qingdu/urlnav.json",
        type: 'get',
        cache: false,
        data: '',
        async: true,
        dataType: 'text',
        crossDomain: false,
        beforeSend: function () {

        },
        success: function (data) {
            try {
                data = $.parseJSON(data);
                var oneFlag = 1;
                for (var dk in data) {
                    var dak = data[dk];
                    var nav = '<li vid="t' + oneFlag + '">' +
                        '<button class="stable" hidefocus="true">' +
                        '<span class="img ' + dak.symbol + '"></span>' +
                        '<span class="title">' + dak.name + '</span>' +
                        '</button>' +
                        '</li>';
                    $(nav).appendTo($('.nav'));

                    var onSwitch = '';
                    if (oneFlag == 1) {
                        onSwitch = 'on';
                    }
                    var urllList = '<ul id="t' + oneFlag + '" class="' + onSwitch + '">';
                    for (var ck in dak['children']) {
                        var dck = dak['children'][ck];

                        var liList = '<li>' +
                            '<a href="' + dck.link + '" target="_blank">' +
                            '<img src="' + dck.icon_cdn + '">' +
                            '<span class="title">' + dck.name + '</span>' +
                            '</a>' +
                            '</li>';
                        urllList += liList;
                    }
                    urllList += '</ul>';

                    $(urllList).appendTo($('.list'));

                    oneFlag++;
                }

            } catch (e) {
            }

            clickSwitch();

            $('.list').find('a').click(function () {
                var url = $(this).attr('href');

                reportToSoftCenter('urlnav_click', url);
                console.log(123)

                window.setTimeout(function () {
                    window.open(url);
                }, 300);

                return false;
            });

        },

        complete: function () {
        },
        error: function () {
        }
    });

});

function clickSwitch() {
    var preObject = $('#site-list .bg-0');
    var target = getTarget();
    $("#baseTarget").attr("target", target > 0 ? "_blank" : "_top");
    $('#site-list .nav li').click(function (e) {
        $('#site-list .list ul').removeClass('on');
        $('#' + $(this).attr('vid')).addClass('on');
        //$('#site-list .sepr').removeClass().addClass('sepr bg-' + $(this).index());
        preObject.css('display', 'none');
        preObject = $('#site-list .bg-' + $(this).index());
        preObject.css('display', 'block');
    });
}

function getTarget() {
    var url = location.search;
    var Request = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1) //..?.
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            Request[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return parseInt(Request["t"]);
}
