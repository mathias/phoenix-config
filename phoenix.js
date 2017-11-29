/* globals Phoenix Window App Key */

/*
 * This is a self-documenting configuration for
 * [Phoenix.app](https://github.com/sdegutis/Phoenix), a lightweight scriptable
 * OSX window manager.
 *
 * Phoenix is configured in JS.
 *
 * ## Usage
 *
 * Install Phoenix.app <https://github.com/kasper/phoenix> or use brew:
 * <pre><code>brew cask install phoenix</code></pre>
 *
 * Then, symlink the configuration file into place:
 * <pre><code>ln -s ~/path/to/phoenix_config/phoenix.js ~/.phoenix.js</code></pre>
 *
 * ## Extra credit
 *
 * Installing <https://github.com/puffnfresh/toggle-osx-shadows> will make
 * layouts look a lot nicer!
 */

/* ## Config begins here */

Phoenix.notify('Phoenix config loading');

/* Phoenix helper functions */

var focused = Window.focused;
var visibleWindows = Window.recent;

/* Math helpers */

var round = function(num) {
  return Math.round(num);
}

/* Development helpers */

var debug = function(message) {
  return Phoenix.alert(message, 5);
}

var log = function(message) {
  return Phoenix.log(message);
}

/* Window focus operatiosn */

/* Application Launching: TODO */

/* Grid functions */

var calculateGrid = function(coords) {
  var screenSize = Screen.main().visibleFrame();
  return {
    x: round((screenSize.width * requested.x) + screenSize.x)
  };
}

var sizeToGrid = function(coords) {
  var screen = Screen.main().flippedVisibleFrame();
  var window = Window.focused();
  var frame = {
    x: screen.origin().x,
    y: screen.origin().y,
    width: screen.width()/ 2,
    height: screen.height() / 2
  };


  if (window) {
    return window.setFrame({
      x: round(screen.x + (screen.width * coords.x) - frame.width),
      y: round(screen.y + (screen.height / coords.y) - frame.height),

    });
  }
}

/* Movement functions */
var pushLeft = function() { return sizeToGrid({x: 0, y: 0, width: 0.5, height: 1}); };


/* Keybindings */

var emptyMods = [];
var mash = ['ctrl', 'alt', 'cmd'];
var superK = ['ctrl', 'cmd'];
var superMeta = ['alt', 'cmd'];

var bind = function(letter, chord, fn) {
  return Key.on(letter, chord, fn);
}

bind('left', mash, pushLeft);

/* vim-style bindings for HHKB */
bind('h', superK, pushLeft);
