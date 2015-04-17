# widgets
A library of useful Android widgets.

#Proguard
 - If you are using `BubbleView` and you use proguard, include this line in your rules
 
 ```
 -keep class * extends com.staticbloc.widgets.BubbleView** {*;}
 ```
