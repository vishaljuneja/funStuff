(function(wndw){var jadify=function(jade){return function anonymous(locals,attrs,escape,rethrow,merge){attrs=attrs||jade.attrs,escape=escape||jade.escape,rethrow=rethrow||jade.rethrow,merge=merge||jade.merge;var buf=[];with(locals||{})buf.push("<button"),buf.push(attrs({"data-wysihtml5-custom-command":"latex",type:"button",unselectable:"on",title:"Math Options","data-popup":"#"+prefix+"-toolbar-latex-popup","aria-haspopup":"true","aria-expanded":"false","aria-owns":prefix+"-toolbar-latex-popup","data-popup-close":!0,"data-popup-direction":"sw","class":prefix+"-toolbar-item"},{"class":!0,"data-wysihtml5-custom-command":!0,type:!0,unselectable:!0,title:!0,"data-popup":!0,"aria-haspopup":!0,"aria-expanded":!0,"aria-owns":!0,"data-popup-close":!0,"data-popup-direction":!0})),buf.push(">Math</button><div"),buf.push(attrs({id:prefix+"-toolbar-latex-popup",style:"display:none;"},{id:!0,style:!0})),buf.push("><a"),buf.push(attrs({"data-popup-close":!0,"class":prefix+"-toolbar-latex-popup-item "+prefix+"-toolbar-latex-popup-item-editor"},{"class":!0,"data-popup-close":!0})),buf.push(">Equation Builder</a><a"),buf.push(attrs({"data-popup-close":!0,"class":prefix+"-toolbar-latex-popup-item "+prefix+"-toolbar-latex-popup-item-preview"},{"class":!0,"data-popup-close":!0})),buf.push(">Full Preview</a></div>");return buf.join("")}};"function"==typeof define&&define.amd?define(["js/lib/jade"],function(e){return jadify(e)}):wndw.jade.templates["toolbar-latex"]=jadify(wndw.jade.helpers)})(window);