(function(wndw){var jadify=function(jade){return function anonymous(locals,attrs,escape,rethrow,merge){attrs=attrs||jade.attrs,escape=escape||jade.escape,rethrow=rethrow||jade.rethrow,merge=merge||jade.merge;var buf=[];with(locals||{})thread.get("num_posts")>1&&(buf.push('<ul class="nav nav-pills pull-right"><li><span style="padding: 8px; display: inline-block;">Sort replies by:</span></li><li data-sort-name="oldest"><a'),buf.push(attrs({href:thread.getURLForSortOrder("oldest")},{href:!0})),buf.push('>Oldest first</a></li><li data-sort-name="newest"><a'),buf.push(attrs({href:thread.getURLForSortOrder("newest")},{href:!0})),buf.push('>Newest first</a></li><li data-sort-name="popular"><a'),buf.push(attrs({href:thread.getURLForSortOrder("popular")},{href:!0})),buf.push(">Most popular</a></li></ul>"));return buf.join("")}};"function"==typeof define&&define.amd?define(["js/lib/jade"],function(e){return jadify(e)}):wndw.jade.templates.ThreadSortView=jadify(wndw.jade.helpers)})(window);