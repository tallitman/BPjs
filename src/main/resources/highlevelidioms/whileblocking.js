/* global bp */

var _bp_sync = bp.sync;

new Object {
  blocking: new EventStack(),

  whileBlocking: function(blocked, f) {
    try {
      blocking.push(blocked);
      f();
      return blocking.pop();
    } catch (thrownEvent) {
      blocking.pop();
      throw thrownEvent;
    }
  },

  bsync: function(requested, wait, blocked) {
    var e;
    if (!this.blocking.isEmpty()) {
      var eset = new EventSet([blocked, this.blocking]);
      e = _bp_sync(requested, wait, eset);
    } else {
      e = _bp_sync(requested, wait, blocked);
    }

    return e;
  }
};
