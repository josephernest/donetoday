#!/bin/bash

V="0.0.3"

mkdir releasesFree/DoneTodayFree$V
mkdir releasesPro/DoneTodayPro$V

mv mobile/build/outputs/apk/mobile-free-release.apk       releasesFree/DoneTodayFree$V/
mv mobile/build/outputs/apk/mobile-pro-release.apk        releasesPro/DoneTodayPro$V/

mv mobile/build/outputs/mapping/free/release       releasesFree/DoneTodayFree$V/proguard
mv mobile/build/outputs/mapping/pro/release        releasesPro/DoneTodayPro$V/proguard

rm -f releasesFree/DoneTodayFree$V/proguard/dump.txt
rm -f releasesPro/DoneTodayPro$V/proguard/dump.txt


