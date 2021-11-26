# SpeedView
Dynamic Speedometer, Gauge for Android. **amazing**, **powerful**, and _multi shape_ :zap: , you can change (colors, bar width, shape, text, font ...everything !!), this Library has also made to build **games** with `accelerate` and `decelerate`,
 [see project on GitHub](https://github.com/anastr/SpeedView/).

`minSdkVersion=11`

Library Size just ~ 48 KB.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SpeedView-green.svg?style=true)](https://android-arsenal.com/details/1/4169)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.anastr/speedviewlib?color=cyan)](https://mvnrepository.com/artifact/com.github.anastr/speedviewlib/latest)
[![API](https://img.shields.io/badge/API-+11-red.svg?style=flat)](#)
[![Twitter](https://img.shields.io/badge/Twitter-@AnasAltairDent-blue.svg?style=flat)](http://twitter.com/AnasAltairDent)

Download demo on Google Play:\
 <a href='https://play.google.com/store/apps/details?id=com.github.anastr.speedviewapp&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img  width="25%" alt='SpeedView Demo on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

> This library has rewritten with Kotlin in version 1.4.0 manually.
> If you have any problem please open an issue, or you can use old version 1.3.1 written in java.

**Speedometers...**<br/>
<img src="images/SpeedView.gif" width="32%" />
<img src="images/AwesomeSpeedometer.gif" width="32%" />
<img src="images/PointerSpeedometer.gif" width="32%" />

**Gauges...**<br/>
<img src="images/ProgressiveGauge.gif" width="49%" />
<img src="images/ImageLinearGauge.gif" width="49%" />

# Download

Starting from version `1.5.4` this library uploaded to `mavenCentral`, the old versions was on `jcenter`. To work with this library you need `Kotlin` version `1.5.20` or above.

First add kotlin to your project, in `build.gradle` **project level**:

```gradle
buildscript {
    ext.kotlin_version = '1.5.31'
    dependencies {
        ...
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
...
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```

Then add this line to `build.gradle` **app module level**:

```gradle
apply plugin: 'kotlin-android'
...
dependencies {
	implementation 'com.github.anastr:speedviewlib:1.6.0'
}

```

For **maven**

```maven
<dependency>
  <groupId>com.github.anastr</groupId>
  <artifactId>speedviewlib</artifactId>
  <version>1.6.0</version>
  <type>pom</type>
</dependency>
```
**[Get Starting](https://github.com/anastr/SpeedView/wiki/0.-Get-Started)** with _SpeedView Library_.
# Simple Usage
Choose one of Speedometers, gauges and add it to your `Layout.xml`, here we use **SpeedView**.<br>
```xml

<com.github.anastr.speedviewlib.SpeedView
        android:id="@+id/speedView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

```

For all speedometers and gauges, this simple method to smoothly change the speed:
```kotlin
SpeedView speedometer = findViewById(R.id.speedView)

// move to 50 Km/s
speedometer.speedTo(50)
```

By default, speed change duration between last speed and new one is `2000 ms`.<br>
You can pass your duration by this method :
```kotlin
// move to 50 Km/s with Duration = 4 sec
speedometer.speedTo(50, 4000)
```

Automatically, indicator moves around current speed to add some reality to speedometer because of [Tremble](https://github.com/anastr/SpeedView/wiki/0.-Get-Started#tremble), you can stop it by `app:sv_withTremble="false"`attribute or call this in your code:
```kotlin
speedometer.withTremble = false
```

**For more control**, see the most important methods at [Get Started - Wiki](https://github.com/anastr/SpeedView/wiki/0.-Get-Started) for **All Speedometers & Gauges**.<br>
And also you can see **Advanced Usage** in [Usage - Wiki](https://github.com/anastr/SpeedView/wiki/Usage).

More advanced features:
- Work with [Indicators - Wiki](https://github.com/anastr/SpeedView/wiki/Indicators).
- Work With [Notes - Wiki](https://github.com/anastr/SpeedView/wiki/Notes).

<img src="/images/usage/StartEndDegree.png" width="40%" /> <img src="/images/usage/WorkWithNote.gif" width="35%" />

## All Speedometers, Gauges :

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
       <pre>
&lt; com.github.anastr.speedviewlib.SpeedView
        android:id="@+id/speedView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
	</pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/2.-DeluxeSpeedView">2. DeluxeSpeedView - Wiki</a></td>
    <td><img src="/images/DeluxeSpeedView2.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.DeluxeSpeedView
        android:id="@+id/deluxeSpeedView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/3.-AwesomeSpeedometer">3. AwesomeSpeedometer - Wiki</a></td>
    <td><img src="/images/AwesomeSpeedometer.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.AwesomeSpeedometer
        android:id="@+id/awesomeSpeedometer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/4.-RaySpeedometer">4. RaySpeedometer - Wiki</a></td>
    <td><img src="/images/RaySpeedometer.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.RaySpeedometer
        android:id="@+id/raySpeedometer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/5.-PointerSpeedometer">5. PointerSpeedometer - Wiki</a></td>
    <td><img src="/images/PointerSpeedometer.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.PointerSpeedometer
        android:id="@+id/pointerSpeedometer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/6.-TubeSpeedometer">6. TubeSpeedometer - Wiki</a></td>
    <td><img src="/images/TubeSpeedometer.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.TubeSpeedometer
        android:id="@+id/tubeSpeedometer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/7.-ImageSpeedometer">7. ImageSpeedometer - Wiki</a></td>
    <td><img src="/images/ImageSpeedometer.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.ImageSpeedometer
        android:id="@+id/imageSpeedometer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:sv_image="@drawable/your_image" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/8.-ProgressiveGauge">8. ProgressiveGauge - Wiki</a></td>
    <td><img src="/images/ProgressiveGauge.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.ProgressiveGauge
        android:id="@+id/gauge"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
      </pre>
    </td>
  </tr>

  <tr>
    <td> <a href="https://github.com/anastr/SpeedView/wiki/9.-ImageLinearGauge">9. ImageLinearGauge - Wiki</a></td>
    <td><img src="/images/ImageLinearGauge.png"/></td>
    <td>
      <pre>
&lt; com.github.anastr.speedviewlib.ImageLinearGauge
        android:id="@+id/gauge"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:sv_image="@drawable/fire" />
      </pre>
    </td>
  </tr>
</table>

## TODO
* Build start animation.
* Add fuel gauge component.
* Build new custom speedometer.

Your `pull request` is always welcome, please review the **[rules of contribution](https://github.com/anastr/SpeedView/blob/master/CONTRIBUTING.md)** to make a useful change.

# LICENSE
```

Copyright 2016 Anas Altair

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
