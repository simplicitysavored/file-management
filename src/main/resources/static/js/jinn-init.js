/**
 * 转自：http://blog.ttionya.com/article-1511.html
 */
;(function () {
    if (typeof window.CustomEvent === "function") return false;

    function CustomEvent(event, params) {
        params = params || {bubbles: false, cancelable: false, detail: undefined};
        var evt = document.createEvent('CustomEvent');
        evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
        return evt;
    }

    CustomEvent.prototype = window.Event.prototype;

    window.CustomEvent = CustomEvent;
})();
;(function () {
    function ajaxEventTrigger(event) {
        var ajaxEvent = new CustomEvent(event, {detail: this});
        window.dispatchEvent(ajaxEvent);
    }

    var oldXHR = window.XMLHttpRequest;

    function newXHR() {
        var realXHR = new oldXHR();

        realXHR.addEventListener('abort', function () {
            ajaxEventTrigger.call(this, 'ajaxAbort');
        }, false);

        realXHR.addEventListener('error', function () {
            ajaxEventTrigger.call(this, 'ajaxError');
        }, false);

        realXHR.addEventListener('load', function () {
            ajaxEventTrigger.call(this, 'ajaxLoad');
        }, false);

        realXHR.addEventListener('loadstart', function () {
            ajaxEventTrigger.call(this, 'ajaxLoadStart');
        }, false);

        realXHR.addEventListener('progress', function () {
            ajaxEventTrigger.call(this, 'ajaxProgress');
        }, false);

        realXHR.addEventListener('timeout', function () {
            ajaxEventTrigger.call(this, 'ajaxTimeout');
        }, false);

        realXHR.addEventListener('loadend', function () {
            ajaxEventTrigger.call(this, 'ajaxLoadEnd');
        }, false);

        realXHR.addEventListener('readystatechange', function () {
            ajaxEventTrigger.call(this, 'ajaxReadyStateChange');
        }, false);

        return realXHR;
    }

    window.XMLHttpRequest = newXHR;
})();
let xhr = new XMLHttpRequest();


window.addEventListener('ajaxReadyStateChange', function (e) {
    /*
        XHR.readyState == 状态（0，1，2，3，4）
            0：请求未初始化，还没有调用 open()。
            1：请求已经建立，但是还没有发送，还没有调用 send()。
            2：请求已发送，正在处理中（通常现在可以从响应中获取内容头）。
            3：请求在处理中；通常响应中已有部分数据可用了，没有全部完成。
            4：响应已完成；您可以获取并使用服务器的响应了。
     */
    let XHR = e.detail;
    if (XHR.readyState === 1) {
        /*
         * XMLHttpRequest.setRequestHeader() 是设置HTTP请求头部的方法。
         * 此方法必须在  open() 方法和 send()   之间调用。
         * 如果多次对同一个请求头赋值，只会生成一个合并了多个值的请求头。
         */
        XHR.setRequestHeader('token', localStorage.getItem('token'));
    }
    console.log('ajaxReadyStateChange',e.detail); // XMLHttpRequest Object
});

window.addEventListener('ajaxAbort', function (e) {
    console.log('ajaxAbort',e.detail.responseText); // XHR 返回的内容
});

let JINN_INIT = {};
JINN_INIT.jumpPage = function (param) {
    param = param || {url: '/', method: 'get', data: {}};
    param.url = param.url || "/";
    param.method = param.method || "get";
    param.data = param.data || {};

    // xhr.open(param.method, param.url);
    // xhr.send();

    let formBody = '<form id="jumpPageForm" action="'+param.url+'" method="'+param.method+'" style="display: none;">\n';
    formBody += '<input type="hidden" name="token" value="'+localStorage.getItem('token')+'" />\n';
    for (let dataKey in param.data) {
        formBody += '<input type="hidden" name="'+dataKey+'" value="'+param.data[dataKey]+'" />\n';
    }
    formBody += '</form>';


    $('body').append(formBody);

    // $('#jumpPageForm').click();

    $('#jumpPageForm').submit();
};