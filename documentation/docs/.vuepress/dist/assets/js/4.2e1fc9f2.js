(window.webpackJsonp=window.webpackJsonp||[]).push([[4],{114:function(t,n,r){},237:function(t,n,r){"use strict";var e=r(114);r.n(e).a},243:function(t,n,r){"use strict";r.r(n);r(71);var e={name:"token",data:function(){return{addr:"",showRes:!1,tokenRet:!1,i18n:{title:"Receive 10,000 Test (TRX) Coins",placeholder:"Test wallet address",submit:"SUBMIT",submitDone:"Submitted successfully",submitError:"Incorrect address or reach daily limit"}}},mounted:function(){0==this.$route.path.indexOf("/zh/")&&(this.i18n={title:"获取10000测试币",placeholder:"测试网钱包地址",submit:"提交",submitDone:"提交成功",submitError:"地址错误或达到每日获取上限"})},methods:{token:function(){var t=this,n="";this.$axios.post("".concat(n,"/sunnetwork/token"),{addr:this.addr}).then((function(n){t.tokenRet=n.data.ok,t.showRes=!0})).catch((function(n){t.tokenRet=!1,t.showRes=!0}))}}},o=(r(237),r(1)),i=Object(o.a)(e,(function(){var t=this,n=t.$createElement,r=t._self._c||n;return r("div",{staticClass:"token"},[r("div",{staticClass:"row"},[r("h3",{staticClass:"tokenLabel"},[t._v(t._s(t.i18n.title))]),t._v(" "),r("VueInput",{staticClass:"big addr",attrs:{placeholder:t.i18n.placeholder},model:{value:t.addr,callback:function(n){t.addr=n},expression:"addr"}}),t._v(" "),r("VueButton",{staticClass:"primary big",attrs:{label:t.i18n.submit},on:{click:t.token}}),t._v(" "),t.showRes?r("div",{staticClass:"tokenRet"},[r("VueIcon",{attrs:{icon:t.tokenRet?"done":"error"}}),t._v("\n      "+t._s(t.tokenRet?t.i18n.submitDone:t.i18n.submitError)+"\n    ")],1):t._e()],1)])}),[],!1,null,"53a16544",null);n.default=i.exports},35:function(t,n){var r=t.exports="undefined"!=typeof window&&window.Math==Math?window:"undefined"!=typeof self&&self.Math==Math?self:Function("return this")();"number"==typeof __g&&(__g=r)},36:function(t,n,r){var e=r(35),o=r(42),i=r(40),u=r(47),c=r(58),a=function(t,n,r){var s,f,l,p,d=t&a.F,v=t&a.G,h=t&a.S,y=t&a.P,x=t&a.B,b=v?e:h?e[n]||(e[n]={}):(e[n]||{}).prototype,m=v?o:o[n]||(o[n]={}),_=m.prototype||(m.prototype={});for(s in v&&(r=n),r)l=((f=!d&&b&&void 0!==b[s])?b:r)[s],p=x&&f?c(l,e):y&&"function"==typeof l?c(Function.call,l):l,b&&u(b,s,l,t&a.U),m[s]!=l&&i(m,s,p),y&&_[s]!=l&&(_[s]=l)};e.core=o,a.F=1,a.G=2,a.S=4,a.P=8,a.B=16,a.W=32,a.U=64,a.R=128,t.exports=a},37:function(t,n){t.exports=function(t){try{return!!t()}catch(t){return!0}}},38:function(t,n){t.exports=function(t){return"object"==typeof t?null!==t:"function"==typeof t}},39:function(t,n,r){t.exports=!r(37)((function(){return 7!=Object.defineProperty({},"a",{get:function(){return 7}}).a}))},40:function(t,n,r){var e=r(46),o=r(57);t.exports=r(39)?function(t,n,r){return e.f(t,n,o(1,r))}:function(t,n,r){return t[n]=r,t}},42:function(t,n){var r=t.exports={version:"2.6.9"};"number"==typeof __e&&(__e=r)},43:function(t,n,r){var e=r(38);t.exports=function(t){if(!e(t))throw TypeError(t+" is not an object!");return t}},44:function(t,n){t.exports=function(t){if(null==t)throw TypeError("Can't call method on  "+t);return t}},45:function(t,n){var r=Math.ceil,e=Math.floor;t.exports=function(t){return isNaN(t=+t)?0:(t>0?e:r)(t)}},46:function(t,n,r){var e=r(43),o=r(61),i=r(63),u=Object.defineProperty;n.f=r(39)?Object.defineProperty:function(t,n,r){if(e(t),n=i(n,!0),e(r),o)try{return u(t,n,r)}catch(t){}if("get"in r||"set"in r)throw TypeError("Accessors not supported!");return"value"in r&&(t[n]=r.value),t}},47:function(t,n,r){var e=r(35),o=r(40),i=r(48),u=r(56)("src"),c=r(68),a=(""+c).split("toString");r(42).inspectSource=function(t){return c.call(t)},(t.exports=function(t,n,r,c){var s="function"==typeof r;s&&(i(r,"name")||o(r,"name",n)),t[n]!==r&&(s&&(i(r,u)||o(r,u,t[n]?""+t[n]:a.join(String(n)))),t===e?t[n]=r:c?t[n]?t[n]=r:o(t,n,r):(delete t[n],o(t,n,r)))})(Function.prototype,"toString",(function(){return"function"==typeof this&&this[u]||c.call(this)}))},48:function(t,n){var r={}.hasOwnProperty;t.exports=function(t,n){return r.call(t,n)}},50:function(t,n,r){var e=r(64),o=r(44);t.exports=function(t){return e(o(t))}},51:function(t,n){var r={}.toString;t.exports=function(t){return r.call(t).slice(8,-1)}},52:function(t,n,r){var e=r(45),o=Math.min;t.exports=function(t){return t>0?o(e(t),9007199254740991):0}},53:function(t,n,r){"use strict";var e=r(37);t.exports=function(t,n){return!!t&&e((function(){n?t.call(null,(function(){}),1):t.call(null)}))}},55:function(t,n,r){var e=r(42),o=r(35),i=o["__core-js_shared__"]||(o["__core-js_shared__"]={});(t.exports=function(t,n){return i[t]||(i[t]=void 0!==n?n:{})})("versions",[]).push({version:e.version,mode:r(60)?"pure":"global",copyright:"© 2019 Denis Pushkarev (zloirock.ru)"})},56:function(t,n){var r=0,e=Math.random();t.exports=function(t){return"Symbol(".concat(void 0===t?"":t,")_",(++r+e).toString(36))}},57:function(t,n){t.exports=function(t,n){return{enumerable:!(1&t),configurable:!(2&t),writable:!(4&t),value:n}}},58:function(t,n,r){var e=r(69);t.exports=function(t,n,r){if(e(t),void 0===n)return t;switch(r){case 1:return function(r){return t.call(n,r)};case 2:return function(r,e){return t.call(n,r,e)};case 3:return function(r,e,o){return t.call(n,r,e,o)}}return function(){return t.apply(n,arguments)}}},59:function(t,n,r){var e=r(50),o=r(52),i=r(70);t.exports=function(t){return function(n,r,u){var c,a=e(n),s=o(a.length),f=i(u,s);if(t&&r!=r){for(;s>f;)if((c=a[f++])!=c)return!0}else for(;s>f;f++)if((t||f in a)&&a[f]===r)return t||f||0;return!t&&-1}}},60:function(t,n){t.exports=!1},61:function(t,n,r){t.exports=!r(39)&&!r(37)((function(){return 7!=Object.defineProperty(r(62)("div"),"a",{get:function(){return 7}}).a}))},62:function(t,n,r){var e=r(38),o=r(35).document,i=e(o)&&e(o.createElement);t.exports=function(t){return i?o.createElement(t):{}}},63:function(t,n,r){var e=r(38);t.exports=function(t,n){if(!e(t))return t;var r,o;if(n&&"function"==typeof(r=t.toString)&&!e(o=r.call(t)))return o;if("function"==typeof(r=t.valueOf)&&!e(o=r.call(t)))return o;if(!n&&"function"==typeof(r=t.toString)&&!e(o=r.call(t)))return o;throw TypeError("Can't convert object to primitive value")}},64:function(t,n,r){var e=r(51);t.exports=Object("z").propertyIsEnumerable(0)?Object:function(t){return"String"==e(t)?t.split(""):Object(t)}},68:function(t,n,r){t.exports=r(55)("native-function-to-string",Function.toString)},69:function(t,n){t.exports=function(t){if("function"!=typeof t)throw TypeError(t+" is not a function!");return t}},70:function(t,n,r){var e=r(45),o=Math.max,i=Math.min;t.exports=function(t,n){return(t=e(t))<0?o(t+n,0):i(t,n)}},71:function(t,n,r){"use strict";var e=r(36),o=r(59)(!1),i=[].indexOf,u=!!i&&1/[1].indexOf(1,-0)<0;e(e.P+e.F*(u||!r(53)(i)),"Array",{indexOf:function(t){return u?i.apply(this,arguments)||0:o(this,t,arguments[1])}})}}]);