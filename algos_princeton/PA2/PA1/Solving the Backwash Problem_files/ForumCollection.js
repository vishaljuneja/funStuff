define(["backbone","pages/forum/models/ForumModel"],function(e,t){var n=e.Collection.extend({model:t,comparator:function(e){return e.getFullName()}});return n});