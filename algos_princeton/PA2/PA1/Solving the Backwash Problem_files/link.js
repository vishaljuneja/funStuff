(function(){var e=function(e,t,n,r,o){return n.defaults=e.extend(n.defaults,{"toolbar.link":!0}),n.plugin("link",function(i,s,a,c){if(c["toolbar.link"]){var l=e(r({prefix:c["prefix.css"]}));n.insertIntoLeftToolbar(s,l,5),s.find("."+c["prefix.css"]+"-toolbar-modals").append(o({prefix:c["prefix.css"]}));var u,d=s.find("[data-wysihtml5-modal-link]"),h=t(d),p=d.find("[name=url]");h.on("open",function(){p.focus()}),h.on("close",function(){i.currentView.element.focus()}),d.find("form").on("submit",function(e){e.preventDefault(),e.stopPropagation(),i.currentView.element.focus(),(void 0!==u||null!==u)&&(i.composer.selection.setBookmark(u),u=null);var t=i.currentView.selection.getSelection();t.focusNode&&t.focusNode.parentNode&&"A"==t.focusNode.parentNode.nodeName?(t.focusNode.parentNode.title=null,t.focusNode.parentNode.href=p.val()):i.composer.commands.exec("createLink",{href:p.val(),target:"_blank"}),i.fire("change").fire("change:composer")}),s.find("[data-wysihtml5-custom-command=link]").on("click",function(){if(!e(this).hasClass("wysihtml5-command-active")&&!s.hasClass("wysihtml5-commands-disabled")){var t=i.currentView.selection&&i.currentView.selection.getSelection();t&&t.focusNode&&t.focusNode.parentNode&&"A"==t.focusNode.parentNode.nodeName?p.val(t.focusNode.parentNode.href):p.val(""),u=i.composer&&i.composer.selection.getBookmark(),h.open()}})}}),n};"function"==typeof define&&define.amd?define(["jquery","js/lib/modals","bundles/wysihtml5/editor","bundles/wysihtml5/toolbar-link.html","bundles/wysihtml5/toolbar-modal-link.html"],function(t,n,r,o,i){return e(t,n,r,o,i)}):e(window.$,window.Modal,window.wysiEditor,window.jade.templates["bundles.wysihtml5.toolbar-link"],window.jade.templates["bundles.wysihtml5.toolbar-modal-link"])})(window);