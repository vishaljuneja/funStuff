(function(){function t(t){if("title"==t.nodeName.toLowerCase())return!1;if(window.getComputedStyle){try{var e=window.getComputedStyle(t,null);if(e.getPropertyValue&&"none"==e.getPropertyValue("display"))return!0}catch(n){}return!1}}var e=function(n,r,i){i=i||{};for(var o=i.nodesToIgnore||[],a=i.parseHiddenNodes||"false",u=n.childNodes,s=0;u.length>s;s++)try{for(var l=!1,c=0;o.length>c;c++)if(u[s].nodeName.toLowerCase()==o[c]){l=!0;break}if(l||!a&&t(u[s]))continue;if("#text"!=u[s].nodeName.toLowerCase()&&"#comment"!=u[s].nodeName.toLowerCase()){var f=[];if(u[s].hasAttributes())for(var h=u[s].attributes,d=0;h.length>d;d++){var p=h.item(d);f.push({name:p.nodeName,value:p.nodeValue})}if(r.start)if(u[s].hasChildNodes()){if(r.start(u[s].nodeName,f,!1),"iframe"==u[s].nodeName.toLowerCase()||"frame"==u[s].nodeName.toLowerCase()){if(u[s].contentDocument&&u[s].contentDocument.documentElement)return e(u[s].contentDocument.documentElement,r,i)}else u[s].hasChildNodes()&&e(u[s],r,i);r.end&&r.end(u[s].nodeName)}else r.start(u[s].nodeName,f,!0)}else"#text"==u[s].nodeName.toLowerCase()?r.chars&&r.chars(u[s].nodeValue):"#comment"==u[s].nodeName.toLowerCase()&&r.comment&&r.comment(u[s].nodeValue)}catch(g){console.error(g),console.log("error while parsing node: "+u[s].nodeName.toLowerCase())}};Array("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"),"function"==typeof define&&define.amd?define([],function(){return e}):window.HTMLParser=e})();