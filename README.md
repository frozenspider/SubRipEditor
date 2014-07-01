SubRipEditor
============

*Simple stand-alone editor for .srt subtitle files.*

I'd wish I could say that editor is lightweight, but it's hardly so due to Scala library size (runnable jar size currently is about 9 MB)

Editor is pretty simple and straightforward - e.g. it lacks media player integration
and it's unlikely it will be added later.


Assembly
--------

```
sbt assemby
```
with sbt 0.13.X will create a runnable jar in project root folder.
Don't forget to put `transliteration.conf` file near generated jar, or you won't be able to use transliterations.


Some usage details
------------------

*Transliterate* button serves for changing text written with incorrect keyboard layout on multi-lingual systems.
Currently only support English <--> Russian layout changes.

*Shift All* shifts time marks for all subtitle entries in the editor for a given amount of milliseconds.


Planned features
----------------

* **Func:** add support for custom transliterations
* **Func:** add configurable options
* **Code:** clean it
* **Code:** improve test coverage

License
-------

Licensed under MIT license (see LICENSE file nearby).


Changelog
---------
### 1.0.3
* Parsing rules for TimeMark slightly relaxed, things like 0:0:0,0
* File contol button moved to top
* Added "Shift Before" and "Shift After" buttons
* Timing shift buttons separated from control buttons

### 1.0.2
* TimeTark now supports mills separated by dot, in addition to comma
* Renamed main class to SubRipEditorMain
* Implemented workaround for UTF-8 BOM character
* Fixed case when file is terminated by EOF with no newline

### 1.0.1
* Fixed Find Next