#!/bin/sh
###
 # @Author: starrysky9959 starrysky9651@outlook.com
 # @Date: 2022-11-11 14:26:21
 # @LastEditors: starrysky9959 starrysky9651@outlook.com
 # @LastEditTime: 2022-12-14 00:14:27
 # @Description:  
### 
sh clean.sh
javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
javac -encoding UTF-8 -cp . ticketingsystem/Replay.java
# threadNum,  testNum, isSequential(0/1), delay(millionsec), delay(nanosec)
java -cp . ticketingsystem/GenerateHistory 8 10000 1 0 0 > history

