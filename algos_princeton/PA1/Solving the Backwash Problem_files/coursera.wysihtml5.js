(function(){var t=function(t,e,o){var n={"prefix.css":"coursera-wysihtml5","prefix.data":"data-coursera-wysihtml5","toolbar.modes":!0,"toolbar.image":!1,"composer.class":"coursera-wysihtml5","composer.default":"rich","composer.stylesheet":e.url.app_assets+"bundles/wysihtml5/editor.css","transloadit.key":"05912e90e83346abb96c261bf458b615","transloadit.template":"a295b36c8fd44fbdb89e8eab06980081",parserRules:{tags:{b:1,i:1,p:1,span:1,div:1,ul:1,li:1,ol:1,pre:1,code:1,u:1,br:1,table:1,tr:1,td:1,thead:1,tbody:1,h1:1,h2:1,h3:1,h4:1,h5:1,strong:{rename_tag:"b"},em:{rename_tag:"i"},img:{check_attributes:{src:"url",style:"allow",alt:"allow"}},a:{check_attributes:{href:"url",target:"alt"}}}}};return o.defaults=t.extend(o.defaults,n),o};"function"==typeof define&&define.amd?define(["underscore","js/core/config","bundles/wysihtml5/editor","bundles/wysihtml5/code","bundles/wysihtml5/link","bundles/wysihtml5/modes","bundles/wysihtml5/transloadit","bundles/wysihtml5/latex"],function(e,o,n){return t(e,o,n)}):t(window._,window.configure,window.wysiEditor)})(window);