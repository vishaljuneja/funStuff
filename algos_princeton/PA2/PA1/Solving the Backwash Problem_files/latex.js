(function(){var e=function(e){for(var t=/\${2}/g,n=[],r={},o=t.exec(e);o;)r.start?(r.end=o.index,r.text=e.substring(r.start,r.end),n.push(r),r={}):r.start=o.index+"$$".length,o=t.exec(e);return n},t=function(e,t){for(var n,r=0;n=t[r];r++)if(e>=n.start&&n.end>=e)return n;return null},n=function(n,r,o,i,s,a,c,l){var u=function(n,r,o,i,s,a){o=[],i.val(""),s.html("");var c=n.composer.selection.getBookmark();if(c){if(3===c.startContainer.nodeType){o=e(c.startContainer.textContent);var l=t(c.startOffset,o);l&&(i.val(l.text),i.trigger("keyup"))}caretBookmark=c}a.open()},d=function(e,t,o){t.addClass("wysihtml5-commands-disabled"),t.find(".coursera-wysihtml5-toolbar-modes").hide(),n(e.textareaElement);var i=n(e.composer.iframe),s=i.clone().removeClass("wysihtml-sandbox"),a=n("<div>").hide().addClass(e.config.composeClassName).appendTo(t),c=["background-color","color","cursor","font-family","font-size","font-style","font-variant","font-weight","line-height","letter-spacing","text-align","text-decoration","text-indent","text-rendering","word-break","word-wrap","word-spacing"];s.css({position:"absolute",top:t.outerHeight(),left:0}).appendTo(t),s.ready(function(){window.setTimeout(function(){var l=s[0].contentWindow.document,u=i[0].contentWindow.document,d=n("body",u),h=n("body",l);r.each(c,function(e){h.css(e,d.css(e))});var p=n("head",l);r.each(e.config.stylesheets,function(e){n("<link>",l).attr({rel:"stylesheet",href:e}).appendTo(p)}),a.append(e.getValue(!0)),o.render(a[0],function(){a.remove().appendTo(h).show()}),t.find(".coursera-wysihtml5-toolbar-latex-preview").show().one("click",function(){a.remove(),s.remove(),t.removeClass("wysihtml5-commands-disabled"),t.find(".coursera-wysihtml5-toolbar-modes").show(),t.find(".coursera-wysihtml5-toolbar-latex-preview").hide()})},0)})},h=r.debounce(i.render,200);return s.defaults=n.extend(s.defaults,{"toolbar.latex":!1}),s.plugin("latex",function(e,r,p,f){if(f["toolbar.latex"]){var g=n(a({prefix:f["prefix.css"]})),m=n(c({prefix:f["prefix.css"]}));s.insertIntoLeftToolbar(r,g,100),s.insertIntoRightToolbar(r,m,200),r.find("."+f["prefix.css"]+"-toolbar-modals").append(l({prefix:f["prefix.css"]}));var v,b=r.find("[data-wysihtml5-modal-latex]"),y=o(b),w=b.find("[name=latex]"),C=b.find(".coursera-wysihtml5-latex-preview"),x=[];w.on("keyup",function(){C.html("$$"+w.val()+"$$"),h(C[0])}),w.trigger("keyup"),y.on("open",function(){w.focus()}),y.on("close",function(){e.currentView.element.focus()}),b.find("form").on("submit",function(n){n.preventDefault(),e.currentView.element.focus();var r,o,i,s,a,c,l;v||(v=e.composer.selection.getBookmark()),e.composer.selection.setBookmark(v);var u=t(v.startOffset,x);u?(o=u.start,i=u.end,r=!1):(o=v.startOffset,i=o,r=!0),s=w.val(),r&&(s="$$"+s+"$$"),3===v.startContainer.nodeType?(a=v.startContainer.textContent.substring(0,o),c=v.startContainer.textContent.substring(i),v.startContainer.textContent=a+s+c):(l=document.createTextNode(s),v.insertNode(l)),v=null,e.fire("change").fire("change:composer")}),r.find("."+f["prefix.css"]+"-toolbar-latex-popup-item-editor").on("click",function(){n(this).hasClass("wysihtml5-command-active")||r.hasClass("wysihtml5-commands-disabled")||u(e,r,x,w,C,y)}),r.find("."+f["prefix.css"]+"-toolbar-latex-popup-item-preview").on("click",function(){n(this).hasClass("wysihtml5-command-active")||r.hasClass("wysihtml5-commands-disabled")||d(e,r,i)})}}),s};"function"==typeof define&&define.amd?define(["jquery","underscore","js/lib/modals","js/lib/coursera.mathjax","bundles/wysihtml5/editor","bundles/wysihtml5/toolbar-latex.html","bundles/wysihtml5/toolbar-right-latex.html","bundles/wysihtml5/toolbar-modal-latex.html","js/lib/popups"],function(e,t,r,o,i,s,a,c,l){return n(e,t,r,o,i,s,a,c,l)}):n(window.$,window._,window.Modal,window.mathjax,window.wysiEditor,window.jade.templates["bundles.wysihtml5.toolbar-latex"],window.jade.templates["bundles.wysihtml5.toolbar-right-latex"],window.jade.templates["bundles.wysihtml5.toolbar-modal-latex"],window.Popup)})(window);