(function(wndw){var jadify=function(jade){return function anonymous(locals,attrs,escape,rethrow,merge){attrs=attrs||jade.attrs,escape=escape||jade.escape,rethrow=rethrow||jade.rethrow,merge=merge||jade.merge;var buf=[];with(locals||{}){var interp;buf.push('<div class="course-forum-threads-listing"><table><caption><h4 style="text-align: left;" class="hidden">Threads</h4></caption><thead class="hidden"><tr><th> <h5>Thread title</h5></th></tr></thead><tbody>'),threads.each(function(e){var t=1==e.get("is_spam")&&user.canModerateForum()?"course-forum-threads-listing-spam":"";buf.push("<tr"),buf.push(attrs({"class":t},{"class":!0})),buf.push("><td>");var r=e.get("_viewer_read")?"":"course-forum-threads-listing-unread";buf.push("<div"),buf.push(attrs({style:"font-size: 14px; line-height: 16px; margin-bottom: 3px;","class":r},{"class":!0,style:!0})),buf.push("><a"),buf.push(attrs({href:e.get("_link")},{href:!0})),buf.push("> <span>"+escape(null==(interp=e.get("title"))?"":interp)+"</span>"),e.get("_viewer_read")||buf.push('<span class="hidden"> (Un-read)</span>'),buf.push('</a></div><div style="opacity:.8; opacity: .8; font-size: 11px; margin-bottom: 6px; line-height: 13px;"><span>Last post by ');var s=e.get("_last_poster");-1==s.id?buf.push("<span>Anonymous</span>"):(buf.push("<a"),buf.push(attrs({href:s._user_profile_link},{href:!0})),buf.push(">"),s._user_profile?(s._user_profile.photo_24&&(buf.push("<img"),buf.push(attrs({src:s._user_profile.photo_24},{src:!0})),buf.push("/>")),buf.push(""+escape(null==(interp=s._user_profile.display_name)?"":interp)+" ")):buf.push(""+escape(null==(interp=s.full_name)?"":interp)+" "),buf.push("</a>")),-1!=s.id&&s.forum_title&&"Student"!=s.forum_title&&buf.push('<span class="course-forum-profile-badge">'+escape(null==(interp=s.forum_title)?"":interp)+"</span>"),buf.push("</span> (<time"),buf.push(attrs({datetime:e.get("last_updated_time")},{datetime:!0})),buf.push("></time>)</div></td></tr>")}),buf.push("</tbody></table></div>")}return buf.join("")}};"function"==typeof define&&define.amd?define(["js/lib/jade"],function(e){return jadify(e)}):wndw.jade.templates.ForumThreadsMiniView=jadify(wndw.jade.helpers)})(window);