# MIDI2G

_Turn musics into movement!_

## About

**MIDI2G** is a G-code generator (a bit like any slicer; see CuraEngine or Slic3r) but rather than consuming STL or
 3MF files, it consumes MIDI files and turn them into a dance that matches the music.
 
 That was initially a joke, but it actually came online!

### The joke behind this project.

The initial goal of **MIDI2G** was for a joke, discover it here:

> I was upgrading my 3D printer firmware when I thought about using Windows XP startup sound. I suggested that to a
> friend, it was definitely a funny idea. So, I started to write this piece of software.
>
> Sadly, Marlin does not support start-up G-code directly into the firmware.

## Installing

Currently, no install script or package is provided. You need it to install it from a distribution Zip or from sources.

Building packages for Microsoft Windows (`.msi`) and Debian-based Linux (`.deb`) is in the roadmap though. Probably
 OS X builds later too.

## Build

Gradle build-system allows building a distribution with only one command:

```
$ ./gradlew assembleDist
```

It will create a Zip and a GZip Tarball in the `build/distribution` directory.

## Usage

No clear usage is defined yet. See next commits...
