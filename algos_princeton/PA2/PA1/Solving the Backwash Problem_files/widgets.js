define(["jquery","underscore","backbone","js/lib/util","pages/forum/app","pages/forum/models/UserModel","pages/forum/models/ThreadModel","pages/forum/models/ForumModel","pages/forum/models/ForumThreadsModel","pages/forum/models/ReputationsCollection","pages/forum/views/ThreadView","pages/forum/views/ThreadNewView","pages/forum/views/UserProfileView","pages/forum/views/ForumView","pages/forum/views/ForumThreadsView","pages/forum/views/ForumThreadsMiniView","pages/forum/views/ForumReputationsView"],function(u,e,t,r,a,s,n,i,o,E,l,d,c,g,f,h,F){u(document).ready(function(){var t=new s({id:"current"});t.read().done(function(){a.user=t,u("[data-coursera-forum-thread-widget]").each(function(){var t=u(this).is("[data-triage-enabled]"),a=u(this).is("[data-hangouts-enabled]"),s=Number(u(this).attr("data-course-id")),i=Number(u(this).attr("data-thread-id")),o=u(this).attr("data-thread-mode"),E=window.location.hash.replace("#","").split("-"),d={};d[E[0]]=E[1];var c=r.getUrlParam(window.location.href,"sort"),g=new n({courseId:s,triageEnabled:t,id:i,_sort_order:c});new l(e.extend(d,{el:u(this)[0],model:g,mode:o,hangoutsEnabled:a}))}),u("[data-coursera-forum-thread-new-widget]").each(function(){var e=new n({forum_id:u(this).attr("data-forum-id")});new d({el:u(this)[0],model:e,tagName:u(this).attr("data-tag-name")})}),u("[data-coursera-forum-widget]").each(function(){var e=u(this).attr("data-forum-id"),t=u(this).attr("data-embed-mode"),r=new i({id:e,course_id:Number(u(this).attr("data-course-id"))});new g({el:u(this)[0],mode:t,model:r})}),u("[data-coursera-forum-threads-widget]").each(function(){var e=new i({id:u(this).attr("data-forum-id")||"0",course_id:Number(u(this).attr("data-course-id"))}),t=new o({id:e.get("id"),tag_id:u(this).attr("data-tag-id"),tag_name:u(this).attr("data-tag-name"),course_id:Number(u(this).attr("data-course-id")),sort:r.getUrlParam(window.location.href,"sort")});new f({el:u(this)[0],mode:u(this).attr("data-embed-mode"),model:e,threads:t,showNewThread:!0}),e.read()}),u("[data-coursera-forum-mini-threads-widget]").each(function(){var e=new o({id:u(this).attr("data-forum-id")||"0",page_size:5});new h({el:u(this)[0],model:e})}),u("[data-coursera-forum-user-profile-widget]").each(function(){new c({el:u(this)[0],mode:u(this).attr("data-embed-mode"),model:new s({id:u(this).attr("data-user-id")})})}),u("[data-coursera-forum-reputations-widget]").each(function(){new F({el:u(this)[0],mode:u(this).attr("data-embed-mode"),collection:new E})}),r.setupPromos()})})});