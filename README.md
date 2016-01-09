# Pickings

A Clojure app which on pressing a global hotkey appends text from clipboard into the specified file.
I mainly use it to save quotes while reading ebooks.
Works only in Mac OS at the moment.

## Usage

    java -jar <app jar>
    
App stores its config at `~/.pickings-config.edn`.

## Development

To (re)run from REPL:

    lein repl
    user=> (reset)

To dispatch signals from REPL:

    user=> ((-> @system :app :dispatch-signal) :on-reveal-file)

To build an executable:

    lein uberjar

To package for Mac OS (requires pyinvoke):

    inv mac
    
## Architecture

- Model-View-Signal-Action architecture similar to the one from [cljs-elmish-todomvc](https://github.com/metametadata/cljs-elmish-todomvc)
- [Reloaded workflow] (http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) for easier running from REPL
- Seesaw for UI

## TODO

- feature: make it crossplatform (mainly fix notifications: use jwindow and/or use terminal-notifier)
- feature: ! and !!! priorities - e.g by pressing hotkey several times
- feature: enbale/disable taskbar icon, sounds, notificaitons
- feature: only one instance allowed
- feature: drag-n-drop file
- feature: recent files
- feature: better reveal (with file selected)
- feature: be able to edit the rest of config from app: hotkey, delimeter
- package for different OSes
