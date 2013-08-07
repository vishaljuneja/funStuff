define(["jquery","underscore","js/lib/moment","js/lib/cookie","js/core/coursera"],function(u,e,t,r,a){function n(u,e){404==u?a.router.trigger("error",404,e,{message:"this page does not exist"}):500==u?a.router.trigger("error",500,e,{message:"backend server has seen an error"}):401==u?a.router.navigate("/account/signin?r="+e,{trigger:!0,replace:!0}):403==u&&a.router.trigger("error",403,e,{message:"you do not have permission to view this page"})}function s(u){if(u){var e=/(?:https?:\/\/)?((?:[\d\w\-\.]+?\.)?[\w\d]+(?:\.[\w]+)?)(\/.*)?$/,t=e.exec(u),r=(t&&t.length?t[1]:"").split("."),n=e.exec(location.host)[1].split(".");r&&r.length&&r[r.length-1]==n[n.length-1]&&r[r.length-2]==n[n.length-2]?location.href=u:/^\/.*/.test(u)?location.href=u:a.router.navigate(a.config.dir.home,!0)}}function o(e,t){var r=e.find('[data-section="'+t+'"]');if(r.length){var n=r.position().top,s=Math.abs(u(document).scrollTop()-n);s>1e3?u("html,body").scrollTop(n):u("html,body").animate({scrollTop:n}),a.multitracker&&a.multitracker.push([document.title.split(" |")[0]+" "+t,"Open"])}}function i(u){u.bind("view:appended",function(){o(u.$el,this.options.section)}),u.$el.on("click","a.internal-page",function(e){if(!e.ctrlKey&&!e.metaKey){var t=e.currentTarget.pathname,r=t.substr(t.lastIndexOf("/")+1);u.$("[data-section="+r+"]").length&&(e.preventDefault(),o(u.$el,r),a.router.navigateTo(t,{trigger:!1}))}})}function E(u){return u.length>1?u.slice(0,-1).join(", ")+" and "+u[u.length-1]:""+u}function l(u,e){if(!u)return"";if(e>=u.length)return u;var t=e-3,r=u.lastIndexOf(" ",t);return(-1==r||e/2>r)&&(r=t),u.substr(0,r)+"..."}function c(u){return u.replace(/^http:\/\//i,"https://")}function g(u,e){e=e.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");var t="[\\?&]"+e+"=([^&#]*)",r=RegExp(t),a=r.exec(unescape(u));return null===a?null:a[1]}function d(u,e,t){var r=RegExp("([?&])"+e+"=.*(&|$)","i");return separator=-1!==u.indexOf("?")?"&":"?",u.match(r)?u.replace(r,"$1"+e+"="+t+"$2"):u+separator+e+"="+t}function f(t,r,n){function s(u){var e=c.attr("data-"+u+"-message");c.html(e)}function o(){var u=t.find('input[type="checkbox"]').filter("[required]");return u.length&&!u.is(":checked")?(c.attr("disabled","disabled"),void 0):(c.removeAttr("disabled"),s("default"),void 0)}function i(){l=t.serialize();var e=(r.type||"POST").toLowerCase();a.api[e]&&a.api[e](r.url,{data:r.data||t.serialize()||null,headers:r.headers||{},dataType:r.dataType||"json"}).done(function(u,e,a){r.success&&r.success(u,e,a),s("success"),g.html("(Saved!)"),A(t,null)}).fail(function(e,a,n){var s=u.parseJSON(e.responseText);r.error&&r.error(e,a,n),o(),t.find("input").off(".validate"),A(t,s)})}function E(){t.serialize()!=l&&(c.trigger("click"),g.html("(Saving...)"))}r=r||{};var l=t.serialize();r.url=r.url||t.attr("action"),r.type=r.type||t.attr("type");var c=t.find('button[type="submit"]'),g=u(c.attr("data-update"));t.find("input, select").on("change",o),t.find("input, textarea").on("keyup",o);var d=t.find("input, select");d.each(function(e,t){e!=d.length-1&&u(t).on("keypress",function(u){return 13!=u.keyCode})});var f="true"==t.attr("data-validate");if(f&&t.find("input").each(function(){var e=u(this);"checkbox"!=e.attr("type")&&(e.on("keyup.validate",function(){F(e)||b(e,null)}),e.on("blur.validate paste.validate",function(){b(e,F(e))}))}),c.on("click",function(u){u.preventDefault(),(!f||f&&!h(t))&&(s("inflight"),c.attr("disabled","disabled"),i(r))}),n){var p=e.debounce(E,1e3);t.find("input, select").on("change.autosave",p),t.find("input, textarea").on("keyup.autosave",p),t.find("input, select, blur").on("blur.autosave paste.autosave",E)}}function h(e){var t=null;if(e.find("input").each(function(){var e=u(this),r=F(e);b(e,r),t=t||r,r&&!t&&0===u("input:focus").length&&e.focus()}),t){var r=e.attr("data-invalid-message")||"Uh-oh, please check the form!";p(e,r),e.find('button[type="submit"]').animate({opacity:.4},100).animate({"margin-left":"-=10"},100).animate({"margin-left":"+=20"},100).animate({"margin-left":"-=20"},100).animate({"margin-left":"+=10"},100).animate({opacity:1},100)}return t}function F(u){var e=u.attr("type"),t=u.val(),r=u.attr("pattern"),a=null,n=null;return"email"==e?n=RegExp(/[a-zA-Z0-9!#$%&'*+\/=?\^_`{|}~\-]+(?:\.[a-zA-Z0-9!#$%&'*+\/=?\^_`{|}~\-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9\-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9\-]*[a-zA-Z0-9])?/):r&&(n=RegExp(r)),n&&(n.test(t)||(a=u.attr("data-invalid-message")||"Invalid field")),u.attr("required")&&("text"==e&&0===t.length&&(a="This field is required."),"checkbox"!=e||u.is(":checked")||(a="⬆ Please check this!")),a}function A(u,e){if(u.find(".form-errors").remove(),u.find(".error .help-inline").remove(),u.find(".error").removeClass("error"),e&&(e.error_message&&p(u,e.error_message),e.field_errors))for(var t in e.field_errors){var r=u.find('*[name="'+t+'"]');b(r,e.field_errors[t]),r.focus()}}function p(e,t){e.find(".form-errors").remove(),$errorsDom=u("<span></span>").addClass("form-errors").attr("role","alert").attr("aria-live","assertive").html(t),e.find(".actions").length?e.find(".actions").children(":last").after($errorsDom):e.find(".modal-footer").length?e.find(".modal-footer").children(":last").after($errorsDom):e.find('button[type="submit"]')&&e.find('button[type="submit"]').after($errorsDom)}function b(e,t){var r=e.parents(".control-mini-group").length&&e.parents(".control-mini-group")||e.parents(".control-group");if(r.removeClass("error"),r.find(".help-inline.error").remove(),t){var a=(e.attr("id")||"")+"-form-field-error-"+(new Date).getTime(),n=(e.attr("aria-describedby")||"").split(",");if(n.push(a),e.attr("aria-describedby",n.join(",")),$errorsDom=u("<span></span>").addClass("help-inline error").attr("role","alert").attr("aria-live","assertive").attr("id",a),t instanceof Array)for(var s=0;t.length>s;s++)$errorsDom.append("<span>"+t[s]+"</span>");else $errorsDom.append(t);r.addClass("error"),"checkbox"==e.attr("type")?r.find(".controls").append($errorsDom):e.after($errorsDom)}}function C(){var u=navigator.userAgent||navigator.vendor||window.opera;return/iPhone|iPod|iPad|Android|BlackBerry|Opera Mini|IEMobile/.test(u)}function v(){var u=navigator.userAgent||navigator.vendor||window.opera;return/iPhone|iPod|iPad/.test(u)}function m(){return"ontouchstart"in window||window.DocumentTouch&&document instanceof DocumentTouch}function D(){var u=document.createElement("canvas");return!(!u.getContext||!u.getContext("2d"))}function B(e,t,r){var a="meta["+e+"='"+t+"']",n=u(a);if(0===n.length){var s="<meta "+e+'="'+t+'" />';n=u(s).appendTo("head")}n.attr("content",r)}function y(){var e=window.innerHeight,t=document.compatMode;return(t||!u.support.boxModel)&&(e="CSS1Compat"==t?document.documentElement.clientHeight:document.body.clientHeight),e}function w(u,e){e=e||0;var t=y(),r=document.documentElement.scrollTop?document.documentElement.scrollTop:document.body.scrollTop,a=r-e,n=r+t+e,s=u.offset().top,o=u.height(),i=s+o;return n>i&&i>a}function k(u){var t=0,r=["cs-programming","cs-systems","cs-theory","cs-ai","cs","fmgb"];return u.each(function(u){u.get("topic")&&e.intersection(u.get("topic").get("category-ids"),r).length>0&&t++}),t}function x(){var e=u("[data-promo-id]");e.each(function(){var e=u(this);if(e.find(".close").length){var t=e.attr("data-promo-id");r.get(t)?e.hide():(e.show(),e.find(".close").on("click",function(){e.hide(),r.set(t,"closed")}))}})}function T(u){var e=t.unix(u.attr("datetime"));6e4>Math.abs(t().diff(e))?u.text("just now"):u.text(e.fromNow()),u.attr("title",e.format("dddd, MMMM Do YYYY, h:mm a Z"))}function j(u){return u=u.replace(/^.*:\/\//,""),u=u.replace(/\/$/,"")}function M(u){var t=u.text(),r=0,a=function(){u.prop("scrollHeight")>u.height()+6&&(r+=1,u.text(t.slice(0,t.length-r)),u.append("&hellip;"),e.defer(a))};e.defer(a)}function I(u){return u&&(u=u.replace(/((https?\:\/\/)|(www\.))(\S+)(\w{2,4})(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/gi,function(u){var e=u;return e.match("^https?://")||(e="http://"+e),'<a target="_blank" href="'+e+'">'+u+"</a>"})),u}function $(u,e,t){u/=255,e/=255,t/=255;var r,a,n=Math.max(u,e,t),s=Math.min(u,e,t),o=(n+s)/2;if(n==s)r=a=0;else{var i=n-s;switch(a=o>.5?i/(2-n-s):i/(n+s),n){case u:r=(e-t)/i+(t>e?6:0);break;case e:r=(t-u)/i+2;break;case t:r=(u-e)/i+4}r/=6}return[r,a,o]}function z(u,e,t){var r,a,n;if(0===e)r=a=n=t;else{var s=function(u,e,t){return 0>t&&(t+=1),t>1&&(t-=1),1/6>t?u+6*(e-u)*t:.5>t?e:2/3>t?u+6*(e-u)*(2/3-t):u},o=.5>t?t*(1+e):t+e-t*e,i=2*t-o;r=s(i,o,u+1/3),a=s(i,o,u),n=s(i,o,u-1/3)}return[Math.round(255*r),Math.round(255*a),Math.round(255*n)]}function _(u,e,t){u/=255,e/=255,t/=255;var r,a,n=Math.max(u,e,t),s=Math.min(u,e,t),o=n,i=n-s;if(a=0===n?0:i/n,n==s)r=0;else{switch(n){case u:r=(e-t)/i+(t>e?6:0);break;case e:r=(t-u)/i+2;break;case t:r=(u-e)/i+4}r/=6}return[r,a,o]}function S(u,e,t){var r,a,n,s=Math.floor(6*u),o=6*u-s,i=t*(1-e),E=t*(1-o*e),l=t*(1-(1-o)*e);switch(s%6){case 0:r=t,a=l,n=i;break;case 1:r=E,a=t,n=i;break;case 2:r=i,a=t,n=l;break;case 3:r=i,a=E,n=t;break;case 4:r=l,a=i,n=t;break;case 5:r=t,a=i,n=E}return[Math.round(255*r),Math.round(255*a),Math.round(255*n)]}function O(u,t,r){var a=e.groupBy(u,t);return a=e.sortBy(a,r)}function P(u,e){for(var t=Math.ceil(u.length/e),r=[],a=0;u.length>a;a+=t)r.push(u.slice(a,a+t));return r}function Z(u,e){e.css("position","relative"),e.css("top",u.height()/2-e.height()/2)}function H(u){var e=u.first_name;return u.middle_name&&(e=e+" "+u.middle_name),u.last_name&&(e=e+" "+u.last_name),e}function R(u){switch(u){case"en":return"English";case"zh-Hant":return"Chinese";case"zh-Hans":return"Chinese";case"it":return"Italian";case"fr":return"French";case"es":return"Spanish"}}function V(u){for(var e=0;L.length>e;e++)u=u.replace(L[e].letters,L[e].base);return u}var L=[{base:"A",letters:/[\u0041\u24B6\uFF21\u00C0\u00C1\u00C2\u1EA6\u1EA4\u1EAA\u1EA8\u00C3\u0100\u0102\u1EB0\u1EAE\u1EB4\u1EB2\u0226\u01E0\u00C4\u01DE\u1EA2\u00C5\u01FA\u01CD\u0200\u0202\u1EA0\u1EAC\u1EB6\u1E00\u0104\u023A\u2C6F]/g},{base:"AA",letters:/[\uA732]/g},{base:"AE",letters:/[\u00C6\u01FC\u01E2]/g},{base:"AO",letters:/[\uA734]/g},{base:"AU",letters:/[\uA736]/g},{base:"AV",letters:/[\uA738\uA73A]/g},{base:"AY",letters:/[\uA73C]/g},{base:"B",letters:/[\u0042\u24B7\uFF22\u1E02\u1E04\u1E06\u0243\u0182\u0181]/g},{base:"C",letters:/[\u0043\u24B8\uFF23\u0106\u0108\u010A\u010C\u00C7\u1E08\u0187\u023B\uA73E]/g},{base:"D",letters:/[\u0044\u24B9\uFF24\u1E0A\u010E\u1E0C\u1E10\u1E12\u1E0E\u0110\u018B\u018A\u0189\uA779]/g},{base:"DZ",letters:/[\u01F1\u01C4]/g},{base:"Dz",letters:/[\u01F2\u01C5]/g},{base:"E",letters:/[\u0045\u24BA\uFF25\u00C8\u00C9\u00CA\u1EC0\u1EBE\u1EC4\u1EC2\u1EBC\u0112\u1E14\u1E16\u0114\u0116\u00CB\u1EBA\u011A\u0204\u0206\u1EB8\u1EC6\u0228\u1E1C\u0118\u1E18\u1E1A\u0190\u018E]/g},{base:"F",letters:/[\u0046\u24BB\uFF26\u1E1E\u0191\uA77B]/g},{base:"G",letters:/[\u0047\u24BC\uFF27\u01F4\u011C\u1E20\u011E\u0120\u01E6\u0122\u01E4\u0193\uA7A0\uA77D\uA77E]/g},{base:"H",letters:/[\u0048\u24BD\uFF28\u0124\u1E22\u1E26\u021E\u1E24\u1E28\u1E2A\u0126\u2C67\u2C75\uA78D]/g},{base:"I",letters:/[\u0049\u24BE\uFF29\u00CC\u00CD\u00CE\u0128\u012A\u012C\u0130\u00CF\u1E2E\u1EC8\u01CF\u0208\u020A\u1ECA\u012E\u1E2C\u0197]/g},{base:"J",letters:/[\u004A\u24BF\uFF2A\u0134\u0248]/g},{base:"K",letters:/[\u004B\u24C0\uFF2B\u1E30\u01E8\u1E32\u0136\u1E34\u0198\u2C69\uA740\uA742\uA744\uA7A2]/g},{base:"L",letters:/[\u004C\u24C1\uFF2C\u013F\u0139\u013D\u1E36\u1E38\u013B\u1E3C\u1E3A\u0141\u023D\u2C62\u2C60\uA748\uA746\uA780]/g},{base:"LJ",letters:/[\u01C7]/g},{base:"Lj",letters:/[\u01C8]/g},{base:"M",letters:/[\u004D\u24C2\uFF2D\u1E3E\u1E40\u1E42\u2C6E\u019C]/g},{base:"N",letters:/[\u004E\u24C3\uFF2E\u01F8\u0143\u00D1\u1E44\u0147\u1E46\u0145\u1E4A\u1E48\u0220\u019D\uA790\uA7A4]/g},{base:"NJ",letters:/[\u01CA]/g},{base:"Nj",letters:/[\u01CB]/g},{base:"O",letters:/[\u004F\u24C4\uFF2F\u00D2\u00D3\u00D4\u1ED2\u1ED0\u1ED6\u1ED4\u00D5\u1E4C\u022C\u1E4E\u014C\u1E50\u1E52\u014E\u022E\u0230\u00D6\u022A\u1ECE\u0150\u01D1\u020C\u020E\u01A0\u1EDC\u1EDA\u1EE0\u1EDE\u1EE2\u1ECC\u1ED8\u01EA\u01EC\u00D8\u01FE\u0186\u019F\uA74A\uA74C]/g},{base:"OI",letters:/[\u01A2]/g},{base:"OO",letters:/[\uA74E]/g},{base:"OU",letters:/[\u0222]/g},{base:"P",letters:/[\u0050\u24C5\uFF30\u1E54\u1E56\u01A4\u2C63\uA750\uA752\uA754]/g},{base:"Q",letters:/[\u0051\u24C6\uFF31\uA756\uA758\u024A]/g},{base:"R",letters:/[\u0052\u24C7\uFF32\u0154\u1E58\u0158\u0210\u0212\u1E5A\u1E5C\u0156\u1E5E\u024C\u2C64\uA75A\uA7A6\uA782]/g},{base:"S",letters:/[\u0053\u24C8\uFF33\u1E9E\u015A\u1E64\u015C\u1E60\u0160\u1E66\u1E62\u1E68\u0218\u015E\u2C7E\uA7A8\uA784]/g},{base:"T",letters:/[\u0054\u24C9\uFF34\u1E6A\u0164\u1E6C\u021A\u0162\u1E70\u1E6E\u0166\u01AC\u01AE\u023E\uA786]/g},{base:"TZ",letters:/[\uA728]/g},{base:"U",letters:/[\u0055\u24CA\uFF35\u00D9\u00DA\u00DB\u0168\u1E78\u016A\u1E7A\u016C\u00DC\u01DB\u01D7\u01D5\u01D9\u1EE6\u016E\u0170\u01D3\u0214\u0216\u01AF\u1EEA\u1EE8\u1EEE\u1EEC\u1EF0\u1EE4\u1E72\u0172\u1E76\u1E74\u0244]/g},{base:"V",letters:/[\u0056\u24CB\uFF36\u1E7C\u1E7E\u01B2\uA75E\u0245]/g},{base:"VY",letters:/[\uA760]/g},{base:"W",letters:/[\u0057\u24CC\uFF37\u1E80\u1E82\u0174\u1E86\u1E84\u1E88\u2C72]/g},{base:"X",letters:/[\u0058\u24CD\uFF38\u1E8A\u1E8C]/g},{base:"Y",letters:/[\u0059\u24CE\uFF39\u1EF2\u00DD\u0176\u1EF8\u0232\u1E8E\u0178\u1EF6\u1EF4\u01B3\u024E\u1EFE]/g},{base:"Z",letters:/[\u005A\u24CF\uFF3A\u0179\u1E90\u017B\u017D\u1E92\u1E94\u01B5\u0224\u2C7F\u2C6B\uA762]/g},{base:"a",letters:/[\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250]/g},{base:"aa",letters:/[\uA733]/g},{base:"ae",letters:/[\u00E6\u01FD\u01E3]/g},{base:"ao",letters:/[\uA735]/g},{base:"au",letters:/[\uA737]/g},{base:"av",letters:/[\uA739\uA73B]/g},{base:"ay",letters:/[\uA73D]/g},{base:"b",letters:/[\u0062\u24D1\uFF42\u1E03\u1E05\u1E07\u0180\u0183\u0253]/g},{base:"c",letters:/[\u0063\u24D2\uFF43\u0107\u0109\u010B\u010D\u00E7\u1E09\u0188\u023C\uA73F\u2184]/g},{base:"d",letters:/[\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A]/g},{base:"dz",letters:/[\u01F3\u01C6]/g},{base:"e",letters:/[\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD]/g},{base:"f",letters:/[\u0066\u24D5\uFF46\u1E1F\u0192\uA77C]/g},{base:"g",letters:/[\u0067\u24D6\uFF47\u01F5\u011D\u1E21\u011F\u0121\u01E7\u0123\u01E5\u0260\uA7A1\u1D79\uA77F]/g},{base:"h",letters:/[\u0068\u24D7\uFF48\u0125\u1E23\u1E27\u021F\u1E25\u1E29\u1E2B\u1E96\u0127\u2C68\u2C76\u0265]/g},{base:"hv",letters:/[\u0195]/g},{base:"i",letters:/[\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131]/g},{base:"j",letters:/[\u006A\u24D9\uFF4A\u0135\u01F0\u0249]/g},{base:"k",letters:/[\u006B\u24DA\uFF4B\u1E31\u01E9\u1E33\u0137\u1E35\u0199\u2C6A\uA741\uA743\uA745\uA7A3]/g},{base:"l",letters:/[\u006C\u24DB\uFF4C\u0140\u013A\u013E\u1E37\u1E39\u013C\u1E3D\u1E3B\u017F\u0142\u019A\u026B\u2C61\uA749\uA781\uA747]/g},{base:"lj",letters:/[\u01C9]/g},{base:"m",letters:/[\u006D\u24DC\uFF4D\u1E3F\u1E41\u1E43\u0271\u026F]/g},{base:"n",letters:/[\u006E\u24DD\uFF4E\u01F9\u0144\u00F1\u1E45\u0148\u1E47\u0146\u1E4B\u1E49\u019E\u0272\u0149\uA791\uA7A5]/g},{base:"nj",letters:/[\u01CC]/g},{base:"o",letters:/[\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275]/g},{base:"oi",letters:/[\u01A3]/g},{base:"ou",letters:/[\u0223]/g},{base:"oo",letters:/[\uA74F]/g},{base:"p",letters:/[\u0070\u24DF\uFF50\u1E55\u1E57\u01A5\u1D7D\uA751\uA753\uA755]/g},{base:"q",letters:/[\u0071\u24E0\uFF51\u024B\uA757\uA759]/g},{base:"r",letters:/[\u0072\u24E1\uFF52\u0155\u1E59\u0159\u0211\u0213\u1E5B\u1E5D\u0157\u1E5F\u024D\u027D\uA75B\uA7A7\uA783]/g},{base:"s",letters:/[\u0073\u24E2\uFF53\u00DF\u015B\u1E65\u015D\u1E61\u0161\u1E67\u1E63\u1E69\u0219\u015F\u023F\uA7A9\uA785\u1E9B]/g},{base:"t",letters:/[\u0074\u24E3\uFF54\u1E6B\u1E97\u0165\u1E6D\u021B\u0163\u1E71\u1E6F\u0167\u01AD\u0288\u2C66\uA787]/g},{base:"tz",letters:/[\uA729]/g},{base:"u",letters:/[\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289]/g},{base:"v",letters:/[\u0076\u24E5\uFF56\u1E7D\u1E7F\u028B\uA75F\u028C]/g},{base:"vy",letters:/[\uA761]/g},{base:"w",letters:/[\u0077\u24E6\uFF57\u1E81\u1E83\u0175\u1E87\u1E85\u1E98\u1E89\u2C73]/g},{base:"x",letters:/[\u0078\u24E7\uFF58\u1E8B\u1E8D]/g},{base:"y",letters:/[\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF]/g},{base:"z",letters:/[\u007A\u24E9\uFF5A\u017A\u1E91\u017C\u017E\u1E93\u1E95\u01B6\u0225\u0240\u2C6C\uA763]/g}];return{handleAPIError:n,handleRedirect:s,setupInternalLinks:i,truncateText:l,setupFormSave:f,renderAllErrors:A,renderFieldErrors:b,isMobileDevice:C,isIOS:v,isTouchSupported:m,isCanvasSupported:D,scrollToInternalLink:o,makeHttps:c,prettyJoin:E,getUrlParam:g,changeUrlParam:d,setMetaTag:B,inView:w,countCS:k,setupPromos:x,prettifyTime:T,prettifyUrl:j,truncateElemText:M,linkifyText:I,rgbToHsl:$,hslToRgb:z,rgbToHsv:_,hsvToRgb:S,verticalCenterEl:Z,sortedGroupBy:O,splitIntoGroups:P,concatName:H,languageCodeToName:R,removeDiacritics:V}});