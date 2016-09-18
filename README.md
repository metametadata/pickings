# Pickings

A Clojure app which on pressing a global hotkey appends text from clipboard into the specified file.
I mainly use it to save quotes while reading ebooks.

![Screenshot](http://i.imgur.com/2aGvNmA.png)

## Download

See [Releases](https://github.com/metametadata/pickings/releases).

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

To package for Mac OS (experimental, requires pyinvoke):

    inv mac
    
## Architecture

- Unidirectional data flow using [Carry](https://github.com/metametadata/carry/)
- [Seesaw](https://github.com/daveray/seesaw) for UI
- [Reloaded workflow] (http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) for easier running from REPL

## TODO
- feature: ! and !!! priority suffixes - e.g by pressing hotkey several times
- feature: toggle taskbar icon
- feature: only one instance allowed
- feature: drag-n-drop file
- feature: recent files
- feature: better reveal (with file selected)
- feature: be able to edit the rest of config from app: hotkey, delimeter
- package for different OSes
