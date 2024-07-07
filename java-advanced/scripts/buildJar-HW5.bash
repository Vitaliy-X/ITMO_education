#!/bin/bash

find "../java-solutions/info/kgeorgiy/ja/gavriliuk/implementor/" -name "*.java" > sources.txt
javac -cp "../java-advanced-2024/artifacts/*:./java-advanced-2024/lib/*" @sources.txt -d out/
mkdir -p "artifacts"
jar -cfm JarImplementor.jar MANIFEST.MF -C out/ .
find . -name 'sources.txt' -delete