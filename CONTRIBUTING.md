if you want to contribute to this library, _Please_ take look at these **rules**.

_**First:**_ you must read [Get Starting](https://github.com/anastr/SpeedView/wiki/0.-Get-Started) and [Advanced Usage](https://github.com/anastr/SpeedView/wiki/Usage).

before you start, you have to know what you want to do:
 * [Fix Bugs](#fix-bugs).
 * [improve the Library](#improve-the-Library).
 * [Create New Speedometer](#create-new-speedometer).
 * [Build New Component](#build-new-component).

## Fix Bugs
report abut the bug with explain and Screenshots _if it possible_, and we will discuss about it to solve it as fast as possible.

## improve the Library
add some `methods, classes, interfaces.....` anywhere, **please** Post a _description_ of each new method and variable, use simple english language to explain.

## Create New Speedometer
keep these in your mind when you want to create new Speedometer
* extends `Speedometer` class.
* implement abstract methods.
* Override `onSizeChanged(int w, int h, int oldW, int oldH)` method.
* Override `onDraw(Canvas canvas)` method.
* add default Gauge values in `defaultValues()` method by call super for each, like so:
```java
   super.setBackgroundCircleColor(Color.TRANSPARENT);
```
* add default Speedometer values in `defaultSpeedometerValues()` method by call super for each, like so:
```java
   super.setSpeedometerColor(Color.RED);
   super.setSpeedometerWidth(dpTOpx(40f));
```
* call `updateBackgroundBitmap();` at end of `onSizeChanged` method.
* call `drawTicks(canvas);` inside `updateBackgroundBitmap()` method.
* call `drawSpeedUnitText(canvas);` inside `onDraw` method.
* call `drawIndicator(canvas);` inside `onDraw` method.
* call `drawNotes(canvas);` at end of `onDraw` method.
* add this lines at first of `updateBackgroundBitmap` method:
```java
   Canvas c = createBackgroundBitmapCanvas();
   // draw on c canvas all drawing that doesn't change when speed update.
```

_so_, **your CustomSpeedometer class must be like this**:
```java
/**
 * this Library build By Anas Altair, and this Speedometer added by YOUR_NAME.
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class CustomSpeedometer extends Speedometer {

    // add your Variables Here.

    public CustomSpeedometer(Context context) {
        this(context, null);
    }

    public CustomSpeedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSpeedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void defaultValues() {
        // add default Gauge's values by call super.method like
        // super.setBackgroundCircleColor(Color.TRANSPARENT);
    }

    @Override
    protected void defaultSpeedometerValues() {
        // add default Speedometer's values by call super.method like
        // super.setStartEndDegree(135, 135 + 320);

        // by default there is No Indicator, add indicator by:
        // super.setIndicator(new TriangleIndicator(getContext())
        //        .setIndicatorWidth(dpTOpx(25f))
        //        .setIndicatorColor(0xff00e6e6));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        // update your speedometer here if it depend on size.

        // don't remove this line, and don't move up.
        updateBackgroundBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // backgroundBitmap is Already painted.

        // you can draw what do you want here.

        // you must call this method to draw speed-unit Text.
        // this method must call before drawIndicator(canvas) method.
        drawSpeedUnitText(canvas);
        // you must call this method to draw the indicator.
        // put it wherever you want inside this method.
        drawIndicator(canvas);

        // you can draw what do you want here.

        // don't remove this line, and don't move up.
        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        // don't remove these lines.
        Canvas c = createBackgroundBitmapCanvas();

       // you must call drawTicks(c), but if you wont to draw
       // min and max speed value use this.
       if (getTickNumber() > 0)
           drawTicks(c);
       else
           drawDefMinMaxSpeedPosition(c);
    }

    // add your custom methods here.
}
```
these methods/varibles can help you in your custom Speedometer:

method/varible | description
--- | ---
getSpeedText() | get correct speed as string to **Draw**.
getUnit() | get unit string to **Draw**.
getSize() | return width of SpeedometerRect.
getSizePa() | return width of SpeedometerRect without padding.
getWidthPa() | return View width without padding.
getHeightPa() | return View height without padding.
isSpeedometerTextRightToLeft() | if `true` you should draw unit string to the left of speed Text.
getPadding() | use just this method to get padding.
getDegree() | return correct degree of indicator.
getStartDegree() | start degree where indicator and speedometer start.
getEndDegree() | the end of speedometer, where indicator and speedometer must stop.
getLowSpeedOffset() | return [0f, 1f], where `LowSpeedSection` must stop between **startDegree** and **endDegree** [what is this?](https://github.com/anastr/SpeedView/wiki/Usage#control-division-of-the-speedometer).
getMediumSpeedOffset() | return [0f, 1f], where `MediumSpeedSection` must stop between **startDegree** and **endDegree** [what is this?](https://github.com/anastr/SpeedView/wiki/Usage#control-division-of-the-speedometer).
drawDefMinMaxSpeedPosition (canvas) | use this method in `updateBackgroundBitmap()` method if you want to draw **Min** and **Max** speed text in default position.
drawTicks (canvas) | use this method in `updateBackgroundBitmap()` method to draw [Ticks](https://github.com/anastr/SpeedView/wiki/Usage#ticks).
unitTextPaint | you must use this paint to draw unit text.

and also : `getSpeedometerWidth()`, `getMarkColor()`, `getIndicatorColor()`, `getCenterCircleColor()`, `getLowSpeedColor()`, `getMediumSpeedColor()`, `getHighSpeedColor()`, `getTextColor()`, `getBackgroundCircleColor()`, `getIndicatorWidth()`.

## Build New Component
_**components:**_ are small objects can be drawn on speedometer.

just like [Indicators](https://github.com/anastr/SpeedView/wiki/Indicators) and [Notes](https://github.com/anastr/SpeedView/wiki/Notes), your component must have `draw()` method with `Canvas` parameter.
