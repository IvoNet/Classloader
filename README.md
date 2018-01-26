# Introduction #

Small project to play around with ClassLoaders.
I want to know more about this subject so...

Started with copying the URLClassLoader and now it's time to play...


# Trials #

I'm making the classes as I go by calling them FirstTry / SeccondTry / etc.
These will all have a main with a specific test and posibly a specific setup.

Always read the Class JavaDoc to see what has to be done to get it to work.

## FirstTry ##

Just illustrates the workings of a ClassLoader

## SecondTry ##

Illustrates the workings of the Classloader with a parent Classloader.

## ThirdTry ##

Illustrates the workings of two completely separate Classloaders.
One classloader without parent and therefore without delegation

## Todo / Whishes ##

* Write my own classloader from scratch
* Learn more about java.security.*
* How to direct Parent first / Parent last
* Retrieving bytecode over a network and running it locally by using my own classloader(s).


## Discussion ##

contact through twitter @ivonet

## Some links ##

One can get a realy good document here about classloaders at the link below but you have to register first.

* https://zeroturnaround.com/rebellabs/rebel-labs-tutorial-do-you-really-get-classloaders/4/

A video about classloaders can be found here:

* http://www.infoq.com/presentations/Do-You-Really-Get-classloaders
