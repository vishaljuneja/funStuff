define(["backbone","jquery","underscore","js/lib/util","pages/forum/app","pages/forum/views/ForumListView.html"],function(e,t,n,i,r,o){var a=e.View.extend({className:"course-forum-subforums-listing",events:{},initialize:function(){this.forum=this.model,this.forum.bind("sync",this.render,this)},render:function(){return this.$el.html(o({config:r.config,util:i,forum:this.forum})),this.$el.find("time").each(function(){i.prettifyTime(t(this))}),this}});return a});