(function(t){var e=function(t){var e=function(t){var e=t||{};this.trackers={},this.logger=e.logger||Log({level:"error"})};e.prototype.register=function(t,e,u,o){this.trackers[t]={queue:e||[],type:u||"key-value",parse:o}},e.prototype.get=function(t){return this.trackers[t]},e.prototype.push=function(){for(var e=arguments,u=0;e.length>u;u++){var o=t.isArray(e[u])?e[u]:[e[u]];for(var n in this.trackers)if("google"==this.trackers[n].type){var i,r=[];switch(o[0]){case"pageview":for(r=["_trackPageview"],i=1;o.length>i;i++)r.push(t.isString(o[i])?o[i]:JSON.stringify(o[i]));break;case"event":for(r=["_trackEvent"],i=1;o.length>i;i++)r.push(t.isString(o[i])?o[i]:JSON.stringify(o[i]));break;default:for(r=["_trackEvent"],i=0;o.length>i;i++)r.push(t.isString(o[i])?o[i]:JSON.stringify(o[i]))}this.trackers[n].queue.push(r)}else if("key-value"==this.trackers[n].type){for(var s=[],a=1;o.length>a;a++)s.push(t.isString(o[a])?o[a]:JSON.stringify(o[a]));s.length?this.trackers[n].queue.push({key:o[0],value:s.join(".")}):this.trackers[n].queue.push({key:o[0]})}else"custom"==this.trackers[n].type&&this.trackers[n].parse&&this.trackers[n].queue.push(this.trackers[n].parse.apply(this,o));this.logger.info.apply(this.logger,o)}};var u=function(t){return new e(t)};return u};"function"==typeof define&&define.amd?define(["underscore","js/lib/json2"],function(t){return e(t)}):t.MultiTracker=e(t._)})(window);