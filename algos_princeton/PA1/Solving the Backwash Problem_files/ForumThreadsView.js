define(["backbone","jquery","underscore","js/lib/util","js/lib/jquery.bbq","pages/forum/app","pages/forum/views/WidgetView","pages/forum/views/ForumThreadsView.html"],function(e,t,s,r,a,i,o,n){var h=o.extend({events:{"click .course-forum-thread-paginator":"onPageClick","click .course-forum-thread-showmore":"onShowMoreClick","click .course-forum-threads-sort li a":"onSortClick"},initialize:function(){var e=this;this.forum=this.model,this.forumThreads=this.options.threads,this.showNewThread=s.isUndefined(this.options.showNewThread)?!1:this.options.showNewThread,this.showSortControls=s.isUndefined(this.options.showSortControls)?!0:this.options.showSortControls,this.maxDisplayed=s.isUndefined(this.options.maxDisplayed)?this.forumThreads.get("page_size"):this.options.maxDisplayed,this.showEmptyMessage=s.isUndefined(this.options.showEmptyMessage)?!0:this.options.showEmptyMessage,this.mode=this.options.mode||"widget",this.getWidgetState("sort")&&this.forumThreads.set("sort",this.getWidgetState("sort"),{silent:!0}),this.forum.bind("change",this.onForumChange,this),this.forumThreads.bind("change",this.onThreadsChange,this);var t=this.getWidgetState("page_num")||1;this.forumThreads.loadPage(t),e.addWidgetStateListener(),e.bind("widget:changed",function(t){s.contains(t,"sort")&&e.changeSort(e.getWidgetState("sort")),s.contains(t,"page_num")&&e.changePage(e.getWidgetState("page_num"))})},onForumChange:function(){this.forum.changedAttributes()&&this.forum.changedAttributes()._marked_as_read&&(this.forumThreads.get("threads").each(function(e){e.set("_viewer_read",!0,{silent:!0})}),this.render()),this.forum.changedAttributes()&&this.forum.changedAttributes()._new_thread_link&&this.render()},onThreadsChange:function(){this.render()},render:function(){function e(e){e.parent("ul").remove(".course-forum-threads-sort-selected-marker"),e.parent("ul").find("li").removeClass("active"),e.addClass("active"),e.append(t("<span>").addClass("course-forum-threads-sort-selected-marker").append("(selected)"))}var a=this,o=1==this.forumThreads.get("current_page")&&this.forumThreads.get("page_size")>this.maxDisplayed&&this.forumThreads.get("threads").size()>this.maxDisplayed,h=this.showNewThread;this.forum.get("_new_thread_link")?(h=this.showNewThread&&-1!=this.forum.get("parent_id")&&(this.forum.get("can_post")||i.user.canModerateForum()),this.newThreadLink=this.forum.get("_new_thread_link"),this.forumThreads.get("tag_name")&&(this.newThreadLink=r.changeUrlParam(this.newThreadLink,"tag_name",this.forumThreads.get("tag_name")))):h=!1,this.$el.html(n({_:s,user:i.user,forum:this.forum,forumThreads:this.forumThreads,threads:this.forumThreads.get("threads"),newThreadLink:this.newThreadLink,showNewThread:h,showSortControls:this.showSortControls,showMore:o,maxDisplayed:this.maxDisplayed,showEmptyMessage:this.showEmptyMessage})),this.$el.find("time").each(function(){r.prettifyTime(t(this))});var d=null,u=a.$(".course-forum-threads-sort li");return u.each(function(t,s){var r=a.$(s);r.attr("data-sort-name")===a.forumThreads.get("sort")&&(e(r),d=r)}),this},onShowMoreClick:function(){this.$(".course-forum-thread-paginator").show(),this.$(".course-forum-threads-listing-row").show(),this.$(".course-forum-thread-showmore").hide()},onPageClick:function(e){this.changeWidgetState({page_num:t(e.target).attr("data-page-num")})},onSortClick:function(e){this.changeWidgetState({page_num:1,sort:t(e.target).parent().attr("data-sort-name")})},changePage:function(e){var s=this;s.forumThreads.loadPage(e).done(function(){t("html,body").scrollTop(s.$el.position().top)})},changeSort:function(e){this.forumThreads.set("sort",e),this.forumThreads.loadPage(1)}});return h});