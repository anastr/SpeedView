# SpeedView
Dynamic Speedometer, Gauge for Android. **amazing**, **powerful**, and _multi shape_ :zap: , you can change (colors, bar width, shape, text, font ...everything !!), this Library has also made to build **games** with `accelerate` and `decelerate`,
 [see project on GitHub](https://github.com/anastr/SpeedView/).

`minSdkVersion=8`

Library Size just ~ 40 Kb.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SpeedView-blue.svg?style=true)](https://android-arsenal.com/details/1/4169)
[![API](https://img.shields.io/badge/API-+8-red.svg?style=flat)](#)
[![Bintray](https://img.shields.io/bintray/v/anastr/maven/SpeedView.svg)](https://bintray.com/anastr/maven/SpeedView)

<img src="https://github.com/anastr/SpeedView/blob/master/images/SpeedView.gif" width="30%" />
<img src="https://github.com/anastr/SpeedView/blob/master/images/DeluxeSpeedView.gif" width="30%" />
<img src="https://github.com/anastr/SpeedView/blob/master/images/AwesomeSpeedometer.gif" width="30%" /><br/>
<img src="https://github.com/anastr/SpeedView/blob/master/images/RaySpeedometer.gif" width="30%" />
<img src="https://github.com/anastr/SpeedView/blob/master/images/PointerSpeedometer.gif" width="30%" />
<img src="https://github.com/anastr/SpeedView/blob/master/images/TubeSpeedometer.gif" width="30%" />

# Download

**add this line to** `build.gradle`

```gradle

dependencies {
	    compile 'com.github.anastr:speedviewlib:1.1.3'
}

```

for **maven**

```maven
<dependency>
  <groupId>com.github.anastr</groupId>
  <artifactId>speedviewlib</artifactId>
  <version>1.1.3</version>
  <type>pom</type>
</dependency>
```
[Get Started](https://github.com/anastr/SpeedView/wiki/0.-Get-Started) with _Speedometer Library_.
# Simple Usage
add Speedometer to your `Layout.xml`.<br>
```xml

<com.github.anastr.speedviewlib.SpeedView
        android:id="@+id/speedView"
        android:layout_width="250dp"
        android:layout_height="wrap_content" />

```

for all speedometers, this simple method to move the indicator:
```java
// move to 50 Km/s
speedometer.speedTo(50);
```

by default, indicator move Duration is 2000 ms.<br>
you can use other Duration by method :
```java
// move to 50 Km/s with Duration = 4 sec
speedometer.speedTo(50, 4000);
```

for more control, see The most important methods at [Get Started - Wiki](https://github.com/anastr/SpeedView/wiki/0.-Get-Started) for **All Speedometers**.<br>
and also you can see **advanced usage** in [Usage - Wiki](https://github.com/anastr/SpeedView/wiki/Usage) and [Work With Notes - Wiki](https://github.com/anastr/SpeedView/wiki/Notes).<br>
<img src="/images/usage/StartEndDegree.png" width="40%" />
<img src="/images/usage/WorkWithNote.gif" width="35%" />
we have 7 Speedometers : <br>
======================
<table style="width:100%">
  <tr>
    <th>Name</th>
    <th>Screenshot</th>
    <th>XML Layout</th>
  </tr>
  <tr>
    <td width="24%"> <a href="https://github.com/anastr/SpeedView/wiki/1.-SpeedView">1. SpeedView - Wiki</a></td>
    <td width="22%"><img src="/images/SpeedView3.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.SpeedView
        android:id="@+id/speedView"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/2.-DeluxeSpeedView">2. DeluxeSpeedView - Wiki</a></td>
    <td><img src="/images/DeluxeSpeedView2.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.DeluxeSpeedView
        android:id="@+id/deluxeSpeedView"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/3.-AwesomeSpeedometer">3. AwesomeSpeedometer - Wiki</a></td>
    <td><img src="/images/AwesomeSpeedometer.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.AwesomeSpeedometer
        android:id="@+id/awesomeSpeedometer"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/4.-RaySpeedometer">4. RaySpeedometer - Wiki</a></td>
    <td><img src="/images/RaySpeedometer.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.RaySpeedometer
        android:id="@+id/raySpeedometer"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/5.-PointerSpeedometer">5. PointerSpeedometer - Wiki</a></td>
    <td><img src="/images/PointerSpeedometer.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.PointerSpeedometer
        android:id="@+id/pointerSpeedometer"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/6.-TubeSpeedometer">6. TubeSpeedometer - Wiki</a></td>
    <td><img src="/images/TubeSpeedometer.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.TubeSpeedometer
        android:id="@+id/tubeSpeedometer"
        android:layout_width="300dp"
        android:layout_height="wrap_content" />
      </textarea></pre>
    </td>
  </tr>
  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/7.-ImageSpeedometer">7. ImageSpeedometer - Wiki</a></td>
    <td><img src="/images/ImageSpeedometer.png"/></td>
    <td>
      <pre><textarea>
<com.github.anastr.speedviewlib.ImageSpeedometer
        android:id="@+id/imageSpeedometer"
        android:layout_width="300dp"
        android:layout_height="wrap_content"
        app:imageSpeedometer="@drawable/your_image" />
      </textarea></pre>
    </td>
  </tr>
</table>

## TODO
* add fuel gauge component.
* build new custom speedometer.

## Coming Soon ...
i well try to draw this Speedometer.
if you have any idea, image, template please **open new issue** and give me the image , and i well try to add it to the Library.

<img src="/images/new2.png" width="30%" />
<img src="/images/new3.png" width="30%" />

# LICENSE
```

Copyright 2016 Anas ALtair

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
