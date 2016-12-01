# SpeedView
Dynamic Speedometer, Gauge for Android. **amazing**, **powerful**, and _multi shape_ :zap: , you can change the color of everything, this Library has also made to build **games**.

`minSdkVersion=11`

Library Size just ~ 19 Kb.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SpeedView-blue.svg?style=true)](https://android-arsenal.com/details/1/4169)
[![API](https://img.shields.io/badge/API-+11-red.svg?style=flat)](#)
[![Bintray](https://img.shields.io/bintray/v/anastr/maven/SpeedView.svg)](https://bintray.com/anastr/maven/SpeedView)

<img src="/images/SpeedView.gif" width="30%" />
<img src="/images/DeluxeSpeedView.gif" width="30%" />
<img src="/images/AwesomeSpeedometer.gif" width="30%" /><br/>
<img src="/images/RaySpeedometer.gif" width="30%" />
<img src="/images/PointerSpeedometer.gif" width="30%" />

# Download

**add this line to** `build.gradle`

```gradle

dependencies {
	    compile 'com.github.anastr:speedviewlib:1.1.0'
}

```

for **maven**

```maven
<dependency>
  <groupId>com.github.anastr</groupId>
  <artifactId>speedviewlib</artifactId>
  <version>1.1.0</version>
  <type>pom</type>
</dependency>
```

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
and also you can see **advanced usage** in [Usage - Wiki](https://github.com/anastr/SpeedView/wiki/Usage).<br>
<img src="/images/usage/StartEndDegree.png" width="40%" />
we have 5 Speedometers : <br>
======================
Name | Screenshot
--- | ---
[1. SpeedView - Wiki](https://github.com/anastr/SpeedView/wiki/1.-SpeedView) | <img src="/images/SpeedView3.png" width="25%" />
[2. DeluxeSpeedView - Wiki](https://github.com/anastr/SpeedView/wiki/2.-DeluxeSpeedView) | <img src="/images/DeluxeSpeedView2.png" width="25%" />
[3. AwesomeSpeedometer - Wiki](https://github.com/anastr/SpeedView/wiki/3.-AwesomeSpeedometer) | <img src="/images/AwesomeSpeedometer.png" width="25%" />
[4. RaySpeedometer - Wiki](https://github.com/anastr/SpeedView/wiki/4.-RaySpeedometer) | <img src="/images/RaySpeedometer.png" width="25%" />
[5. PointerSpeedometer - Wiki](https://github.com/anastr/SpeedView/wiki/5.-PointerSpeedometer) | <img src="/images/PointerSpeedometer.png" width="25%" />

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
