#!/bin/sh
###
 # @Author: starrysky9959 starrysky9651@outlook.com
 # @Date: 2022-11-11 14:26:21
 # @LastEditors: starrysky9959 starrysky9651@outlook.com
 # @LastEditTime: 2022-11-11 17:06:58
 # @Description:  
### 
sh clean.sh
javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
javac -encoding UTF-8 -cp . ticketingsystem/Replay.java
# threadNum,  testNum, isSequential(0/1), delay(millionsec), delay(nanosec)
java -cp . ticketingsystem/GenerateHistory 8 10000 0 0 0 > history
# The parameter list of VeriLin is threadNum, historyFile, isPosttime(0/1), failedTrace.
java -cp . ticketingsystem/Replay 8 history 1 failedHistory
