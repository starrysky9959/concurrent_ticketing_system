rm ticketingsystem/*.class
###
 # @Author: starrysky9959 starrysky9651@outlook.com
 # @Date: 2022-11-17 20:19:36
 # @LastEditors: starrysky9959 starrysky9651@outlook.com
 # @LastEditTime: 2022-12-15 19:27:13
 # @Description:  
### 
javac -encoding UTF-8 -cp . ticketingsystem/Test.java
# threadNum,  testNum, isSequential(0/1), delay(millionsec), delay(nanosec)
java -Xss1024m -Xmx400g -ea -cp . ticketingsystem/Test