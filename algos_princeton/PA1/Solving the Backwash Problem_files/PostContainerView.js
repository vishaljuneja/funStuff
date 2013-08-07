define(["backbone","jquery","underscore","pages/forum/app","pages/forum/models/CommentModel","pages/forum/views/PostContainerView.html","pages/forum/views/PostView","pages/forum/views/CommentView"],function(e,t,o,n,r,i,s,u){var a="Forum Post Container",c=e.View.extend({template:i,events:{"click .course-forum-new-comment-link":"onNewCommentClick"},initialize:function(){this.post=this.model,this.post.bind("sync",this.hideCommentsMaybe,this),this.comments=this.options.comments,this.comments.bind("reset",this.renderComments,this),this.comments.bind("add",this.renderComments,this)},render:function(){var e=this;return e.$el.html(e.template({post:e.post})),e.$newCommentContainer=e.$el.find(".course-forum-new-comment-container"),e.renderPost(),e.renderComments(),e.hideCommentsMaybe(),e},renderPost:function(){var e=this;e.$el.find(".course-forum-post-top-container").empty();var t=new s({model:e.post});e.$el.find(".course-forum-post-top-container").append(t.render().$el)},hideCommentsMaybe:function(){this.post.get("deleted")?this.$(".course-forum-comments-container").hide():this.$(".course-forum-comments-container").show()},renderComments:function(){var e=this;e.$el.find(".course-forum-comments-container").empty(),e.comments.each(function(t){c=new u({model:t}),e.$el.find(".course-forum-comments-container").append(c.render().$el)})},initNewComment:function(){this.newComment=new r({post_id:this.post.get("id"),thread:this.post.get("thread")}),this.newComment.bind("sync",this.saveNewCommentMaybe,this);var e=new u({model:this.newComment});this.$newCommentContainer.html(e.render().$el)},saveNewCommentMaybe:function(){this.newComment.isNew()||(this.comments.add(this.newComment),this.newComment.unbind("sync",this.saveNewCommentMaybe,this),this.newComment=null,this.$newCommentContainer.empty())},onNewCommentClick:function(){this.newComment||this.initNewComment(),this.$newCommentContainer.find(".course-forum-post-edit-container").show(),n.multitracker.push([a,"Click New Comment"])}});return c});