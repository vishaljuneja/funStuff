(function(wndw){var jadify=function(jade){return function anonymous(locals,attrs,escape,rethrow,merge){attrs=attrs||jade.attrs,escape=escape||jade.escape,rethrow=rethrow||jade.rethrow,merge=merge||jade.merge;var buf=[];with(locals||{}){var interp;entry.get("anonymous")?buf.push("<span>Anonymous</span>"):(buf.push("<a"),buf.push(attrs({href:entry.get("_user_profile_link")},{href:!0})),buf.push(">"),entry.get("_user_profile")?(entry.get("_user_profile").photo_24&&(buf.push("<img"),buf.push(attrs({src:entry.get("_user_profile").photo_24},{src:!0})),buf.push("/>")),buf.push(""+escape(null==(interp=entry.get("_user_profile").display_name)?"":interp))):buf.push(""+escape(null==(interp=entry.get("_user_full_name"))?"":interp)),buf.push("</a>")),entry.get("anonymous")||(entry.get("_user_title")&&"Student"!=entry.get("_user_title")?buf.push('<span class="course-forum-profile-badge">'+escape(null==(interp=entry.get("_user_title"))?"":interp)+"</span>"):entry.get("_show_signature_track_label")&&(buf.push("<a"),buf.push(attrs({style:"text-decoration: none;",href:config.url.base+"signature/course/"+spark_class_short_name+"/"+spark_class_id,target:"_blank"},{style:!0,href:!0,target:!0})),buf.push('><span class="course-forum-profile-badge-signaturetrack">Signature Track</span></a>'))),buf.push('<span style="vertical-align: middle; margin-left: 5px;" class="course-forum-profile-badgeville"></span><span style="vertical-align:middle">· </span><a'),buf.push(attrs({href:entry.getPermalink(),name:entry.getPermalinkHash(),style:"vertical-align:middle"},{href:!0,name:!0,style:!0})),buf.push("> <time"),buf.push(attrs({datetime:entry.get("post_time")},{datetime:!0})),buf.push(">"+escape(null==(interp=entry.get("post_time"))?"":interp)+'</time>&nbsp;<span class="icon-link"></span></a>')}return buf.join("")}};"function"==typeof define&&define.amd?define(["js/lib/jade"],function(e){return jadify(e)}):wndw.jade.templates.EntryBylineView=jadify(wndw.jade.helpers)})(window);