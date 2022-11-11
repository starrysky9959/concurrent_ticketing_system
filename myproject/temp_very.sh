#!/bin/sh
###
 # @Author: starrysky9959 starrysky9651@outlook.com
 # @Date: 2022-11-11 14:49:08
 # @LastEditors: starrysky9959 starrysky9651@outlook.com
 # @LastEditTime: 2022-11-11 17:07:32
 # @Description:  
### 
# sh clean.sh
# javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
# threadNum,  testNum, isSequential(0/1), delay(millionsec), delay(nanosec)
# java -cp . ticketingsystem/GenerateHistory 1 1000 1 0 0 > history
# The parameter list of VeriLin is threadNum, historyFile, isPosttime(0/1), failedTrace.
java -Xss1024m -Xmx400g -jar VeriLin.jar 4 history 1 failedHistory
