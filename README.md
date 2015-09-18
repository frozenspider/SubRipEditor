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

*Shift* buttons shifts time marks for subtitle entries in the editor for a given amount of milliseconds.

*Shift Before* and *Shift After* shifts all entries before/after selected entry (inclusive).


Planned features
----------------

* **Func:** add support for custom transliterations
* **Func:** add configurable options
* **Code:** clean it
* **Code:** improve test coverage

License
-------

Licensed under MIT license (see LICENSE file nearby).


Known issues
------------

(Re-)loading a file causes selection indicator to disappear, but it remains in the same place for the purpose of relative shift-buttons and "Find Next" 


Changelog
---------
### 1.0.5
* Included juniversalchardet in order to detect file encoding (files will be saved as UTF-8 regardless)
* Now supports reading file with multiple newlines between entries
* Updated Scala version to 2.11.7 and libraries to latest

### 1.0.4
* Reloading current file no longer scrolls list to the top
* Fixed issue with uppercase letters in search field
* Fixed opening file in UTF-8 with BOM
* Fixed issue with double-clicking list items (thanks for the bug, scala-swing!)

### 1.0.3
* Parsing rules for TimeMark slightly relaxed, things like 0:0:0,0 are accepted now
* File contol button moved to top
* Added "Shift Before" and "Shift After" buttons
* Timing shift buttons separated from control buttons
* Loading a file now automatically scrolls to the top
* Fixed "Add" button not working after closing a file

### 1.0.2
* TimeTark now supports mills separated by dot, in addition to comma
* Renamed main class to SubRipEditorMain
* Implemented workaround for UTF-8 BOM character
* Fixed case when file is terminated by EOF with no newline

### 1.0.1
* Fixed Find Next