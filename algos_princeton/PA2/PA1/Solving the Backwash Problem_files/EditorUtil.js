define(["backbone","jquery","underscore","pages/forum/app","js/lib/coursera.wysihtml5","js/lib/util","js/lib/cookie"],function(r,e,t,a,s,n){function o(r){var e="rich";n.isIOS()&&(e="markdown");var t=s(r.find("textarea"),{"toolbar.image":!0,"toolbar.latex":!0,"toolbar.code":!0,"toolbar.mode.markdown":!0,"composer.default":e,"composer.group":"forums","composer.stylesheet":[a.config.url.app_assets+"css/spark.main.css",a.config.url.app_assets+"css/spark.forum.hg.css"]});return r.find("input[name=text_type]").val("html"),t}function i(r){var t={};return r.find("input,textarea,select").each(function(){t[e(this).attr("name")]="checkbox"==e(this).attr("type")?e(this).is(":checked")?e(this).val():0:e(this).val()}),t}return{makeEditor:o,getPropsFromForm:i}});