(function(){var e=function(e,t,a,r,s){var i={"prefix.css":"","prefix.data":"data-wysihtml5","composer.class":"wysihtml5","composer.stylesheet":"editor.css","toolbar.enabled":!0,parserRules:{tags:{b:1,i:1,p:1,ul:1,li:1,ol:1,u:1,br:1,a:{check_attributes:{href:"url",target:"allow"}}}}},o=function(e){var t=function(){var t=parseInt(e.css("height"),10)||0,a=parseInt(e.css("min-height"),10)||0,r=parseInt(e.css("max-height"),10)||0,s=parseInt(e.css("padding-top"),10)||0,i=parseInt(e.css("padding-bottom"),10)||0,o=parseInt(e.css("border-top"),10)||0,n=parseInt(e.css("border-bottom"),10)||0,l=function(e){var l=a?Math.max(parseInt(e,10)+o+n+s+i||0,a):t+o+n+s+i;return r?Math.min(l,r):Math.max(l,e+o+n+s+i)},u=e.get(0).scrollHeight-s-i,c=l(u);e.css({height:c}),u>c?e.css({"overflow-y":"auto"}):e.css({"overflow-y":"hidden"})};e.on("keyup focus",t),t()},n=function(t){var a=e(t.textareaElement);t.on("load",function(){if(t.composer){var r=e(t.composer.iframe),s=e(r[0].contentWindow.document),i=s.find("html").css({height:"100%",width:"100%",margin:0,padding:0}).find("body").css({height:"auto",width:"100%",margin:0,padding:0}),o=parseInt(a.css("height"),10)||0,n=parseInt(a.css("min-height"),10)||0,l=parseInt(a.css("max-height"),10)||0,u=parseInt(r.css("padding-top"),10)||0,c=parseInt(r.css("padding-bottom"),10)||0,d=parseInt(r.css("border-top"),10)||0,p=parseInt(r.css("border-bottom"),10)||0,h=function(e){var t=n?Math.max(parseInt(e,10)+d+p+u+c||0,n):o+d+p+u+c;return l?Math.min(t,l):Math.max(t,e+d+p+u+c)},g=function(){var t=r;if(e(r).is(":visible")){t.css({height:h(i.height())});var a=i.children(),s=a.length&&1==a.get(0).nodeType?a.get(0).offsetTop:0;t.css({height:h(i.get(0).scrollHeight+s)}),i.height()<parseInt(i.css("line-height"),10)&&t.css({height:h(i.css("line-height"))})}};r.css({height:a.css("height")}),e(t.composer.element).on("keyup",g),t.on("aftercommand:composer",g),t.on("change_view",g),g()}})},l=function(e,t){var a={};for(var r in d.defaults)a[r]=t[r]!==void 0?t[r]:d.defaults[r];return s.parse(e,a,d.defaults["prefix.data"]+"-")},u={},c=function(s,i){var c=e(s),d=l(c,i||{}),p=e(r({prefix:d["prefix.css"]})).insertBefore(c[0]);"100%"!=c[0].style.width&&p.css("width",c[0].style.width);var h=e("<div>").addClass(d["prefix.css"]+"-footer");c.after(h);var g=new a.Editor(c[0],{toolbar:d["toolbar.enabled"]===!0?p[0]:!1,parserRules:d.parserRules,composerClassName:d["composer.class"],stylesheets:t.isArray(d["composer.stylesheet"])?d["composer.stylesheet"]:[d["composer.stylesheet"]]});g.on("change",function(){c.trigger("change")}),n(g),o(e(g.textareaElement).addClass(d["prefix.css"]+"-rich-editor"));for(var f in u)"function"==typeof u[f]&&u[f](g,p,h,d);return g},d=function(e,t){return new c(e,t)};return d.defaults=i,d.setupSecondaryTextarea=function(e,t){a.dom.copyStyles(["overflow-y","width"]).from(e.textareaElement).to(t[0]),o(t)},d.insertIntoLeftToolbar=function(t,a,r){var s=t.find(".left").find("[data-wysihtml5-order]");s.each(function(t){var i=e(this);i.attr("data-wysihtml5-order")>r?a.insertBefore(i).attr({"data-wysihtml5-order":r}):s.length==t+1&&a.insertAfter(i).attr({"data-wysihtml5-order":r})})},d.insertIntoRightToolbar=function(t,a,r){var s=t.find(".right").find("[data-wysihtml5-order]");s.each(function(t){var i=e(this);i.attr("data-wysihtml5-order")>r?a.insertBefore(i).attr({"data-wysihtml5-order":r}):s.length==t+1&&a.insertAfter(i).attr({"data-wysihtml5-order":r})})},d.updateFooter=function(e,t){e.html(t)},d.plugin=function(e,t){u[e]=t},d};"function"==typeof define&&define.amd?define(["jquery","underscore","bundles/wysihtml5/wysihtml5-0.4.0pre","bundles/wysihtml5/toolbar.html","js/lib/data.attributes"],function(t,a,r,s,i){return e(t,a,r,s,i)}):window.wysiEditor=e(window.$,window._,window.wysihtml5,window.jade.templates["bundles.wysihtml5.toolbar"],window.DataAttributes)})(window);