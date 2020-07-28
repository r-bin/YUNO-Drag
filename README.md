YUNO-Drag
=========

DragDropManager is a standalone library which allows to apply the [drag/drop framework][2] for all android versions with a single API.

Based on the [official documentation][1] the [native drag/drop framework][2] was ported back to API level 4 (Android 1.6: Donut). API level 11 (Android 3.0.x: Honeycomb) and newer are using a proxy implementation. This allows developers to almost transparently reuse their implementation.

Recommended to combine with other libraries:

* [ActionBarSherlock][3]
* [HoloEverywhere][4]

Example included.

Progress
========

Based on the [key classes][1]:

* View - completed
* OnLongClickListener - completed
* OnDragListener - completed
* DragEvent - completed
* DragShadowBuilder - WIP
* ClipData - completed*
* ClipDescription -  completed*

    *reuses official android implementation, stripped by about three probably unused methods.

Getting started
===============

The static class `DragDropManager` handles every drag/drop related command for now since it is too invasive to wrap every `View`.

**Preparation:**

```java
public static void createInstance(Activity activity) {...}

public static void destroyInstance() {...}
```

**Usage:**

```java
public void setOnDragListener(View view, OnDragListener dropZonelistener) {...}

public void startDrag(View view, ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {...}

public boolean onDrag(View view, DragEvent event) {...}
```

License
=======

    Copyright 2013

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: http://developer.android.com/training/backward-compatible-ui/index.html
 [2]: http://developer.android.com/guide/topics/ui/drag-drop.html
 [3]: https://github.com/JakeWharton/ActionBarSherlock
 [4]: https://github.com/Prototik/HoloEverywhere
