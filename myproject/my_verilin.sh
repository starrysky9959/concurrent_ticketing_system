#!/bin/sh
###
 # @Author: starrysky9959 starrysky9651@outlook.com
 # @Date: 2022-11-17 09:44:33
 # @LastEditors: starrysky9959 starrysky9651@outlook.com
 # @LastEditTime: 2022-12-14 01:02:49
 # @Description:  
### 

javac -encoding UTF-8 -cp . ticketingsystem/GenerateHistory.java
java -cp . ticketingsystem/GenerateHistory 4 10000 1 0 0 > history
java -Xss1024m -Xmx400g -jar VeriLin.jar 4 history 1 failedHistory
