# SpeedView
Dynamic Speedometer UI for Android :zap: , you can change the color of everything.

`minSdkVersion=11`

Library Size ~ 9 Kb.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SpeedView-green.svg?style=true)](https://android-arsenal.com/details/1/4169)

<img src="/images/SpeedView.gif" width="100%" /><br/>

# dependencies 

**add this line to** `build.gradle`

```gradle

dependencies {
	    compile 'com.github.anastr:speedviewlib:1.0.3'
}

```

# Usage
we have two views :

## 1st SpeedView

<img src="/images/SpeedView1.png" width="24%" />
<img src="/images/SpeedView2.png" width="24%" />
<img src="/images/SpeedView3.png" width="24%" />
<img src="/images/SpeedView4.png" width="24%" />

**add SpeedView to your layout**

```xml

<com.github.anastr.speedviewlib.SpeedView
        android:id="@+id/speedView"
        android:layout_width="250dp"
        android:layout_height="wrap_content" />

```

change the **speed** from your code : 

```java

SpeedView speedView = (SpeedView) findViewById(R.id.speedView);

// change speed to 50 Km/h
speedView.speedTo(50);
```

you can change max speed by this line (default : 100)
```java
speedView.setMaxSpeed(220);

/** 
  * see also:
  * speedView.setWithTremble(false);
  * speedView.setIndicatorColor(Color.BLUE);
  * speedView.setHighSpeedColor(Color.RED);
  * .....
  */

```

## 2nd DeluxeSpeedView
SpeedView with Blur Effects.

<img src="/images/DeluxeSpeedView1.png" width="49%" />
<img src="/images/DeluxeSpeedView2.png" width="49%" />

**add SpeedView to your layout**

```xml

<com.github.anastr.speedviewlib.DeluxeSpeedView
        android:id="@+id/deluxeSpeedView"
        android:layout_width="250dp"
        android:layout_height="wrap_content" />

```
the same methods in SpeedView
```java
DeluxeSpeedView deluxeSpeedView = (DeluxeSpeedView) findViewById(R.id.deluxeSpeedView);

deluxeSpeedView.speedTo(50);

deluxeSpeedView.setMaxSpeed(220);
```

special methods for DeluxeSpeedView :
```java
deluxeSpeedView.setWithEffects(false); //def : true

deluxeSpeedView.setSpeedBackgroundColor(Color.YELLOW);
```

# Attributes

and also you can change everything in XML, see this Attributes : 

```xml

app:unit="m/s" <!-- def : Km/h -->
app:maxSpeed="220" <!-- def : 100 -->
app:withTremble="false" <!-- def : true -->
app:withBackgroundCircle="false" <!-- def : true -->
app:speedometerWidth="35dp" <!-- def : 30dp -->
app:indicatorColor="#2b38e6" 
app:centerCircleColor="#b2f941ff" 
app:lowSpeedColor="#58ed21"
app:mediumSpeedColor="#edd029"
app:highSpeedColor="#ec2f33"
app:markColor="#99000000"
app:textColor="#e23900"

```
Attributes just for **DeluxeSpeedView** :

```xml

app:withEffects="false" <!-- def : true -->
app:speedBackgroundColor="#000077"
app:speedTextColor="#000000"

```

## Coming Soon ...
i well try to draw this Speedometer.
if you have any idea, image, template please **open new issue** and give me the image , and i well try to add it to the Library.

<img src="/images/new1.png" width="24%" />
<img src="/images/new2.png" width="24%" />
<img src="/images/new3.png" width="24%" />

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
